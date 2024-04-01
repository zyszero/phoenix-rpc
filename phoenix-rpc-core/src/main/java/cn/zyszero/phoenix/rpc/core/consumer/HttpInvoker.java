package cn.zyszero.phoenix.rpc.core.consumer;

import cn.zyszero.phoenix.rpc.core.api.RpcRequest;
import cn.zyszero.phoenix.rpc.core.api.RpcResponse;

/**
 * @Author: zyszero
 * @Date: 2024/4/1 21:10
 */
public interface HttpInvoker {
    RpcResponse<?> post(RpcRequest rpcRequest, String url);
}
