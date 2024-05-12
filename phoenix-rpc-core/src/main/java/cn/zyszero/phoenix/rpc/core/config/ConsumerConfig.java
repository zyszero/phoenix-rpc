package cn.zyszero.phoenix.rpc.core.config;

import cn.zyszero.phoenix.rpc.core.api.*;
import cn.zyszero.phoenix.rpc.core.cluster.GrayRouter;
import cn.zyszero.phoenix.rpc.core.cluster.RoundRibonLoadBalancer;
import cn.zyszero.phoenix.rpc.core.consumer.ConsumerBootstrap;
import cn.zyszero.phoenix.rpc.core.filters.ContextParameterFilter;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.registry.phoenix.PhoenixRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Slf4j
@Configuration
@EnableConfigurationProperties({AppProperties.class, ConsumerProperties.class})
public class ConsumerConfig {

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private ConsumerProperties consumerProperties;


    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "apollo.bootstrap", value = "enabled")
    public ApolloChangedListener provider_apolloChangedListener() {
        return new ApolloChangedListener();
    }

    @Bean
    public ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrapRunner(ConsumerBootstrap consumerBootstrap) {
        return args -> {
            log.info("consumerBootstrapRunner starting ...");
            consumerBootstrap.start();
            log.info("consumerBootstrapRunner started ...");
        };
    }


    @Bean
    public LoadBalancer<InstanceMeta> loadBalancer() {
        return new RoundRibonLoadBalancer<>();
    }

    @Bean
    public Router<InstanceMeta> route() {
        return new GrayRouter(consumerProperties.getGrayRatio());
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumer_rc() {
//        return new ZookeeperRegistryCenter();
        return new PhoenixRegistryCenter();
    }


    @Bean
    public Filter defaultFilter() {
        return new ContextParameterFilter();
    }


    @Bean
    @RefreshScope // context.refresh
    RpcContext createRpcContext(Router<InstanceMeta> router,
                                LoadBalancer<InstanceMeta> loadBalancer,
                                List<Filter> filters) {

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadBalancer(loadBalancer);
        context.setFilters(filters);
        context.getParameters().put("app.id", appProperties.getId());
        context.getParameters().put("app.namespace", appProperties.getNamespace());
        context.getParameters().put("app.env", appProperties.getEnv());
        context.setConsumerProperties(consumerProperties);
        return context;
    }

//    @Bean
//    public Filter filter() {
//        return new CacheFilter();
//    }

//    @Bean
//    public Filter mockFilter() {
//        return new MockFilter();
//    }
}
