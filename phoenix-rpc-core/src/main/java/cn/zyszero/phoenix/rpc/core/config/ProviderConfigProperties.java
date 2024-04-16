package cn.zyszero.phoenix.rpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * provider config properties.
 * @Author: zyszero
 * @Date: 2024/4/16 23:44
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "phoenix.rpc.provider")
public class ProviderConfigProperties {

    private Map<String, String> metas = new HashMap<>();
}
