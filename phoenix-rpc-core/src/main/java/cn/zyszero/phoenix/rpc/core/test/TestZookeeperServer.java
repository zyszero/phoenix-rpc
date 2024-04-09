package cn.zyszero.phoenix.rpc.core.test;

import lombok.SneakyThrows;
import org.apache.curator.test.InstanceSpec;
import org.apache.curator.test.TestingCluster;
import org.apache.curator.utils.CloseableUtils;

/**
 * Testing zk server.
 *
 * @Author: zyszero
 * @Date: 2024/4/9 20:51
 */
public class TestZookeeperServer {
    TestingCluster cluster;

    @SneakyThrows
    public void start() {
        InstanceSpec instanceSpec = new InstanceSpec(null, 2182,
                -1, -1, true,
                -1, -1, -1);
        cluster = new TestingCluster(instanceSpec);
        System.out.println("TestingZooKeeperServer starting ...");
        cluster.start();
        cluster.getServers().forEach(s -> System.out.println(s.getInstanceSpec()));
        System.out.println("TestingZooKeeperServer started.");
    }


    @SneakyThrows
    public void stop() {
        System.out.println("TestingZooKeeperServer stopping ...");
        cluster.stop();
        CloseableUtils.closeQuietly(cluster);
        System.out.println("TestingZooKeeperServer stopped.");
    }
}
