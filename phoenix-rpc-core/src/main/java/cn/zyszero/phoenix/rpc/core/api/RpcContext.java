package cn.zyszero.phoenix.rpc.core.api;

import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.List;

/**
 * @Author: zyszero
 * @Date: 2024/3/24 20:44
 */
@Data
public class RpcContext {

    private List<Filter> filters;

    private Router<InstanceMeta> router;

    private LoadBalancer<InstanceMeta> loadBalancer;
}
