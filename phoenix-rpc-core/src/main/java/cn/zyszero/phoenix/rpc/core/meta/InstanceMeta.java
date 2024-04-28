package cn.zyszero.phoenix.rpc.core.meta;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述服务实例的元数据。
 *
 * @Author: zyszero
 * @Date: 2024/4/2 0:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"scheme", "host", "port", "context"})
public class InstanceMeta {

    private String scheme;

    private String host;

    private Integer port;

    private String context; // dubbo url?k1=v1

    private boolean status; // online or offline

    public InstanceMeta(String scheme, String host, Integer port, String context) {
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    /**
     * 打标记
     * 比如：机房、单元等
     * idc A B C
     */
    private Map<String, String> parameters = new HashMap<>();

    public String toPath() {
        return String.format("%s_%d", host, port);
    }


    public static InstanceMeta http(String host, Integer port) {
        return new InstanceMeta("http", host, port, "phoenix-rpc");
    }


    public String toUrl() {
        return String.format("%s://%s:%d/%s", scheme, host, port, context);
    }

    public String toMetas() {
        return JSON.toJSONString(this.parameters);

    }
}
