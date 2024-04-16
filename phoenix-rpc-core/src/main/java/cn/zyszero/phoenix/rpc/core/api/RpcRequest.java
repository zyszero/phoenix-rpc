package cn.zyszero.phoenix.rpc.core.api;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

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

    /**
     * 跨调用方需要传递的参数
     */
    private Map<String, String> params = new HashMap<>();
}
