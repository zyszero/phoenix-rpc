package cn.zyszero.phoenix.rpc.core.registry;

/**
 * @Author: zyszero
 * @Date: 2024/3/29 1:04
 */
public interface ChangedListener {
    void fire(Event event);
}
