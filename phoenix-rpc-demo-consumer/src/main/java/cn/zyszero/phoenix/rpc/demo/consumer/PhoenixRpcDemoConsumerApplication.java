package cn.zyszero.phoenix.rpc.demo.consumer;

import cn.zyszero.phoenix.rpc.core.annotation.PhoenixConsumer;
import cn.zyszero.phoenix.rpc.core.api.Router;
import cn.zyszero.phoenix.rpc.core.api.RpcContext;
import cn.zyszero.phoenix.rpc.core.cluster.GrayRouter;
import cn.zyszero.phoenix.rpc.core.config.ConsumerConfig;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.demo.api.User;
import cn.zyszero.phoenix.rpc.demo.api.UserService;
import io.github.zyszero.phoenix.config.client.annotation.EnablePhoenixConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@Import({ConsumerConfig.class})
@RestController
@EnablePhoenixConfig
public class PhoenixRpcDemoConsumerApplication {

    @PhoenixConsumer
    UserService userService;


    @GetMapping("/user")
    public User findBy(@RequestParam("id") int id) {
        return userService.findById(id);
    }


    @RequestMapping("/find")
    public User find(@RequestParam("timeout") int timeout) {
        return userService.find(timeout);
    }


    @Autowired
    private Router<InstanceMeta> grayRouter;

    @RequestMapping("/gray")
    public String gray(@RequestParam("radio") int radio) {
        ((GrayRouter)grayRouter).setGrayRatio(radio);
        return "OK-new gray ratio is " + radio;
    }


    public static void main(String[] args) {
        SpringApplication.run(PhoenixRpcDemoConsumerApplication.class, args);
    }

    @Bean
    public ApplicationRunner consumerRunner() {
        return args -> {
            testAll();
        };
    }

    private void testAll() {
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


        System.out.println("Case 14. >>===[测试参数和返回值都是User[]类型]===");
        System.out.println("RPC result userService.findUsers(users): ");
        User[] users = new User[]{
                new User(120, "zys120"),
                new User(121, "zys121")};
        Arrays.stream(userService.findUsers(users)).forEach(System.out::println);

        System.out.println("Case 15. >>===[测试参数为long，返回值是User类型]===");
        User userLong = userService.findById(10000L);
        System.out.println(userLong);

        System.out.println("Case 16. >>===[测试参数为boolean，返回值都是User类型]===");
        User user100 = userService.ex(false);
        System.out.println(user100);

        System.out.println("Case 17. >>===[测试服务端抛出一个RuntimeException异常]===");
        try {
            User userEx = userService.ex(true);
            System.out.println(userEx);
        } catch (RuntimeException e) {
            System.out.println(" ===> exception: " + e.getMessage());
        }

        System.out.println("Case 18. >>===[测试服务端抛出一个超时重试后成功的场景]===");
        // 超时设置的【漏斗原则】
        // A 2000 -> B 1500 -> C 1200 -> D 1000
        long start = System.currentTimeMillis();
        userService.find(1100);
        userService.find(1000);
        System.out.println("userService.find take "
                + (System.currentTimeMillis() - start) + "ms");


        System.out.println("Case 19. >>===[测试通过Context跨消费者和提供者进行传参]===");
        String Key_Version = "rpc.version";
        String Key_Message = "rpc.message";
        RpcContext.setContextParameter(Key_Version, "v8");
        RpcContext.setContextParameter(Key_Message, "this is a v8 message");
        String version = userService.echoParameter(Key_Version);
        RpcContext.setContextParameter(Key_Version, "v9");
        RpcContext.setContextParameter(Key_Message, "this is a v9 message");
        String message = userService.echoParameter(Key_Message);
        System.out.println(" ===> echo parameter from c->p->c: " + Key_Version + " -> " + version);
        System.out.println(" ===> echo parameter from c->p->c: " + Key_Message + " -> " + message);
    }
}
