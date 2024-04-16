package cn.zyszero.phoenix.rpc.core.provider;

import cn.zyszero.phoenix.rpc.core.annotation.PhoenixProvider;
import cn.zyszero.phoenix.rpc.core.api.RegistryCenter;
import cn.zyszero.phoenix.rpc.core.config.AppConfigProperties;
import cn.zyszero.phoenix.rpc.core.config.ProviderConfigProperties;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.meta.ProviderMeta;
import cn.zyszero.phoenix.rpc.core.meta.ServiceMeta;
import cn.zyszero.phoenix.rpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;

/**
 * 服务提供者的启动类.
 *
 * @Author : zyszero
 * @Date: 2024/3/6 21:30
 */
@Slf4j
@Data
public class ProviderBootstrap implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private RegistryCenter registryCenter;

    private String port;

    private AppConfigProperties appConfigProperties;

    private ProviderConfigProperties providerConfigProperties;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    private InstanceMeta instance;


    public ProviderBootstrap(String port, AppConfigProperties appConfigProperties, ProviderConfigProperties providerConfigProperties) {
        this.port = port;
        this.appConfigProperties = appConfigProperties;
        this.providerConfigProperties = providerConfigProperties;
    }


    @PostConstruct // init-method
    public void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(PhoenixProvider.class);
        registryCenter = applicationContext.getBean(RegistryCenter.class);
        providers.forEach((k, v) -> log.info("provider: " + k + " -> " + v));
        providers.values().forEach(this::generateInterface);
    }

    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        this.instance = InstanceMeta.http(ip, Integer.parseInt(port));
        this.instance.getParameters().putAll(providerConfigProperties.getMetas());
        registryCenter.start();
        skeleton.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        log.info(" ===> unregister all stop. ");
        skeleton.keySet().forEach(this::unregisterService);
        registryCenter.stop();
    }

    private void registerService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service)
                .app(appConfigProperties.getId())
                .namespace(appConfigProperties.getNamespace())
                .env(appConfigProperties.getEnv())
                .build();
        registryCenter.register(serviceMeta, instance);
    }

    private void unregisterService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .name(service)
                .app(appConfigProperties.getId())
                .namespace(appConfigProperties.getNamespace())
                .env(appConfigProperties.getEnv())
                .build();
        registryCenter.unregister(serviceMeta, instance);
    }


    private void generateInterface(Object provider) {
        Arrays.stream(provider.getClass().getInterfaces())
                .forEach(impl -> {
                    Method[] methods = impl.getMethods();
                    for (Method method : methods) {
                        // 过滤掉 Object 的方法
                        if (MethodUtils.checkLocalMethod(method)) {
                            continue;
                        }
                        createProvider(impl, provider, method);
                    }
                });
    }

    private void createProvider(Class<?> impl, Object provider, Method method) {
        ProviderMeta providerMeta = ProviderMeta.builder()
                .method(method)
                .methodSign(MethodUtils.methodSign(method))
                .serviceImpl(provider)
                .build();
        log.info("create provider: " + providerMeta);
        skeleton.add(impl.getCanonicalName(), providerMeta);
    }


}
