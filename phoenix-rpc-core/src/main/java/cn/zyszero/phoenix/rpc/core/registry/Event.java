package cn.zyszero.phoenix.rpc.core.registry;

import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @Author: zyszero
 * @Date: 2024/3/29 1:11
 */
@Data
@AllArgsConstructor
public class Event {
    private List<InstanceMeta> data;
}
