package cn.zyszero.phoenix.rpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
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
    private boolean statues;

    /**
     *
     */
    private T data;
}
