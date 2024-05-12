package cn.zyszero.phoenix.rpc.core.registry.phoenix;

import cn.zyszero.phoenix.rpc.core.api.RegistryCenter;
import cn.zyszero.phoenix.rpc.core.consumer.HttpInvoker;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import cn.zyszero.phoenix.rpc.core.meta.ServiceMeta;
import cn.zyszero.phoenix.rpc.core.registry.ChangedListener;
import cn.zyszero.phoenix.rpc.core.registry.Event;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    Map<String, Long> VERSIONS = new HashMap<>();

    ScheduledExecutorService executor;

    @Override
    public void start() {
        log.info(" ====>>>> [PhoenixRegistry] : start with server : {})", servers);
        executor = Executors.newScheduledThreadPool(1);
    }

    @Override
    public void stop() {
        log.info(" ====>>>> [PhoenixRegistry] : stop with server : {})", servers);
        executor.shutdown();
        try {
            executor.awaitTermination(1, TimeUnit.SECONDS);
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        } catch (InterruptedException ignore) {
        }
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>>> [PhoenixRegistry] : register instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/register?service=" + service.toPath(), InstanceMeta.class);
        log.info(" ====>>>> [PhoenixRegistry] : registered {}", instance);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>>> [PhoenixRegistry] : unregister instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/unregister?service=" + service.toPath(), InstanceMeta.class);
        log.info(" ====>>>> [PhoenixRegistry] : unregistered {}", instance);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" ====>>>> [PhoenixRegistry] : fetch all instances for {}", service);
        List<InstanceMeta> instances = HttpInvoker.httpGet(servers + "/fetchAll?service=" + service.toPath(), new TypeReference<>() {
        });
        log.info(" ====>>>> [PhoenixRegistry] : fetched all instances {}", instances);
        return instances;
    }

    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {
        log.info(" ====>>>> [PhoenixRegistry] : subscribe service {}", service);
        executor.scheduleWithFixedDelay(() -> {
            Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
            Long newVersion = HttpInvoker.httpGet(servers + "/version?service=" + service.toPath(), Long.class);
            log.info(" ====>>>> [PhoenixRegistry] : version = {}, newVersion = {}", version, newVersion);
            if (newVersion > version) {
                List<InstanceMeta> instances = fetchAll(service);
                listener.fire(new Event(instances));
                VERSIONS.put(service.toPath(), newVersion);
            }
        }, 1, 5, TimeUnit.SECONDS);
    }
}
