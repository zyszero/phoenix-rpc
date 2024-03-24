package cn.zyszero.phoenix.rpc.core.api;

import lombok.Data;

import java.util.List;

/**
 * @Author: zyszero
 * @Date: 2024/3/24 20:44
 */
@Data
public class RpcContext {

    private List<Filter> filters;


    private Router router;

    private LoadBalancer loadBalancer;
}
