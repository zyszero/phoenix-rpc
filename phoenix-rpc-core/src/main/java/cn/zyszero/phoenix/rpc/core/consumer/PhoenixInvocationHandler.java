package cn.zyszero.phoenix.rpc.core.consumer;

import cn.zyszero.phoenix.rpc.core.api.*;
import cn.zyszero.phoenix.rpc.core.consumer.http.OkHttpInvoker;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.util.MethodUtils;
import cn.zyszero.phoenix.rpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.List;

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

    List<InstanceMeta> providers;

    HttpInvoker httpInvoker;

    public PhoenixInvocationHandler(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        this.service = service;
        this.context = context;
        this.providers = providers;
        int timeout = Integer.parseInt(context.getParameters().getOrDefault("app.timeout", "1000"));
        this.httpInvoker = new OkHttpInvoker(timeout);
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

        int retries = Integer.parseInt(context.getParameters().getOrDefault("app.retries", "2"));
        while (retries-- > 0) {

            log.debug(" ===> retries: " + retries);

            try {
                // [
                for (Filter filter : this.context.getFilters()) {
                    Object preResult = filter.preFilter(rpcRequest);
                    if (preResult != null) {
                        log.debug(filter.getClass().getName() + " ==> preFilter: " + preResult);
                        return preResult;
                    }
                }

                // [[
                List<InstanceMeta> instances = context.getRouter().route(providers);
                InstanceMeta instance = context.getLoadBalancer().choose(instances);
                log.debug("loadBalancer.choose(instance) ==> " + instance);

                RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, instance.toUrl());
                Object result = castReturnResult(method, rpcResponse);

                // ]]

                for (Filter filter : this.context.getFilters()) {
                    Object filterResult = filter.postFilter(rpcRequest, rpcResponse, result);
                    if (filterResult != null) {
                        return filterResult;
                    }
                }
                // ]
                return result;
            } catch (Exception ex) {
                if (!(ex.getCause() instanceof SocketTimeoutException)) {
                    throw ex;
                }
            }
        }
        return null;
    }

    private static Object castReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if (rpcResponse.isStatues()) {
            return TypeUtils.castMethodResult(method, rpcResponse.getData());
        } else {
            Exception exception = rpcResponse.getException();
            if (exception instanceof RpcException ex) {
                throw ex;
            }
            throw new RpcException(exception, RpcException.UNKNOWN_EX);
        }
    }


}
