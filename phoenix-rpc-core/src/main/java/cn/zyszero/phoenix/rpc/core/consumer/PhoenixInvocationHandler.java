package cn.zyszero.phoenix.rpc.core.consumer;

import cn.zyszero.phoenix.rpc.core.api.*;
import cn.zyszero.phoenix.rpc.core.util.MethodUtils;
import cn.zyszero.phoenix.rpc.core.util.TypeUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PhoenixInvocationHandler implements InvocationHandler {

    final static MediaType JSON_TYPE = MediaType.parse("application/json");

    Class<?> service;

    RpcContext context;

    List<String> providers;

    public PhoenixInvocationHandler(Class<?> service, RpcContext context, List<String> providers) {
        this.service = service;
        this.context = context;
        this.providers = providers;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (MethodUtils.checkLocalMethod(method)) {
            return null;
        }

        RpcRequest rpcRequest = new RpcRequest();
        // set service canonical name
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);

        List<String> urls = context.getRouter().route(providers);
        String url = (String) context.getLoadBalancer().choose(urls);
        System.out.println("loadBalancer.choose(urls) ==> " + url);
        RpcResponse rpcResponse = post(rpcRequest, url);

        if (rpcResponse.isStatues()) {
            Object data = rpcResponse.getData();
            Class<?> type = method.getReturnType();
            System.out.println("method.getReturnType(): " + type);
            if (data instanceof JSONObject jsonResult) {
                if (Map.class.isAssignableFrom(type)) {
                    Map resultMap = new HashMap<>();
                    Type genericReturnType = method.getGenericReturnType();
                    System.out.println("genericReturnType: " + genericReturnType);
                    if (genericReturnType instanceof ParameterizedType parameterizedType) {
                        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                        System.out.println("actualTypeArguments: " + actualTypeArguments);
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
                        Array.set(resultArray, i, array[i]);
                    }
                    return resultArray;
                } else if (List.class.isAssignableFrom(type)) {
                    List<Object> resultList = new ArrayList<>(array.length);
                    Type genericReturnType = method.getGenericReturnType();
                    System.out.println("genericReturnType: " + genericReturnType);
                    if (genericReturnType instanceof ParameterizedType parameterizedType) {
                        Type actualTypeArgument = parameterizedType.getActualTypeArguments()[0];
                        System.out.println("actualTypeArgument: " + actualTypeArgument);
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
        } else {
//            rpcResponse.getException().printStackTrace();
            throw rpcResponse.getException();
        }
    }

    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build();

    private RpcResponse post(RpcRequest rpcRequest, String url) {
        String reqJson = JSON.toJSONString(rpcRequest);
        System.out.println("reqJson: " + reqJson);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(reqJson, JSON_TYPE))
                .build();
        try {
            String respJson = client.newCall(request)
                    .execute()
                    .body()
                    .string();
            System.out.println("respJson: " + respJson);
            return JSON.parseObject(respJson, RpcResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
