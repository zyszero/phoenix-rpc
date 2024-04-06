package cn.zyszero.phoenix.rpc.core.util;

import cn.zyszero.phoenix.rpc.core.api.RpcResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @Author: zyszero
 * @Date: 2024/3/16 19:35
 * @Description:
 */
@Slf4j
public class TypeUtils {

    @Nullable
    public static Object castMethodResult(Method method, Object data) {
        Class<?> type = method.getReturnType();
        log.debug("method.getReturnType(): " + type);
        if (data instanceof JSONObject jsonResult) {
            if (Map.class.isAssignableFrom(type)) {
                Map resultMap = new HashMap<>();
                Type genericReturnType = method.getGenericReturnType();
                log.debug("genericReturnType: " + genericReturnType);
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    log.debug("actualTypeArguments: " + actualTypeArguments);
                    Class<?> keyType = (Class<?>) actualTypeArguments[0];
                    Class<?> valueType = (Class<?>) actualTypeArguments[1];
                    for (Map.Entry<String, Object> entry : jsonResult.entrySet()) {
                        resultMap.put(TypeUtils.cast(entry.getKey(), keyType), TypeUtils.cast(entry.getValue(), valueType));
                    }
                }
                return resultMap;
            }
            return ((JSONObject) data).toJavaObject(method.getReturnType());
        } else if (data instanceof JSONArray jsonArray) {
            Object[] array = jsonArray.toArray();
            if (type.isArray()) {
                Class<?> componentType = method.getReturnType().getComponentType();
                Object resultArray = Array.newInstance(componentType, array.length);
                for (int i = 0; i < array.length; i++) {
                    if (componentType.isPrimitive() || componentType.getPackageName().startsWith("java")) {
                        Array.set(resultArray, i, array[i]);
                    } else {
                        Object castObject = TypeUtils.cast(array[i], componentType);
                        Array.set(resultArray, i, castObject);
                    }
                }
                return resultArray;
            } else if (List.class.isAssignableFrom(type)) {
                List<Object> resultList = new ArrayList<>(array.length);
                Type genericReturnType = method.getGenericReturnType();
                log.debug("genericReturnType: " + genericReturnType);
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
                    log.debug("actualTypeArgument: " + actualTypeArgument);
                    for (Object o : array) {
                        resultList.add(TypeUtils.cast(o, (Class<?>) actualTypeArgument));
                    }
                } else {
                    resultList.addAll(Arrays.asList(array));
                }
                return resultList;
            } else {
                return null;
            }
        } else {
            return TypeUtils.cast(data, method.getReturnType());
        }
    }

    public static Object cast(Object origin, Class<?> type) {
        if (origin == null) {
            return null;
        }
        Class<?> originClass = origin.getClass();
        if (type.isAssignableFrom(originClass)) {
            return origin;
        }

        if (type.isArray()) {
            // 如果是 List 转换成数组
            if (origin instanceof List list) {
                origin = list.toArray();
            }
            int length = Array.getLength(origin);
            Class<?> componentType = type.getComponentType();
            Object resultArray = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                if (componentType.isPrimitive() || componentType.getPackageName().startsWith("java")) {
                    Array.set(resultArray, i, Array.get(origin, i));
                } else {
                    Object castObject = cast(Array.get(origin, i), componentType);
                    Array.set(resultArray, i, castObject);
                }
            }
            return resultArray;
        }

        if (origin instanceof Map map) {
            // 如果是 Map 转换成 JSONObject, 利用 fastjson 进行转换
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
