package cn.zyszero.phoenix.rpc.core.provider;

import cn.zyszero.phoenix.rpc.core.api.RegistryCenter;
import cn.zyszero.phoenix.rpc.core.registry.ZookeeperRegistryCenter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProviderConfig {
    @Bean
    public ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }


    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter provider_rc() {
        return new ZookeeperRegistryCenter();
    }
}
