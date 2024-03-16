package cn.zyszero.phoenix.rpc.core.meta;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @author zyszero
 * @create: 2024/3/13
 */
@Data
public class ProviderMeta {

    private Method method;

    private String methodSign;

    private Object serviceImpl;
}
