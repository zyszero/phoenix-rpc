package cn.zyszero.phoenix.rpc.core.consumer;

import cn.zyszero.phoenix.rpc.core.api.*;
import cn.zyszero.phoenix.rpc.core.consumer.http.OkHttpInvoker;
import cn.zyszero.phoenix.rpc.core.governance.SlidingTimeWindow;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.util.MethodUtils;
import cn.zyszero.phoenix.rpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 消费端的动态代理处理类
 *
 * @Author: zyszero
 * @Date: 2024/4/1 21:10
 */
@Slf4j
public class PhoenixInvocationHandler implements InvocationHandler {

    Class<?> service;

    RpcContext context;

    final List<InstanceMeta> providers;

    List<InstanceMeta> isolatedProviders = new ArrayList<>();

    final List<InstanceMeta> halfOpenProviders = new ArrayList<>();


    HttpInvoker httpInvoker;

    final Map<String, SlidingTimeWindow> windows = new HashMap<>();

    ScheduledExecutorService executor;

    public PhoenixInvocationHandler(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        this.service = service;
        this.context = context;
        this.providers = providers;
        int timeout = context.getConsumerProperties().getTimeout();
        int halfOpenInitialDelay = context.getConsumerProperties().getHalfOpenInitialDelay();
        int halfOpenDelay = context.getConsumerProperties().getHalfOpenDelay();
        this.httpInvoker = new OkHttpInvoker(timeout);
        this.executor = Executors.newScheduledThreadPool(1);
        this.executor.scheduleWithFixedDelay(this::halfOpen, halfOpenInitialDelay, halfOpenDelay, TimeUnit.SECONDS);
    }

    private void halfOpen() {
        log.debug(" ===> halfOpen isolatedProviders: {}", isolatedProviders);
        halfOpenProviders.clear();
        halfOpenProviders.addAll(isolatedProviders);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (MethodUtils.checkLocalMethod(method)) {
            return null;
        }

        RpcRequest rpcRequest = new RpcRequest();
        // set service canonical name
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);

        int retries = context.getConsumerProperties().getRetries();
        int faultLimit = context.getConsumerProperties().getFaultLimit();

        while (retries-- > 0) {

            log.debug(" ===> retries: " + retries);

            try {
                for (Filter filter : this.context.getFilters()) {
                    Object preResult = filter.preFilter(rpcRequest);
                    if (preResult != null) {
                        log.debug(filter.getClass().getName() + " ==> preFilter: {}", preResult);
                        return preResult;
                    }
                }

                InstanceMeta instance;
                synchronized (halfOpenProviders) {
                    if (halfOpenProviders.isEmpty()) {
                        List<InstanceMeta> instances = context.getRouter().route(providers);
                        instance = context.getLoadBalancer().choose(instances);
                        log.debug(" loadBalancer.choose(instance) ==> {}", instance);
                    } else {
                        // 从半开状态中获取一个实例
                        instance = halfOpenProviders.remove(0);
                        log.debug(" check alive instance ==> {}", instance);
                    }
                }


                RpcResponse<?> rpcResponse;
                Object result;
                String url = instance.toUrl();
                try {
                    rpcResponse = httpInvoker.post(rpcRequest, url);
                    result = castReturnResult(method, rpcResponse);
                } catch (Exception ex) {
                    // 故障的规则统计和隔离
                    // 每一次异常，记录一次，统计 30s 内的异常数

                    synchronized (windows) {
                        SlidingTimeWindow window = windows.get(url);
                        if (window == null) {
                            window = new SlidingTimeWindow();
                            windows.put(url, window);
                        }

                        window.record(System.currentTimeMillis());
                        log.debug("instance {} in window with {}", instance, window.getSum());
                        if (window.getSum() >= faultLimit) {
                            isolate(instance);
                        }
                    }

                    throw ex;
                }

                // 探活成功，恢复实例
                synchronized (providers) {
                    if (!providers.contains(instance)) {
                        isolatedProviders.remove(instance);
                        providers.add(instance);
                        log.debug("instance {} recovered, isolatedProviders={}, providers={}", instance, isolatedProviders, providers);
                    }
                }

                for (Filter filter : this.context.getFilters()) {
                    Object filterResult = filter.postFilter(rpcRequest, rpcResponse, result);
                    if (filterResult != null) {
                        return filterResult;
                    }
                }

                return result;
            } catch (Exception ex) {
                if (!(ex.getCause() instanceof SocketTimeoutException)) {
                    throw ex;
                }
            }
        }
        return null;
    }

    private void isolate(InstanceMeta instance) {

        log.debug(" ===> isolate instance: {}", instance);
        providers.remove(instance);
        log.debug(" ===> providers.remove(instance): {}", instance);
        isolatedProviders.add(instance);
        log.debug(" ===> isolatedProviders.add(instance): {}", instance);
    }

    private static Object castReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if (rpcResponse.isStatus()) {
            return TypeUtils.castMethodResult(method, rpcResponse.getData());
        } else {
            RpcException exception = rpcResponse.getException();
            if (exception != null) {
                log.debug("response error: {}", exception.getMessage());
                throw exception;
            }
            return null;
        }
    }


}
