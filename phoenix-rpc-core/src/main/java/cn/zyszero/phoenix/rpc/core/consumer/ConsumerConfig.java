package cn.zyszero.phoenix.rpc.core.consumer;

import cn.zyszero.phoenix.rpc.core.api.Filter;
import cn.zyszero.phoenix.rpc.core.api.LoadBalancer;
import cn.zyszero.phoenix.rpc.core.api.RegistryCenter;
import cn.zyszero.phoenix.rpc.core.api.Router;
import cn.zyszero.phoenix.rpc.core.cluster.GrayRouter;
import cn.zyszero.phoenix.rpc.core.cluster.RoundRibonLoadBalancer;
import cn.zyszero.phoenix.rpc.core.filters.ParameterFilter;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.registry.zk.ZookeeperRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Slf4j
@Configuration
public class ConsumerConfig {

    @Value("${app.gray-ratio}")
    private String grayRatio;


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
    @SuppressWarnings("unchecked")
    public Router<InstanceMeta> route() {
        return new GrayRouter(Integer.parseInt(grayRatio));
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumer_rc() {
        return new ZookeeperRegistryCenter();
    }


    @Bean
    public Filter defaultFilter() {
        return new ParameterFilter();
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
