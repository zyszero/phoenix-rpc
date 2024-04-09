package cn.zyszero.phoenix.rpc.core.api;

import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: zyszero
 * @Date: 2024/3/24 20:44
 */
@Data
public class RpcContext {

    private List<Filter> filters;

    private Router<InstanceMeta> router;

    private LoadBalancer<InstanceMeta> loadBalancer;

    /**
     * 打标记
     * 比如：机房、单元等
     */
    private Map<String, String> parameters = new HashMap<>();
}
