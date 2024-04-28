package cn.zyszero.phoenix.rpc.demo.consumer;

import cn.zyszero.phoenix.rpc.core.test.TestZookeeperServer;
import cn.zyszero.phoenix.rpc.demo.provider.PhoenixRpcDemoProviderApplication;
import com.ctrip.framework.apollo.mockserver.ApolloTestingServer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = {PhoenixRpcDemoConsumerApplication.class})
class PhoenixRpcDemoConsumerApplicationTests {

    static ApplicationContext context1;
    static ApplicationContext context2;

    static TestZookeeperServer zookeeperServer = new TestZookeeperServer();

    static ApolloTestingServer apollo = new ApolloTestingServer();

    @SneakyThrows
    @BeforeAll
    static void setUp() {
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============     ZK2182    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        zookeeperServer.start();
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============     Apollo    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        apollo.start();
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============      P8094    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        context1 = SpringApplication.run(PhoenixRpcDemoProviderApplication.class,
                "--server.port=8094",
                "--phoenix.rpc.registry.zookeeper.server=localhost:2182",
                "--logging.level.cn.zys.phoenix.rpc=info",
                "--phoenix.rpc.app.env=test",
                "--phoenix.rpc.provider.metas.dc=bj",
                "--phoenix.rpc.provider.metas.gray=false",
                "--phoenix.rpc.provider.metas.unit=B001",
                "--phoenix.rpc.provider.metas.tc=300");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        System.out.println(" =============      P8095    ========== ");
        System.out.println(" ====================================== ");
        System.out.println(" ====================================== ");
        context2 = SpringApplication.run(PhoenixRpcDemoProviderApplication.class,
                "--server.port=8095",
                "--phoenix.rpc.registry.zookeeper.server=localhost:2182",
                "--logging.level.cn.zys.phoenix.rpc=info",
                "--phoenix.rpc.app.env=test",
                "--phoenix.rpc.provider.metas.dc=bj",
                "--phoenix.rpc.provider.metas.gray=false",
                "--phoenix.rpc.provider.metas.unit=B002",
                "--phoenix.rpc.provider.metas.tc=300");

    }

    @Test
    void contextLoads() {
        System.out.println(" ===> PhoenixRpcDemoConsumerApplicationTests  ....");
    }

    @AfterAll
    static void destroy() {
        System.out.println(" ===========     close spring context    ======= ");
        SpringApplication.exit(context1, () -> 1);
        SpringApplication.exit(context2, () -> 1);
        System.out.println(" ===========     stop zookeeper server    ======= ");
        zookeeperServer.stop();
        System.out.println(" ===========     stop apollo mockserver   ======= ");
        apollo.close();
        System.out.println(" ===========     destroy in after all     ======= ");
    }

}
