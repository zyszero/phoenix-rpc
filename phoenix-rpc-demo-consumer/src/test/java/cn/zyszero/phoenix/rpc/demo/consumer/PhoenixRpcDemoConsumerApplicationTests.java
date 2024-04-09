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
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============     ZK2182    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        testZookeeperServer.start();
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============      P8848    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        context = SpringApplication.run(PhoenixRpcDemoProviderApplication.class,
                "--server.port=8848", "--phoenix.rpc.registry.zookeeper.server=localhost:2182", "--logging.level.cn.zys.phoenix.rpc=info");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============      P8849    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        context = SpringApplication.run(PhoenixRpcDemoProviderApplication.class,
                "--server.port=8849", "--phoenix.rpc.registry.zookeeper.server=localhost:2182", "--logging.level.cn.zys.phoenix.rpc=info");

    }

    @Test
    void contextLoads() {
        System.out.println(" ===> PhoenixRpcDemoConsumerApplicationTests  ....");
    }

    @AfterAll
    static void destroy() {
        SpringApplication.exit(context, () -> 1);
        testZookeeperServer.stop();
    }

}
