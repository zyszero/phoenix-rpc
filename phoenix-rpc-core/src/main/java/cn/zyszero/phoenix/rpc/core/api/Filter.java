package cn.zyszero.phoenix.rpc.core.api;

/**
 * @Author: zyszero
 * @Date: 2024/3/24 19:00
 */
public interface Filter {
    Object preFilter(RpcRequest request);

    Object postFilter(RpcRequest request, RpcResponse response, Object result);


    // Filter next();

    // 微服务调用链路排查：
    // A -> B -> C 有问题的
    // - -> - -> D 还有问题
    // - -> D  Mock

    Filter DEFAULT = new Filter() {
        @Override
        public Object preFilter(RpcRequest request) {
            return null;
        }

        @Override
        public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
            return null;
        }
    };
}
