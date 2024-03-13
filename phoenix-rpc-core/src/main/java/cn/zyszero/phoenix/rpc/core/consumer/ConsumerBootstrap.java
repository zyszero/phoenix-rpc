package cn.zyszero.phoenix.rpc.core.consumer;

import cn.zyszero.phoenix.rpc.core.annotation.PhoenixConsumer;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ConsumerBootstrap implements ApplicationContextAware {

    ApplicationContext applicationContext;


    private Map<String, Object> stub = new HashMap<>();


    public void start() {
        // 1. 获取所有的 bean definition name
        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            // 2 找到所有的带有 @PhoenixConsumer 注解的字段
            List<Field> fields = findAnnotatedFields(bean.getClass());

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
                            consumer = createConsumer(service);
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

    private Object createConsumer(Class<?> service) {
        // JDK 动态代理
        return Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new PhoenixInvocationHandler(service));
    }

    private List<Field> findAnnotatedFields(Class<?> aClass) {
        List<Field> result = new ArrayList<>();
        while (aClass != null) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(PhoenixConsumer.class)) {
                    result.add(field);
                }
            }
            aClass = aClass.getSuperclass();
        }
        return result;
    }
}
