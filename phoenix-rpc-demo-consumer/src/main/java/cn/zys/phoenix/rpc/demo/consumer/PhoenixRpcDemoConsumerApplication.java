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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLOutput;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@Import({ConsumerConfig.class})
@RestController
public class PhoenixRpcDemoConsumerApplication {

    @PhoenixConsumer
    UserService userService;

    @PhoenixConsumer
    OrderService orderService;

    @GetMapping("/user")
    public User findBy(int id) {
        return userService.findById(id);
    }

    public static void main(String[] args) {
        SpringApplication.run(PhoenixRpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumerRunner() {
        return args -> {
            // 常规int类型，返回User对象
            System.out.println("Case 1. >>===[常规int类型，返回User对象]===");
            User user = userService.findById(1);
            System.out.println("RPC result userService.findById(1) = " + user);

            // 测试方法重载，同名方法，参数不同
            System.out.println("Case 2. >>===[测试方法重载，同名方法，参数不同===");
            User user1 = userService.findById(2, "zys");
            System.out.println("RPC result userService.findById(2, \"zys\") = " + user1);

            // 测试返回字符串
            System.out.println("Case 3. >>===[测试返回字符串]===");
            String name = userService.getName("zys");
            System.out.println("RPC result userService.getName(\"zys\") = " + name);

            // 测试重载方法返回字符串
            System.out.println("Case 4. >>===[测试重载方法返回字符串]===");
            String name1 = userService.getName(123);
            System.out.println("RPC result userService.getName(1) = " + name1);

            // 测试local toString方法
            System.out.println("Case 5. >>===[测试local toString方法]===");
            System.out.println("RPC result userService.toString() = " + userService.toString());

            // 测试long类型
            System.out.println("Case 6. >>===[常规int类型，返回long 类型]===");
            System.out.println("RPC result userService.getId(1024) = " + userService.getId(1024));

            // 测试long+float类型
            System.out.println("Case 7. >>===[测试long+float类型]===");
            System.out.println("RPC result userService.getId(1024.0f) = " + userService.getId(1024.0f));

            // 测试参数是User类型
            System.out.println("Case 8. >>===[测试参数是User类型]===");
            System.out.println("RPC result userService.getId(new User(1024, \"zys\")) = " + userService.getId(new User(1024, "zys")));

            // 测试返回int[]
            System.out.println("Case 9. >>===[测试参数和返回值都是int[]]===");
            System.out.println("RPC result userService.getIds(): ");
            for (int id : userService.getIds(new int[]{4, 5, 6})) {
                System.out.println(" ===> " + id);
            }

            System.out.println("Case 10. >>===[测试返回long[]]===");
            System.out.println("RPC result userService.getLongIds(): ");
            for (long id : userService.getLongIds()) {
                System.out.println(" ===> " + id);
            }

            // 测试参数和返回值都是List类型
            System.out.println("Case 11. >>===[测试参数和返回值都是List类型]===");
            System.out.println("RPC result userService.getList(list): ");
            List<User> userList = userService.getList(List.of(
                    new User(110, "zys110"),
                    new User(111, "zys111")));
            userList.forEach(System.out::println);

            // 测试参数和返回值都是Map类型
            System.out.println("Case 12. >>===[测试参数和返回值都是Map类型]===");
            System.out.println("RPC result userService.getMap(map): ");
            userService.getMap(
                            Map.of("A200", new User(200, "zys200"),
                                    "A201", new User(201, "zys201")))
                    .forEach((k, v) -> System.out.println(" ===> " + k + " : " + v));

            // 测试返回Boolean
            System.out.println("Case 13. >>===[测试参数和返回值都是Boolean/boolean类型]===");
            System.out.println("RPC result userService.getFlag(true) = " + userService.getFlag(true));

//            Order order = orderService.findById(2);
//            System.out.printf("order: %s\n", order);
//
//            Order order404 = orderService.findById(404);
//            System.out.printf("order404: %s\n", order404);
        };
    }
}
