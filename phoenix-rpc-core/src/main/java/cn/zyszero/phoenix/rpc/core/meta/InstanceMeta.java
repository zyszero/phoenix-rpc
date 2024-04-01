package cn.zyszero.phoenix.rpc.core.meta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author: zyszero
 * @Date: 2024/4/2 0:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceMeta {

    private String scheme;

    private String host;

    private Integer port;

    private String context;

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
     */
    private Map<String, String> parameters;

    public String toPath() {
        return String.format("%s_%d", host, port);
    }


    public static InstanceMeta http(String host, Integer port) {
        return new InstanceMeta("http", host, port, "");
    }


    public String toUrl() {
        return String.format("%s://%s:%d/%s", scheme, host, port, context);
    }
}
