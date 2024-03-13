package cn.zyszero.phoenix.rpc.core.consumer;

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
}
