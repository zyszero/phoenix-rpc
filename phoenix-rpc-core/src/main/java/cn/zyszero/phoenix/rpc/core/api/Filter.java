package cn.zyszero.phoenix.rpc.core.api;

/**
 * @Author: zyszero
 * @Date: 2024/3/24 19:00
 */
public interface Filter {
    Object preFilter(RpcRequest request);

    Object postFilter(RpcRequest request, RpcResponse response, Object result);


    // Filter next();

    Filter DEFAULT = new Filter() {
        @Override
        public Object preFilter(RpcRequest request) {
            return null;
        }

        @Override
        public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
            return result;
        }
    };
}
