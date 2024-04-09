package cn.zyszero.phoenix.rpc.core.consumer;

import cn.zyszero.phoenix.rpc.core.api.Filter;
import cn.zyszero.phoenix.rpc.core.api.RpcContext;
import cn.zyszero.phoenix.rpc.core.api.RpcRequest;
import cn.zyszero.phoenix.rpc.core.api.RpcResponse;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.util.MethodUtils;
import cn.zyszero.phoenix.rpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
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

    public PhoenixInvocationHandler(Class<?> service, RpcContext context, List<InstanceMeta> providers, HttpInvoker httpInvoker) {
        this.service = service;
        this.context = context;
        this.providers = providers;
        this.httpInvoker = httpInvoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (MethodUtils.checkLocalMethod(method)) {
            return null;
        }

        RpcRequest rpcRequest = new RpcRequest();
        // set service canonical name
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);


        for (Filter filter : this.context.getFilters()) {
            Object preResult = filter.preFilter(rpcRequest);
            if (preResult != null) {
                log.debug(filter.getClass().getName() + " ==> preFilter: " + preResult);
                return preResult;
            }
        }

        List<InstanceMeta> instances = context.getRouter().route(providers);
        InstanceMeta instance = context.getLoadBalancer().choose(instances);
        log.debug("loadBalancer.choose(instance) ==> " + instance);

        RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, instance.toUrl());
        Object result = castReturnResult(method, rpcResponse);


        for (Filter filter : this.context.getFilters()) {
            Object filterResult = filter.postFilter(rpcRequest, rpcResponse, result);
            if (filterResult != null) {
                return filterResult;
            }
        }

        return result;
    }

    private static Object castReturnResult(Method method, RpcResponse<?> rpcResponse) throws Exception {
        if (rpcResponse.isStatues()) {
            return TypeUtils.castMethodResult(method, rpcResponse.getData());
        } else {
            throw rpcResponse.getException();
        }
    }


}
