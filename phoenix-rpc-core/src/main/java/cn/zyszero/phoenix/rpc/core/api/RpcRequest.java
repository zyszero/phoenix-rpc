package cn.zyszero.phoenix.rpc.core.api;

import lombok.Data;
import lombok.ToString;

/**
 * @Author: zyszero
 * @create: 2020/12/13
 */
@Data
@ToString
public class RpcRequest {
    /**
     * 接口
     */
    private String service;

    /**
     * 方法签名
     */
    private String methodSign;

    /**
     * 参数
     */
    private Object[] args;
}
