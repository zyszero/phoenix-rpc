package cn.zyszero.phoenix.rpc.core.provider;

import cn.zyszero.phoenix.rpc.core.api.RpcException;
import cn.zyszero.phoenix.rpc.core.api.RpcRequest;
import cn.zyszero.phoenix.rpc.core.api.RpcResponse;
import cn.zyszero.phoenix.rpc.core.meta.ProviderMeta;
import cn.zyszero.phoenix.rpc.core.util.TypeUtils;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @Author: zyszero
 * @Date: 2024/4/1 20:26
 */
public class ProviderInvoker {

    private final MultiValueMap<String, ProviderMeta> skeleton;

    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.skeleton = providerBootstrap.getSkeleton();
    }

    public RpcResponse<Object> invoke(RpcRequest request) {
        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
        try {
            ProviderMeta meta = findProviderMeta(providerMetas, request.getMethodSign());
            Method method = meta.getMethod();
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes(), method.getGenericParameterTypes());
            Object result = method.invoke(meta.getServiceImpl(), args);
            return new RpcResponse<>(true, result, null);
        } catch (InvocationTargetException e) {
            return new RpcResponse<>(false, null, new RpcException(e.getTargetException().getMessage()));
        } catch (Exception e) {
            return new RpcResponse<>(false, null, new RpcException(e.getMessage()));
        }
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
