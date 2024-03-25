package cn.zyszero.phoenix.rpc.core.api;

import java.util.List;

/**
 * router 的作用：
 * 本质上来说从一堆中选出一小部分，即从一个集合中选出一个集合的能力，是从 LoadBalancer 中抽象出来的能力
 * 比如：
 * 有机房 A、机房 B、机房 C，机房 A 的实例都打上标签 a、以此类推，机房 B =》实例 b，机房 C =》实例 c；
 * 假设 A 机房最近，我希望可以就近访问实例，那么选出所有标签 a 的实例的能力就是 Router 的作用，
 * 下一步才是通过 LoadBalancer 选择一个实例进行访问
 * @Author: zyszero
 * @Date: 2024/3/24 19:00
 */
public interface Router<T> {
    List<T> route(List<T> providers);

    Router DEFAULT = providers -> providers;
}
