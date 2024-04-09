package cn.zyszero.phoenix.rpc.demo.provider;

import cn.zyszero.phoenix.rpc.core.test.TestZookeeperServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PhoenixRpcDemoProviderApplicationTests {

    static TestZookeeperServer zkServer = new TestZookeeperServer();

    @BeforeAll
    static void init() {
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============     ZK2182    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        zkServer.start();
    }

    @Test
    void contextLoads() {
        System.out.println(" ===> PhoenixRpcDemoProviderApplicationTests  .... ");
    }

    @AfterAll
    static void destroy() {
        zkServer.stop();
    }

}
