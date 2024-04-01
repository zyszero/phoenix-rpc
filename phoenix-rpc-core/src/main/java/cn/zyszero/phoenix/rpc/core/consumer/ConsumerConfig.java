package cn.zyszero.phoenix.rpc.core.consumer;

import cn.zyszero.phoenix.rpc.core.api.LoadBalancer;
import cn.zyszero.phoenix.rpc.core.api.RegistryCenter;
import cn.zyszero.phoenix.rpc.core.api.Router;
import cn.zyszero.phoenix.rpc.core.cluster.RoundRibonLoadBalancer;
import cn.zyszero.phoenix.rpc.core.consumer.http.OkHttpInvoker;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.registry.zk.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ConsumerConfig {

    @Value("${phoenix.rpc.providers}")
    private String services;

    @Bean
    public ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrapRunner(ConsumerBootstrap consumerBootstrap) {
        return args -> {
            System.out.println("consumerBootstrapRunner starting ...");
            consumerBootstrap.start();
            System.out.println("consumerBootstrapRunner started ...");
        };
    }


    @Bean
    public LoadBalancer<InstanceMeta> loadBalancer() {
        return new RoundRibonLoadBalancer<>();
    }

    @Bean
    @SuppressWarnings("unchecked")
    public Router<InstanceMeta> route() {
        return Router.DEFAULT;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumer_rc() {
        return new ZookeeperRegistryCenter();
    }

    @Bean
    public HttpInvoker httpInvoker() {
        return new OkHttpInvoker();
    }
}
