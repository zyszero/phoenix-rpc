package cn.zyszero.phoenix.rpc.core.registry.phoenix;

import cn.zyszero.phoenix.rpc.core.api.RegistryCenter;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.meta.ServiceMeta;
import cn.zyszero.phoenix.rpc.core.registry.ChangedListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * implementation for phoenix registry center.
 *
 * @Author: zyszero
 * @Date: 2024/5/11 6:00
 */
@Slf4j
public class PhoenixRegistryCenter implements RegistryCenter {


    @Value("${phoenix.registry.servers}")
    private String servers;

    @Override
    public void start() {
        log.info(" ====>>>> [PhoenixRegistry] : start with server : {})", servers);
    }

    @Override
    public void stop() {
        log.info(" ====>>>> [PhoenixRegistry] : stop with server : {})", servers);
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>>> [PhoenixRegistry] : register instance {} for {}", instance, service);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>>> [PhoenixRegistry] : unregister instance {} for {}", instance, service);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" ====>>>> [PhoenixRegistry] : fetch all instances for {}", service);
        return null;
    }

    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {

    }
}
