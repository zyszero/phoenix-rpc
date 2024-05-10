package cn.zyszero.phoenix.rpc.core.registry.zk;

import cn.zyszero.phoenix.rpc.core.api.RegistryCenter;
import cn.zyszero.phoenix.rpc.core.api.RpcException;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.meta.ServiceMeta;
import cn.zyszero.phoenix.rpc.core.registry.ChangedListener;
import cn.zyszero.phoenix.rpc.core.registry.Event;
import com.alibaba.fastjson.JSON;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: zyszero
 * @Date: 2024/3/25 23:25
 */
@Slf4j
public class ZookeeperRegistryCenter implements RegistryCenter {


    @Value("${phoenix.rpc.registry.zookeeper.server}")
    private String zkServer;

    @Value("${phoenix.rpc.registry.zookeeper.root}")
    private String zkRoot;

    private CuratorFramework client = null;

    private List<TreeCache> caches = new ArrayList<>();

    private boolean running = false;

    @Override
    public synchronized void start() {
        if (running) {
            log.info(" ===> zk client has started to server[" + zkServer + "/" + zkRoot + "], ignored.");
            return;
        }

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(zkServer)
                .namespace(zkRoot)
                .retryPolicy(retryPolicy)
                .build();
        log.info(" ===> zk client starting to server[" + zkServer + "/" + zkRoot + "].");
        client.start();
    }

    @Override
    public synchronized void stop() {
        if (!running) {
            log.info(" ===> zk client has stopped, ignored.");
            return;
        }
        log.info(" ===> zk tree cache closed.");
        caches.forEach(TreeCache::close);
        log.info(" ===> zk client stopped.");
        client.close();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service.toPath();
        try {
            // 创建服务的持久化节点
            if (client.checkExists().forPath(servicePath) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, service.toMetas().getBytes());
            }
            // 创建实例的临时性节点
            String instancePath = servicePath + "/" + instance.toPath();
            log.info(" ===> register instance node to zk: " + instancePath);
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, instance.toMetas().getBytes());
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service.toPath();
        try {
            // 判断服务节点是否存在
            if (client.checkExists().forPath(servicePath) == null) {
                return;
            }
            // 删除实例节点
            String instancePath = servicePath + "/" + instance.toPath();
            log.info(" ===> unregister instance node from zk: " + instancePath);
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        String servicePath = "/" + service.toPath();
        try {
            // 获取所有子节点
            log.info(" ===> fetch all instance nodes from zk: " + servicePath);
            List<String> nodes = client.getChildren().forPath(servicePath);
            nodes.forEach(System.out::println);
            return mapInstances(nodes, servicePath);
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

    @NotNull
    private List<InstanceMeta> mapInstances(List<String> nodes, String servicePath) {
        return nodes.stream()
                .map(node -> {
                    String[] strs = node.split("_");
                    InstanceMeta instance = InstanceMeta.http(strs[0], Integer.valueOf(strs[1]));
                    log.info(" instance: {}", instance.toUrl());
                    String nodePath = servicePath + "/" + node;
                    byte[] bytes;
                    try {
                        bytes = client.getData().forPath(nodePath);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    Map<String, String> params = JSON.parseObject(new String(bytes), HashMap.class);
                    params.forEach((k, v) -> log.info(" ===> key: " + k + ", value: " + v));
                    instance.setParameters(params);
                    return instance;
                })
                .collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {
        // 将 zk 的变化，转化为 provider 列表的变化
        String servicePath = "/" + service.toPath();
        final TreeCache cache = TreeCache.newBuilder(client, servicePath)
                .setCacheData(true)
                .setMaxDepth(2)
                .build();
        cache.getListenable().addListener((curator, event) -> {
            // 有任何节点变动，这里都会触发
            log.info("zk subscribe event: " + event);
            List<InstanceMeta> nodes = fetchAll(service);
            listener.fire(new Event(nodes));
        });
        cache.start();
        caches.add(cache);
    }
}
