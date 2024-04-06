package cn.zyszero.phoenix.rpc.core.util;

import cn.zyszero.phoenix.rpc.core.annotation.PhoenixConsumer;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class MethodUtils {

    public static boolean checkLocalMethod(final String method) {
        return "toString".equals(method) ||
                "hashCode".equals(method) ||
                "equals".equals(method) ||
                "getClass".equals(method) ||
                "notify".equals(method) ||
                "notifyAll".equals(method) ||
                "wait".equals(method);
    }


    public static boolean checkLocalMethod(final Method method) {
        return method.getDeclaringClass().equals(Object.class);
    }

    public static String methodSign(Method method) {
        StringBuilder sb = new StringBuilder(method.getName());
        sb.append("@").append(method.getParameterCount());
        Arrays.stream(method.getParameterTypes()).forEach(
                c -> sb.append("_").append(c.getCanonicalName()));
        return sb.toString();
    }


    public static List<Field> findAnnotatedFields(Class<?> aClass, Class<? extends Annotation> annotationClass) {
        List<Field> result = new ArrayList<>();
        while (aClass != null) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(annotationClass)) {
                    result.add(field);
                }
            }
            aClass = aClass.getSuperclass();
        }
        return result;
    }


    public static void main(String[] args) {
        Arrays.stream(MethodUtils.class.getMethods()).forEach(
                method -> log.debug(MethodUtils.methodSign(method)));
    }
}
