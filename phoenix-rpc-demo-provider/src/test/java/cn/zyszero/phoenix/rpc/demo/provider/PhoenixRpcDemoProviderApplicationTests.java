package cn.zyszero.phoenix.rpc.demo.provider;

import cn.zyszero.phoenix.rpc.core.config.ProviderProperties;
import cn.zyszero.phoenix.rpc.core.test.TestZookeeperServer;
import com.ctrip.framework.apollo.core.ApolloClientSystemConsts;
import com.ctrip.framework.apollo.mockserver.ApolloTestingServer;
import com.ctrip.framework.apollo.mockserver.MockApolloExtension;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockApolloExtension.class)
class PhoenixRpcDemoProviderApplicationTests {

    static TestZookeeperServer zkServer = new TestZookeeperServer();

    static ApolloTestingServer apollo = new ApolloTestingServer();


    @Autowired
    ProviderProperties providerProperties;


    @SneakyThrows
    @BeforeAll
    static void init() {
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============     ZK2182    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        zkServer.start();
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" ===========     mock apollo    ======= ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        apollo.start();
    }

    @Test
    void contextLoads() {
        System.out.println(" ===> PhoenixRpcDemoProviderApplicationTests  .... ");
        System.out.println("....  ApolloClientSystemConsts.APOLLO_CONFIG_SERVICE  .....");
        System.out.println(System.getProperty(ApolloClientSystemConsts.APOLLO_CONFIG_SERVICE));
        System.out.println("....  ApolloClientSystemConsts.APOLLO_CONFIG_SERVICE  .....");
    }

    @AfterAll
    static void destroy() {
        System.out.println(" ===========     stop zookeeper server    ======= ");
        zkServer.stop();
        System.out.println(" ===========     stop apollo mockserver   ======= ");
        apollo.close();
        System.out.println(" ===========     destroy in after all     ======= ");
    }

}
