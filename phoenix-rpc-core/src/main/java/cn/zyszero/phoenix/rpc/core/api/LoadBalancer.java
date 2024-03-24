package cn.zyszero.phoenix.rpc.core.api;

import java.util.List;

/**
 * 负载均衡，weightedRR，AAWR-自适应，
 * 8081， w=100, 25次
 * 8082， w=300, 75次
 * 0-99，random，<25， -8081， else 8082
 * <p>
 * UserService 10，，，
 * 8081， 10ms，
 * 8082， 100ms，
 * <p>
 * avg * 0.3 + last * 0.7 = W* ~
 *
 * @Author: zyszero
 * @Date: 2024/3/24 19:00
 */
public interface LoadBalancer<T> {
    T choose(List<T> providers);


    LoadBalancer DEFAULT = providers -> (providers == null || providers.isEmpty()) ? null : providers.get(0);
}
