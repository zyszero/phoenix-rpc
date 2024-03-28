package cn.zyszero.phoenix.rpc.core.provider;

import cn.zyszero.phoenix.rpc.core.annotation.PhoenixProvider;
import cn.zyszero.phoenix.rpc.core.api.RpcRequest;
import cn.zyszero.phoenix.rpc.core.api.RpcResponse;
import cn.zyszero.phoenix.rpc.core.meta.ProviderMeta;
import cn.zyszero.phoenix.rpc.core.util.MethodUtils;
import cn.zyszero.phoenix.rpc.core.util.TypeUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Data
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    @PostConstruct // init-method
    public void start() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(PhoenixProvider.class);
        providers.forEach((k, v) -> System.out.println("provider: " + k + " -> " + v));
        providers.values().forEach(this::generateInterface);
    }

    private void generateInterface(Object provider) {
        Arrays.stream(provider.getClass().getInterfaces())
                .forEach(inter -> {
                    Method[] methods = inter.getMethods();
                    for (Method method : methods) {
                        // 过滤掉 Object 的方法
                        if (MethodUtils.checkLocalMethod(method)) {
                            continue;
                        }
                        createProvider(inter, provider, method);
                    }
                });
    }

    private void createProvider(Class<?> i, Object provider, Method method) {
        ProviderMeta meta = new ProviderMeta();
        meta.setMethod(method);
        meta.setMethodSign(MethodUtils.methodSign(method));
        meta.setServiceImpl(provider);
        System.out.println("create provider: " + meta);
        skeleton.add(i.getCanonicalName(), meta);
    }

    public RpcResponse invoke(RpcRequest request) {
        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
        try {
            ProviderMeta meta = findProviderMeta(providerMetas, request.getMethodSign());
            Method method = meta.getMethod();
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes());
            Object result = method.invoke(meta.getServiceImpl(), args);
            return new RpcResponse(true, result, null);
        } catch (InvocationTargetException e) {
            return new RpcResponse(false, null, new RuntimeException(e.getTargetException().getMessage()));
        } catch (Exception e) {
            return new RpcResponse(false, null, new RuntimeException(e.getMessage()));
        }
    }

    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {
        if (args == null || args.length == 0) {
            return args;
        }
        for (int i = 0; i < args.length; i++) {
            args[i] = TypeUtils.cast(args[i], parameterTypes[i]);
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
