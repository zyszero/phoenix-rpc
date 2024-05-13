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
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * implementation for phoenix registry center.
 *
 * @Author: zyszero
 * @Date: 2024/5/11 6:00
 */
@Slf4j
public class PhoenixRegistryCenter implements RegistryCenter {

    private static final String REGISTER_PATH = "/register";

    private static final String UNREGISTER_PATH = "/unregister";

    private static final String FIND_ALL_PATH = "/findAll";

    private static final String VERSION_PATH = "/version";


    private static final String RENEWS_PATH = "/renews";


    @Value("${phoenix.registry.servers}")
    private String servers;

    Map<String, Long> VERSIONS = new HashMap<>();

    MultiValueMap<InstanceMeta, ServiceMeta> RENEWS = new LinkedMultiValueMap<>();


    private PhoenixHealthChecker healthChecker = new PhoenixHealthChecker();


    @Override
    public void start() {
        log.info(" ====>>>> [PhoenixRegistry] : start with server : {})", servers);
        healthChecker.start();
        providerCheck();
    }

    @Override
    public void stop() {
        log.info(" ====>>>> [PhoenixRegistry] : stop with server : {})", servers);
        healthChecker.stop();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>>> [PhoenixRegistry] : register instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), registerPath(service), InstanceMeta.class);
        log.info(" ====>>>> [PhoenixRegistry] : registered {}", instance);
        RENEWS.add(instance, service);
    }

    @NotNull
    private String registerPath(ServiceMeta service) {
        return path(REGISTER_PATH, service);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" ====>>>> [PhoenixRegistry] : unregister instance {} for {}", instance, service);
        HttpInvoker.httpPost(JSON.toJSONString(instance), unregister(service), InstanceMeta.class);
        log.info(" ====>>>> [PhoenixRegistry] : unregistered {}", instance);
        RENEWS.remove(instance, service);
    }

    private String unregister(ServiceMeta service) {
        return path(UNREGISTER_PATH, service);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" ====>>>> [PhoenixRegistry] : fetch all instances for {}", service);
        List<InstanceMeta> instances = HttpInvoker.httpGet(findAllPath(service), new TypeReference<>() {
        });
        log.info(" ====>>>> [PhoenixRegistry] : fetched all instances {}", instances);
        return instances;
    }

    private String findAllPath(ServiceMeta service) {
        return path(FIND_ALL_PATH, service);
    }


    public void providerCheck() {
        healthChecker.providerCheck(() -> RENEWS.keySet().forEach(
                instance -> {
                    Long timestamp = HttpInvoker.httpPost(JSON.toJSONString(instance),
                            renewsPath(RENEWS.get(instance)), Long.class);
                    log.info(" ====>>>> [PhoenixRegistry] : renew instance {} at {}", instance, timestamp);
                }
        ));
    }

    public void subscribe(ServiceMeta service, ChangedListener listener) {
        log.info(" ====>>>> [PhoenixRegistry] : subscribe service {}", service);
        healthChecker.consumerCheck(() -> {
            Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
            Long newVersion = HttpInvoker.httpGet(versionPath(service), Long.class);
            log.info(" ====>>>> [PhoenixRegistry] : version = {}, newVersion = {}", version, newVersion);
            if (newVersion > version) {
                List<InstanceMeta> instances = fetchAll(service);
                listener.fire(new Event(instances));
                VERSIONS.put(service.toPath(), newVersion);
            }
        });
    }

    private String versionPath(ServiceMeta service) {
        return path(VERSION_PATH, service);
    }


    private String renewsPath(List<ServiceMeta> serviceList) {
        return path(RENEWS_PATH, serviceList);
    }


    private String path(String context, ServiceMeta service) {
        return servers + context + "?service=" + service.toPath();
    }

    private String path(String context, List<ServiceMeta> serviceList) {
        StringBuilder sb = new StringBuilder();
        for (ServiceMeta service : serviceList) {
            sb.append(service.toPath()).append(",");
        }
        String services = sb.toString();
        if (services.endsWith(",")) {
            services = services.substring(0, services.length() - 1);
        }
        log.info(" ====>>>> [PhoenixRegistry] : renew instance for {}", services);
        return servers + context + "?services=" + services;
    }
}
