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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
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

    MultiValueMap<InstanceMeta, ServiceMeta> RENEWS = new LinkedMultiValueMap<>();

    ScheduledExecutorService consumerExecutor;

    ScheduledExecutorService providerExecutor;

    @Override
    public void start() {
        log.info(" ====>>>> [PhoenixRegistry] : start with server : {})", servers);
        consumerExecutor = Executors.newScheduledThreadPool(1);
        providerExecutor = Executors.newScheduledThreadPool(1);
        providerExecutor.scheduleWithFixedDelay(() -> RENEWS.keySet().forEach(instance -> {
            StringJoiner joiner = new StringJoiner(",");
            for (ServiceMeta service : RENEWS.get(instance)) {
                joiner.add(service.toPath());
            }
            String services = joiner.toString();
            Long timestamp = HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/renews?services=" + services, Long.class);
            log.info(" ====>>>> [PhoenixRegistry] : renew instance {} for {} at {}", instance, services, timestamp);
        }), 5, 5, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        log.info(" ====>>>> [PhoenixRegistry] : stop with server : {})", servers);
        gracefulShutdown(consumerExecutor);
        gracefulShutdown(providerExecutor);
    }

    private void gracefulShutdown(ScheduledExecutorService executorService) {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.SECONDS);
            if (!executorService.isTerminated()) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ignore) {
        }
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>>> [PhoenixRegistry] : register instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/register?service=" + service.toPath(), InstanceMeta.class);
        log.info(" ====>>>> [PhoenixRegistry] : registered {}", instance);
        RENEWS.add(instance, service);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>>> [PhoenixRegistry] : unregister instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/unregister?service=" + service.toPath(), InstanceMeta.class);
        log.info(" ====>>>> [PhoenixRegistry] : unregistered {}", instance);
        RENEWS.remove(instance, service);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" ====>>>> [PhoenixRegistry] : fetch all instances for {}", service);
        List<InstanceMeta> instances = HttpInvoker.httpGet(servers + "/findAll?service=" + service.toPath(), new TypeReference<>() {
        });
        log.info(" ====>>>> [PhoenixRegistry] : fetched all instances {}", instances);
        return instances;
    }

    @Override
    public void subscribe(ServiceMeta service, ChangedListener listener) {
        log.info(" ====>>>> [PhoenixRegistry] : subscribe service {}", service);
        consumerExecutor.scheduleWithFixedDelay(() -> {
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
