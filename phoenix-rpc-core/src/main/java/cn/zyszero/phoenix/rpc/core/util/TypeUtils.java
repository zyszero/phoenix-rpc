package cn.zyszero.phoenix.rpc.core.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zyszero
 * @Date: 2024/3/16 19:35
 * @Description:
 */
public class TypeUtils {

    public static Object cast(Object origin, Class<?> type) {
        if (origin == null) {
            return null;
        }
        Class<?> originClass = origin.getClass();
        if (type.isAssignableFrom(originClass)) {
            return origin;
        }

        if (type.isArray()) {
            if (origin instanceof List list) {
                origin = list.toArray();
            }
            int length = Array.getLength(origin);
            Class<?> componentType = type.getComponentType();
            Object resultArray = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(resultArray, i, Array.get(origin, i));
            }
            return resultArray;
        }

        if (origin instanceof Map map) {
            JSONObject jsonObject = new JSONObject(map);
            return jsonObject.toJavaObject(type);
        }

        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(origin.toString());
        }

        if (type == long.class || type == Long.class) {
            return Long.parseLong(origin.toString());
        }
        if (type == float.class || type == Float.class) {
            return Float.parseFloat(origin.toString());
        }
        if (type == double.class || type == Double.class) {
            return Double.parseDouble(origin.toString());
        }
        if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(origin.toString());
        }
        if (type == byte.class || type == Byte.class) {
            return Byte.parseByte(origin.toString());
        }
        if (type == char.class || type == Character.class) {
            return origin.toString().charAt(0);
        }
        if (type == short.class || type == Short.class) {
            return Short.parseShort(origin.toString());
        }

        return null;
    }
}
