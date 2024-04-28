package cn.zyszero.phoenix.rpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * provider config properties.
 * @Author: zyszero
 * @Date: 2024/4/16 23:44
 */
@Data
@ConfigurationProperties(prefix = "phoenix.rpc.provider")
public class ProviderProperties {

    private Map<String, String> metas = new HashMap<>();

    private String test;

    public void setTest(String test) {
        this.test = test;
    }
}
