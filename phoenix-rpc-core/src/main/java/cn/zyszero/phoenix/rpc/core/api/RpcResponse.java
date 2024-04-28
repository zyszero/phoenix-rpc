package cn.zyszero.phoenix.rpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * response data for RPC call
 * @Author: zyszero
 * @create: 2020/12/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse<T> {
    /**
     * 状态: true
     */
    private boolean status;
    /**
     *
     */
    private T data;

    private RpcException exception;
}
