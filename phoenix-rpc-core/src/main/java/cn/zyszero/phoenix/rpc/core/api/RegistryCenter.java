package cn.zyszero.phoenix.rpc.core.api;

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
    void register(String service, String instance); // p

    void unregister(String service, String instance); // p


    // consumer 侧
    List<String> fetchAll(String service); // c

    void subscribe(String service, ChangedListener listener); // c

    // void heartbeat(); // c

    class StaticRegistryCenter implements RegistryCenter {

        List<String> providers;

        public StaticRegistryCenter(List<String> providers) {
            this.providers = providers;
        }

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
            return providers;
        }

        @Override
        public void subscribe(String service, ChangedListener listener) {

        }
    }
}
