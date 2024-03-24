package cn.zyszero.phoenix.rpc.core.api;

import java.util.List;

/**
 * @Author: zyszero
 * @Date: 2024/3/24 19:00
 */
public interface Router<T> {
    List<T> route(List<T> providers);

    Router DEFAULT = providers -> providers;
}
