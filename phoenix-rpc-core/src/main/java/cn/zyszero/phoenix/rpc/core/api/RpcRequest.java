package cn.zyszero.phoenix.rpc.core.api;

import lombok.Data;

/**
 * @Author: zyszero
 * @create: 2020/12/13
 */
@Data
public class RpcRequest {
    /**
     * 接口
     */
    private String service;

    /**
     * 方法
     */
    private String method;

    /**
     * 参数
     */
    private Object[] args;
}
