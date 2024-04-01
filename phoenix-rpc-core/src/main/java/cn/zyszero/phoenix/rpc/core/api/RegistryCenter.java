package cn.zyszero.phoenix.rpc.core.api;

import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.meta.ServiceMeta;
import cn.zyszero.phoenix.rpc.core.registry.ChangedListener;

import java.util.List;

/**
 * @Author: zyszero
 * @Date: 2024/3/25 22:58
 */
public interface RegistryCenter {

    void start(); // p/c

    void stop(); // p/c


    // provider 侧
    void register(ServiceMeta service, InstanceMeta instance); // p

    void unregister(ServiceMeta service, InstanceMeta instance); // p


    // consumer 侧
    List<InstanceMeta> fetchAll(ServiceMeta service); // c

    void subscribe(ServiceMeta service, ChangedListener listener); // c

    // void heartbeat(); // c

    class StaticRegistryCenter implements RegistryCenter {

        List<InstanceMeta> providers;

        public StaticRegistryCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {

        }

        @Override
        public void stop() {

        }

        @Override
        public void register(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public void unregister(ServiceMeta service, InstanceMeta instance) {

        }

        @Override
        public List<InstanceMeta> fetchAll(ServiceMeta service) {
            return providers;
        }

        @Override
        public void subscribe(ServiceMeta service, ChangedListener listener) {

        }
    }
}
