package cn.zyszero.phoenix.rpc.core.consumer;

import cn.zyszero.phoenix.rpc.core.api.LoadBalancer;
import cn.zyszero.phoenix.rpc.core.api.Router;
import cn.zyszero.phoenix.rpc.core.cluster.RandomLoadBalancer;
import cn.zyszero.phoenix.rpc.core.cluster.RoundRibonLoadBalancer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ConsumerConfig {

    @Bean
    public ConsumerBootstrap consumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrapRunner(ConsumerBootstrap consumerBootstrap) {
        return args -> {
            System.out.println("consumerBootstrapRunner starting...");
            consumerBootstrap.start();
            System.out.println("consumerBootstrapRunner started");
        };
    }


    @Bean
    public LoadBalancer loadBalancer() {
        return new RoundRibonLoadBalancer();
    }

    @Bean
    public Router route() {
        return Router.DEFAULT;
    }
}
