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

    public static Object cast(Object origin, Class<?> type) {
        log.debug("cast: origin = " + origin);
        log.debug("cast: type = " + type);
        if (origin == null) {
            return null;
        }
        Class<?> originClass = origin.getClass();
        if (type.isAssignableFrom(originClass)) {
            log.debug(" ======> assignable {} -> {}", originClass, type);
            return origin;
        }

        if (type.isArray()) {
            // 如果是 List 转换成数组
            if (origin instanceof List list) {
                origin = list.toArray();
            }
            log.debug(" ======> list/[] -> []/" + type);
            int length = Array.getLength(origin);
            Class<?> componentType = type.getComponentType();
            log.debug(" ======> [] componentType : " + componentType);
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

        if (origin instanceof HashMap map) {
            // 如果是 Map 转换成 JSONObject, 利用 fastjson 进行转换
            log.debug(" ======> map -> " + type);
            JSONObject jsonObject = new JSONObject(map);
            return jsonObject.toJavaObject(type);
        }

        if (origin instanceof JSONObject jsonObject) {
            log.debug(" ======> JSONObject -> " + type);
            return jsonObject.toJavaObject(type);
        }

        log.debug(" ======> Primitive types.");
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

    @Nullable
    public static Object castMethodResult(Method method, Object data) {
        log.debug("castMethodResult: method = " + method);
        log.debug("castMethodResult: data = " + data);
        Class<?> type = method.getReturnType();
        Type genericReturnType = method.getGenericReturnType();
        return castGeneric(data, type, genericReturnType);
    }


    @SuppressWarnings("unchecked")
    public static Object castGeneric(Object data, Class<?> type, Type genericReturnType) {
        log.debug("castGeneric: data = " + data);
        log.debug("method.getReturnType() = " + type);
        log.debug("method.getGenericReturnType() = " + genericReturnType);
        if (data instanceof Map map) { // data是map的情况包括两种，一种是HashMap，一种是JSONObject
            if (Map.class.isAssignableFrom(type)) { // 目标类型是 Map，此时data可能是map也可能是JO
                log.debug(" ======> map -> map");
                Map resultMap = new HashMap<>();
                log.debug(genericReturnType.toString());
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    Class<?> keyType = (Class<?>) actualTypeArguments[0];
                    Class<?> valueType = (Class<?>) actualTypeArguments[1];
                    log.debug("keyType  : " + keyType);
                    log.debug("valueType: " + valueType);
                    map.forEach((k, v) -> resultMap.put(TypeUtils.cast(k, keyType), TypeUtils.cast(v, valueType)));
                }
                return resultMap;
            }
            if (data instanceof JSONObject jsonObject) { // 此时 type 是Pojo，且 data 是JO
                log.debug(" ======> JSONObject -> Pojo");
                return jsonObject.toJavaObject(type);
            } else if (!Map.class.isAssignableFrom(type)) { // 此时 type 是Pojo类型，data 是Map
                log.debug(" ======> map -> Pojo");
                return new JSONObject(map).toJavaObject(type);
            } else {
                log.debug(" ======> map -> ?");
                return data;
            }
        } else if (data instanceof List list) {
            if (type.isArray()) {
                log.debug(" ======> list -> []");
                Object[] array = list.toArray();
                Class<?> componentType = type.getComponentType();
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
                log.debug(" ======> list -> list");
                List<Object> resultList = new ArrayList<>(list.size());
                log.debug("genericReturnType: {}", genericReturnType.toString());
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Type actualType = parameterizedType.getActualTypeArguments()[0];
                    log.debug("actualType: {}", actualType.toString());
                    for (Object o : list) {
                        resultList.add(TypeUtils.cast(o, (Class<?>) actualType));
                    }
                } else {
                    resultList.addAll(list);
                }
                return resultList;
            } else {
                return null;
            }
        } else {
            return cast(data, type);
        }
    }
}
