package cn.zyszero.phoenix.rpc.core.meta;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * 描述 Provider 的映射关系。
 *
 * @author zyszero
 * @create: 2024/3/13
 */
@Data
public class ProviderMeta {

    private Method method;

    private String methodSign;

    private Object serviceImpl;
}
