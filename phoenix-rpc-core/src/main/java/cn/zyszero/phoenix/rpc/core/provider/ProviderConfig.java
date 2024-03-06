package cn.zyszero.phoenix.rpc.core.provider;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProviderConfig {
    @Bean
    public ProviderBootstrap providerBootstrap(){
        return new ProviderBootstrap();
    }
}
