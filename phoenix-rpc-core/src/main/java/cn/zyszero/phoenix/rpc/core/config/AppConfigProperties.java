package cn.zyszero.phoenix.rpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * app config properties.
 * @Author: zyszero
 * @Date: 2024/4/16 23:42
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "phoenix.rpc.app")
public class AppConfigProperties {

    // for app instance
    private String id = "app1";

    private String namespace = "public";

    private String env = "dev";
}
