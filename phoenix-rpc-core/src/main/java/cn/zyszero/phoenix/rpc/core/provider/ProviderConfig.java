package cn.zyszero.phoenix.rpc.core.provider;

import cn.zyszero.phoenix.rpc.core.api.RegistryCenter;
import cn.zyszero.phoenix.rpc.core.registry.zk.ZookeeperRegistryCenter;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ProviderConfig {
    @Bean
    public ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }


    @Bean
    public ProviderInvoker providerInvoker(ProviderBootstrap providerBootstrap) {
        return new ProviderInvoker(providerBootstrap);
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerBootstrap_runner(ProviderBootstrap providerBootstrap) {
        return args -> {
            System.out.println("providerBootstrap starting ...");
            providerBootstrap.start();
            System.out.println("providerBootstrap started ...");
        };
    }


    @Bean //(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter provider_rc() {
        return new ZookeeperRegistryCenter();
    }
}
