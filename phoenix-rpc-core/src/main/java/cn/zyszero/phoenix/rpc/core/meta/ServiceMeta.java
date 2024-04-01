package cn.zyszero.phoenix.rpc.core.meta;

import lombok.Builder;
import lombok.Data;

/**
 * 描述服务元数据。
 *
 * @Author: zyszero
 * @Date: 2024/4/2 0:54
 */
@Data
@Builder
public class ServiceMeta {
    private String app;

    private String namespace;

    private String env;

    private String name;

    public String toPath() {
        return String.format("%s_%s_%s_%s", app, namespace, env, name);
    }
}
