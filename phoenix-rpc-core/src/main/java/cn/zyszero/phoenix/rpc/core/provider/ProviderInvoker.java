package cn.zyszero.phoenix.rpc.core.provider;

import cn.zyszero.phoenix.rpc.core.api.RpcContext;
import cn.zyszero.phoenix.rpc.core.api.RpcException;
import cn.zyszero.phoenix.rpc.core.api.RpcRequest;
import cn.zyszero.phoenix.rpc.core.api.RpcResponse;
import cn.zyszero.phoenix.rpc.core.config.ProviderProperties;
import cn.zyszero.phoenix.rpc.core.governance.SlidingTimeWindow;
import cn.zyszero.phoenix.rpc.core.meta.ProviderMeta;
import cn.zyszero.phoenix.rpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.zyszero.phoenix.rpc.core.api.RpcException.EXCEED_LIMIT_EX;

/**
 * invoke the service methods in provider.
 *
 * @Author: zyszero
 * @Date: 2024/4/1 20:26
 */
@Slf4j
public class ProviderInvoker {

    private final MultiValueMap<String, ProviderMeta> skeleton;

    final Map<String, SlidingTimeWindow> windows = new HashMap<>();


    final ProviderProperties providerProperties;

    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.skeleton = providerBootstrap.getSkeleton();
        this.providerProperties = providerBootstrap.getProviderProperties();
    }

    public RpcResponse<Object> invoke(RpcRequest request) {
        log.debug(" ===> ProviderInvoker.invoke(request:{})", request);
        if (!request.getParams().isEmpty()) {
            request.getParams().forEach(RpcContext::setContextParameter);
        }

        // 使用滑动窗口实现服务端限流，即流控
        String service = request.getService();
        synchronized (windows) {
            SlidingTimeWindow window = windows.computeIfAbsent(service, k -> new SlidingTimeWindow());
            int trafficControl = Integer.parseInt(providerProperties.getMetas().getOrDefault("tc", "20"));
            log.debug(" ===>> trafficControl:{} for {}", trafficControl, service);
            if (window.calcSum() > trafficControl) {
                System.out.println(window);
                throw new RpcException("service " + service + " invoked in 30s/[" +
                        window.getSum() + "] larger than tpsLimit = " + trafficControl, EXCEED_LIMIT_EX);
            }

            window.record(System.currentTimeMillis());
            log.debug("service {} in window with {}", service, window);
        }

        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
        RpcResponse<Object> rpcResponse;
        try {
            ProviderMeta meta = findProviderMeta(providerMetas, request.getMethodSign());
            Method method = meta.getMethod();
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes(), method.getGenericParameterTypes());
            Object result = method.invoke(meta.getServiceImpl(), args);
            rpcResponse = new RpcResponse<>(true, result, null);
            return rpcResponse;
        } catch (InvocationTargetException e) {
            rpcResponse = new RpcResponse<>(false, null, new RpcException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException | IllegalArgumentException e) {
            rpcResponse = new RpcResponse<>(false, null, new RpcException(e.getMessage()));
        } finally {
            RpcContext.ContextParameters.get().clear(); // 防止内存泄露和上下文污染
        }
        log.debug(" ===> ProviderInvoker.invoke() = {}", rpcResponse);
        return rpcResponse;
    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes, Type[] genericParameterTypes) {
        if (args == null || args.length == 0) {
            return args;
        }
        for (int i = 0; i < args.length; i++) {
            args[i] = TypeUtils.castGeneric(args[i], parameterTypes[i], genericParameterTypes[i]);
        }
        return args;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        return providerMetas.stream()
                .filter(meta -> meta.getMethodSign().equals(methodSign))
                .findFirst()
                .orElse(null);
    }
}
