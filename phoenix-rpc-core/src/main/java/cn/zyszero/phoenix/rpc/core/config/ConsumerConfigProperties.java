package cn.zyszero.phoenix.rpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: zyszero
 * @Date: 2024/4/16 23:45
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "phoenix.rpc.consumer")
public class ConsumerConfigProperties {
    // for ha and governance
    private int retries = 1;

    private int timeout = 1000;

    private int faultLimit = 10;

    private int halfOpenInitialDelay = 10_000;

    private int halfOpenDelay = 60_000;

    private int grayRatio = 0;
}
