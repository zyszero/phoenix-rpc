package cn.zyszero.phoenix.rpc.core.registry.phoenix;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * check health for registry center.
 * @Author: zyszero
 * @Date: 2024/5/13 23:41
 */
@Slf4j
public class PhoenixHealthChecker {

    ScheduledExecutorService consumerExecutor;

    ScheduledExecutorService providerExecutor;


    public void start() {
        log.info(" ====>>>> [PhoenixRegistry] : start with health checker.");
        consumerExecutor = Executors.newScheduledThreadPool(1);
        providerExecutor = Executors.newScheduledThreadPool(1);

    }

    public void stop() {
        log.info(" ====>>>> [PhoenixRegistry] : stop with health checker.");
        gracefulShutdown(consumerExecutor);
        gracefulShutdown(providerExecutor);
    }


    public void consumerCheck(Callback callback) {
        consumerExecutor.scheduleWithFixedDelay(() -> {
            try {
                callback.call();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }, 1, 5, TimeUnit.SECONDS);
    }

    public void providerCheck(Callback callback) {
        providerExecutor.scheduleWithFixedDelay(() -> {
            try {
                callback.call();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }, 5, 5, TimeUnit.SECONDS);
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


    public interface Callback {
        void call() throws Exception;
    }
}
