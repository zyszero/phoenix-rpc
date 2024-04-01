package cn.zyszero.phoenix.rpc.core.consumer;

import cn.zyszero.phoenix.rpc.core.annotation.PhoenixConsumer;
import cn.zyszero.phoenix.rpc.core.api.LoadBalancer;
import cn.zyszero.phoenix.rpc.core.api.RegistryCenter;
import cn.zyszero.phoenix.rpc.core.api.Router;
import cn.zyszero.phoenix.rpc.core.api.RpcContext;
import cn.zyszero.phoenix.rpc.core.util.MethodUtils;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 消费端的启动类.
 *
 * @Author : zyszero
 * @Date: 2024/4/1 21:10
 */
@Data
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    ApplicationContext applicationContext;

    Environment environment;

    private Map<String, Object> stub = new HashMap<>();


    public void start() {

        Router router = applicationContext.getBean(Router.class);
        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);
        RegistryCenter registryCenter = applicationContext.getBean(RegistryCenter.class);
        HttpInvoker httpInvoker = applicationContext.getBean(HttpInvoker.class);

        RpcContext rpcContext = new RpcContext();
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
                try {
                    Class<?> service = field.getType();
                    String serviceName = service.getCanonicalName();
                    Object consumer = stub.get(serviceName);
                    if (consumer == null) {
                        // 3.1 生成代理对象
                        if (consumer == null) {
                            // 3.1 生成代理对象
                            consumer = createConsumerFromRegistry(service, rpcContext, registryCenter, httpInvoker);
                        }
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
        List<String> providers = mapUrls(registryCenter.fetchAll(serviceName));
        System.out.println(" ===> map to providers: " + providers);
        providers.forEach(System.out::println);

        registryCenter.subscribe(serviceName, event -> {
            providers.clear();
            providers.addAll(mapUrls(event.getData()));
        });


        return createConsumer(service, context, providers, httpInvoker);
    }

    private List<String> mapUrls(List<String> nodes) {
        return nodes.stream()
                .map(node -> "http://" + node.replace("_", ":"))
                .collect(Collectors.toList());
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<String> providers, HttpInvoker httpInvoker) {
        // JDK 动态代理
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service},
                new PhoenixInvocationHandler(service, context, providers, httpInvoker));
    }


}
