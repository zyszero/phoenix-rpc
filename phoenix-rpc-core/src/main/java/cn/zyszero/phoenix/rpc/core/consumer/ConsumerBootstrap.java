package cn.zyszero.phoenix.rpc.core.consumer;

import cn.zyszero.phoenix.rpc.core.annotation.PhoenixConsumer;
import cn.zyszero.phoenix.rpc.core.api.*;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.meta.ServiceMeta;
import cn.zyszero.phoenix.rpc.core.util.MethodUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 消费端的启动类.
 *
 * @Author : zyszero
 * @Date: 2024/4/1 21:10
 */
@Data
@Slf4j
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    ApplicationContext applicationContext;

    Environment environment;

    private Map<String, Object> stub = new HashMap<>();


    @Value("${app.id}")
    private String app;

    @Value("${app.namespace}")
    private String namespace;

    @Value("${app.env}")
    private String env;


    public void start() {

        Router<InstanceMeta> router = applicationContext.getBean(Router.class);
        LoadBalancer<InstanceMeta> loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        HttpInvoker httpInvoker = applicationContext.getBean(HttpInvoker.class);
        List<Filter> filters = applicationContext.getBeansOfType(Filter.class).values().stream().toList();


        RpcContext rpcContext = new RpcContext();
        rpcContext.setFilters(filters);
        rpcContext.setRouter(router);
        rpcContext.setLoadBalancer(loadBalancer);

        // 1. 获取所有的 bean definition name
        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            // 2 找到所有的带有 @PhoenixConsumer 注解的字段
            List<Field> fields = MethodUtils.findAnnotatedFields(bean.getClass(), PhoenixConsumer.class);

            // 3. 生成代理对象
            fields.forEach(field -> {
                log.info(" ===> field: " + field.getName());
                try {
                    Class<?> service = field.getType();
                    String serviceName = service.getCanonicalName();
                    Object consumer = stub.get(serviceName);
                    if (consumer == null) {
                        // 3.1 生成代理对象
                        consumer = createConsumerFromRegistry(service, rpcContext, registryCenter, httpInvoker);
                        stub.put(serviceName, consumer);
                        field.setAccessible(true);
                        field.set(bean, consumer);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private Object createConsumerFromRegistry(Class<?> service, RpcContext context, RegistryCenter registryCenter, HttpInvoker httpInvoker) {
        String serviceName = service.getCanonicalName();
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app)
                .namespace(namespace)
                .env(env)
                .name(serviceName)
                .build();
        List<InstanceMeta> providers = registryCenter.fetchAll(serviceMeta);
        log.info(" ===> map to providers: " + providers);
        providers.forEach(System.out::println);

        registryCenter.subscribe(serviceMeta, event -> {
            providers.clear();
            providers.addAll(event.getData());
        });


        return createConsumer(service, context, providers, httpInvoker);
    }


    private Object createConsumer(Class<?> service, RpcContext context, List<InstanceMeta> providers, HttpInvoker httpInvoker) {
        // JDK 动态代理
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service},
                new PhoenixInvocationHandler(service, context, providers, httpInvoker));
    }


}
