package cn.zys.phoenix.rpc.demo.consumer;

import cn.zyszero.phoenix.rpc.core.annotation.PhoenixConsumer;
import cn.zyszero.phoenix.rpc.core.consumer.ConsumerConfig;
import cn.zyszero.phoenix.rpc.demo.api.Order;
import cn.zyszero.phoenix.rpc.demo.api.OrderService;
import cn.zyszero.phoenix.rpc.demo.api.User;
import cn.zyszero.phoenix.rpc.demo.api.UserService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ConsumerConfig.class})
public class PhoenixRpcDemoConsumerApplication {

    @PhoenixConsumer
    UserService userService;

    @PhoenixConsumer
    OrderService orderService;

    public static void main(String[] args) {
        SpringApplication.run(PhoenixRpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumerRunner() {
        return args -> {
            User user = userService.findById(1);
            System.out.println("RPC result userService.findById(1) = " + user);


            User user1 = userService.findById(2, "zys");
            System.out.println("RPC result userService.findById(2, \"zys\") = " + user1);


//            int id = userService.getId(101);
//            System.out.println("id: " + id);

            String name = userService.getName("zys");
            System.out.println("RPC result userService.getName(\"zys\") = " + name);


            String name1 = userService.getName(123);
            System.out.println("RPC result userService.getName(1) = " + name1);


//            Order order = orderService.findById(2);
//            System.out.printf("order: %s\n", order);

//            Order order404 = orderService.findById(404);
//            System.out.printf("order404: %s\n", order404);
        };
    }
}
