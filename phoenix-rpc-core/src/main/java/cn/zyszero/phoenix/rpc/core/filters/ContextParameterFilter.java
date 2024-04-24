package cn.zyszero.phoenix.rpc.core.filters;


import cn.zyszero.phoenix.rpc.core.api.Filter;
import cn.zyszero.phoenix.rpc.core.api.RpcContext;
import cn.zyszero.phoenix.rpc.core.api.RpcRequest;
import cn.zyszero.phoenix.rpc.core.api.RpcResponse;

import java.util.Map;


/**
 * 处理上下文参数.
 *
 * @Author: zyszero
 * @Date: 2024/4/16 20:49
 */
public class ContextParameterFilter implements Filter {

    @Override
    public Object preFilter(RpcRequest request) {
        Map<String, String> params = RpcContext.ContextParameters.get();
        if (!params.isEmpty()) {
            request.getParams().putAll(params);
        }
        return null;
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        RpcContext.ContextParameters.get().clear();
        return null;
    }
}
