package cn.zyszero.phoenix.rpc.core.util;

import lombok.SneakyThrows;

import java.lang.reflect.Field;

/**
 * @Author: zyszero
 * @Date: 2024/4/7 7:10
 */
public class MockUtils {
    public static Object mock(Class type) {
        if (type == int.class || type == Integer.class) {
            return 1;
        } else if (type == long.class || type == Long.class) {
            return 100000L;
        } else if (type == float.class || type == Float.class) {
            return 11111.11f;
        } else if (type == double.class || type == Double.class) {
            return 22220.22d;
        } else if (type == boolean.class || type == Boolean.class) {
            return false;
        } else if (type == char.class || type == Character.class) {
            return '\u0000';
        } else if (type == byte.class || type == Byte.class) {
            return (byte) 0;
        } else if (type == short.class || type == Short.class) {
            return (short) 0;
        } else if (type == String.class) {
            return "this_is_a_mock_string";
        }

        if(Number.class.isAssignableFrom(type)) {
            return 1;
        }


        return mockPojo(type);
    }

    @SneakyThrows
    private static Object mockPojo(Class type) {
        Object result = type.getDeclaredConstructor().newInstance();
        Field[] fields = type.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            Class<?> fType = f.getType();
            Object fValue = mock(fType);
            f.set(result, fValue);
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(mock(UserDto.class));
    }

    public static class UserDto{
        private int a;
        private String b;

        @Override
        public String toString() {
            return a + "," + b;
        }
    }
}
