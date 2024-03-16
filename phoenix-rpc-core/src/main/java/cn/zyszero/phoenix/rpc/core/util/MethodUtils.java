package cn.zyszero.phoenix.rpc.core.util;

import java.lang.reflect.Method;
import java.util.Arrays;

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


    public static void main(String[] args) {
        Arrays.stream(MethodUtils.class.getMethods()).forEach(
                method -> System.out.println(MethodUtils.methodSign(method)));
    }
}
