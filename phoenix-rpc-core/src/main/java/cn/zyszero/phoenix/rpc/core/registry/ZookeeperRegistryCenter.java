package cn.zyszero.phoenix.rpc.core.registry;

import cn.zyszero.phoenix.rpc.core.api.RegistryCenter;

import java.util.List;

/**
 * @Author: zyszero
 * @Date: 2024/3/25 23:25
 */
public class ZookeeperRegistryCenter implements RegistryCenter {
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void register(String service, String instance) {

    }

    @Override
    public void unregister(String service, String instance) {

    }

    @Override
    public List<String> fetchAll(String service) {
        return null;
    }
}
