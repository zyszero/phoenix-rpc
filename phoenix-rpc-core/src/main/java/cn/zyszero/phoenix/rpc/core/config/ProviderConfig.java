package cn.zyszero.phoenix.rpc.core.config;

import cn.zyszero.phoenix.rpc.core.api.RegistryCenter;
import cn.zyszero.phoenix.rpc.core.provider.ProviderBootstrap;
import cn.zyszero.phoenix.rpc.core.provider.ProviderInvoker;
import cn.zyszero.phoenix.rpc.core.registry.zk.ZookeeperRegistryCenter;
import cn.zyszero.phoenix.rpc.core.transport.SpringBootTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

@Slf4j
@Configuration
@EnableConfigurationProperties({AppProperties.class, ProviderProperties.class})
@Import({SpringBootTransport.class})
public class ProviderConfig {

    @Value("${server.port:8080}")
    private String port;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private ProviderProperties providerProperties;


    @Bean
    @ConditionalOnMissingBean
    public ApolloChangedListener provider_apolloChangedListener() {
        return new ApolloChangedListener();
    }

    @Bean
    public ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap(port, appProperties, providerProperties);
    }


    @Bean
    public ProviderInvoker providerInvoker(ProviderBootstrap providerBootstrap) {
        return new ProviderInvoker(providerBootstrap);
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerBootstrap_runner(ProviderBootstrap providerBootstrap) {
        return args -> {
            log.info("providerBootstrap starting ...");
            providerBootstrap.start();
            log.info("providerBootstrap started ...");
        };
    }


    @Bean //(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter provider_rc() {
        return new ZookeeperRegistryCenter();
    }
}
