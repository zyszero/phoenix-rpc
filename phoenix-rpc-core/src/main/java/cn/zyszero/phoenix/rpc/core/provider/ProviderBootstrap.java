package cn.zyszero.phoenix.rpc.core.provider;

import cn.zyszero.phoenix.rpc.core.annotation.PhoenixProvider;
import cn.zyszero.phoenix.rpc.core.api.RpcRequest;
import cn.zyszero.phoenix.rpc.core.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Data
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;


    @PostConstruct
    public void buildProviders() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(PhoenixProvider.class);
        providers.forEach((k, v) -> System.out.println("provider: " + k + " -> " + v));
        providers.values().forEach(provider -> {
            Class<?>[] interfaces = provider.getClass().getInterfaces();
            for (Class<?> i : interfaces) {
                skeletonMap.put(i.getName(), provider);
            }
        });
    }

    public RpcResponse invoke(RpcRequest request) {
        Object bean = skeletonMap.get(request.getService());
        try {
//            Method method = bean.getClass().getMethod(request.getMethod());
            Method method = findMethod(bean.getClass(), request.getMethod());
            Object result = method.invoke(bean, request.getArgs());
            return new RpcResponse(true, result, null);
        } catch (InvocationTargetException e) {
            return new RpcResponse(false, null, new RuntimeException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            return new RpcResponse(false, null, new RuntimeException(e.getMessage()));
        }
    }

    private Method findMethod(Class<?> aClass, String methodName) {
        for (Method method : aClass.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }


    private Map<String, Object> skeletonMap = new HashMap<>();


}
