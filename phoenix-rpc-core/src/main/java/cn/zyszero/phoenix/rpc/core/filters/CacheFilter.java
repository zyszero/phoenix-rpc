package cn.zyszero.phoenix.rpc.core.filters;

import cn.zyszero.phoenix.rpc.core.api.Filter;
import cn.zyszero.phoenix.rpc.core.api.RpcRequest;
import cn.zyszero.phoenix.rpc.core.api.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: zyszero
 * @Date: 2024/4/7 6:29
 */
public class CacheFilter implements Filter {

    // todo 替换成guava cache，加容量和过期时间
    static Map<String, Object> cache = new ConcurrentHashMap<>();

    @Override
    public Object preFilter(RpcRequest request) {
        return cache.get(request.toString());
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        cache.putIfAbsent(request.toString(), result);
        return result;
    }
}
