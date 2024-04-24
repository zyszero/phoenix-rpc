package cn.zyszero.phoenix.rpc.core.api;

import cn.zyszero.phoenix.rpc.core.config.ConsumerProperties;
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


    private ConsumerProperties consumerProperties;


    public String param(String key) {
        return parameters.get(key);
    }

    // phoenix.rpc.color = gray
    // phoenix.rpc.gtrace_id
    // gw -> service1 ->  service2(跨线程传递) ...
    // http headers
    public static ThreadLocal<Map<String,String>> ContextParameters = ThreadLocal.withInitial(HashMap::new);


    public static void setContextParameter(String key, String value) {
        ContextParameters.get().put(key, value);
    }

    public static String getContextParameter(String key) {
        return ContextParameters.get().get(key);
    }

    public static void removeContextParameter(String key) {
        ContextParameters.get().remove(key);
    }
}
