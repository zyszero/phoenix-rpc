package cn.zyszero.phoenix.rpc.core.provider;

import cn.zyszero.phoenix.rpc.core.annotation.PhoenixProvider;
import cn.zyszero.phoenix.rpc.core.api.RegistryCenter;
import cn.zyszero.phoenix.rpc.core.api.RpcRequest;
import cn.zyszero.phoenix.rpc.core.api.RpcResponse;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.meta.ProviderMeta;
import cn.zyszero.phoenix.rpc.core.meta.ServiceMeta;
import cn.zyszero.phoenix.rpc.core.util.MethodUtils;
import cn.zyszero.phoenix.rpc.core.util.TypeUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 服务提供者的启动类.
 *
 * @Author : zyszero
 * @Date: 2024/3/6 21:30
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    private InstanceMeta instance;

    @Value("${server.port}")
    private String port;

    @Value("${app.id}")
    private String app;

    @Value("${app.namespace}")
    private String namespace;

    @Value("${app.env}")
    private String env;


    private RegistryCenter registryCenter;

    @PostConstruct // init-method
    public void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(PhoenixProvider.class);
        providers.forEach((k, v) -> System.out.println("provider: " + k + " -> " + v));
        providers.values().forEach(this::generateInterface);
        registryCenter = applicationContext.getBean(RegistryCenter.class);
    }

    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        this.instance = InstanceMeta.http(ip, Integer.parseInt(port));
        registryCenter.start();
        skeleton.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        System.out.println(" ===> unregister all stop. ");
        skeleton.keySet().forEach(this::unregisterService);
        registryCenter.stop();
    }

    private void registerService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service)
                .app(app)
                .namespace(namespace)
                .env(env)
                .build();
        registryCenter.register(serviceMeta, instance);
    }

    private void unregisterService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service)
                .app(app)
                .namespace(namespace)
                .env(env)
                .build();
        registryCenter.unregister(serviceMeta, instance);
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


}
