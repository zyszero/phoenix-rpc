package cn.zyszero.phoenix.rpc.core.consumer;

import cn.zyszero.phoenix.rpc.core.api.RpcRequest;
import cn.zyszero.phoenix.rpc.core.api.RpcResponse;
import cn.zyszero.phoenix.rpc.core.consumer.http.OkHttpInvoker;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: zyszero
 * @Date: 2024/4/1 21:10
 */
public interface HttpInvoker {

    Logger log = LoggerFactory.getLogger(HttpInvoker.class);

    HttpInvoker Default = new OkHttpInvoker(500);

    RpcResponse<?> post(RpcRequest rpcRequest, String url);

    String post(String requestString, String url);

    String get(String url);


    @SneakyThrows
    static <T> T httpGet(String url, Class<T> clazz) {
        log.debug(" =====>>>>>> httpGet: " + url);
        String respJson = Default.get(url);
        log.debug(" =====>>>>>> response: " + respJson);
        return JSON.parseObject(respJson, clazz);
    }


    @SneakyThrows
    static <T> T httpGet(String url, TypeReference<T> typeReference) {
        log.debug(" =====>>>>>> httpGet: " + url);
        String respJson = Default.get(url);
        log.debug(" =====>>>>>> response: " + respJson);
        return JSON.parseObject(respJson, typeReference);
    }

    @SneakyThrows
    static <T> T httpPost(String requestString, String url, Class<T> clazz) {
        log.debug(" =====>>>>>> httpGet: " + url);
        String respJson = Default.post(requestString, url);
        log.debug(" =====>>>>>> response: " + respJson);
        return JSON.parseObject(respJson, clazz);
    }
}
