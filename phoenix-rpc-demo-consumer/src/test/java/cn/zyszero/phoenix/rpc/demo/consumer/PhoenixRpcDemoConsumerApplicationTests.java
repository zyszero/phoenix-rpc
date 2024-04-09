package cn.zyszero.phoenix.rpc.demo.consumer;

import cn.zyszero.phoenix.rpc.core.test.TestZookeeperServer;
import cn.zyszero.phoenix.rpc.demo.provider.PhoenixRpcDemoProviderApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class PhoenixRpcDemoConsumerApplicationTests {

    static ApplicationContext context;

    static TestZookeeperServer testZookeeperServer = new TestZookeeperServer();

    @BeforeAll
    static void setUp() {
        testZookeeperServer.start();
        context = SpringApplication.run(PhoenixRpcDemoProviderApplication.class,
                "--server.port=8848", "--phoenix.rpc.registry.zookeeper.server=localhost:2182", "--logging.level.cn.zys.phoenix.rpc=DEBUG");

    }

    @Test
    void contextLoads() {
        System.out.println(" ===> consumer test contextLoads .... ");
    }

    @AfterAll
    static void destroy() {
        SpringApplication.exit(context, () -> 1);
        testZookeeperServer.stop();
    }

}
