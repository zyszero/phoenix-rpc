package cn.zyszero.phoenix.rpc.core.annotation;

import cn.zyszero.phoenix.rpc.core.config.ConsumerConfig;
import cn.zyszero.phoenix.rpc.core.config.ProviderConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author: zyszero
 * @Date: 2024/4/18 1:35
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({ProviderConfig.class, ConsumerConfig.class})
public @interface EnablePhoenixRpc {
}
