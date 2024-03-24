package cn.zyszero.phoenix.rpc.core.cluster;

import cn.zyszero.phoenix.rpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: zyszero
 * @Date: 2024/3/24 20:10
 */
public class RoundRibonLoadBalancer<T>  implements LoadBalancer<T> {

    AtomicInteger index = new AtomicInteger(0);


    @Override
    public T choose(List<T> providers) {
        if (providers == null || providers.size() == 0) {
            return null;
        }
        if (providers.size() == 1) {
            return providers.get(0);
        }
        return providers.get((index.getAndIncrement() & 0x7fffffff) % providers.size());
    }
}
