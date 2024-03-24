package cn.zyszero.phoenix.rpc.core.cluster;

import cn.zyszero.phoenix.rpc.core.api.LoadBalancer;

import java.util.List;
import java.util.Random;

/**
 * @Author: zyszero
 * @Date: 2024/3/24 20:10
 */
public class RandomLoadBalancer<T> implements LoadBalancer<T> {

    Random random = new Random();

    @Override
    public T choose(List<T> providers) {
        if (providers == null || providers.size() == 0) {
            return null;
        }
        if (providers.size() == 1) {
            return providers.get(0);
        }
        return providers.get(random.nextInt(providers.size()));
    }
}
