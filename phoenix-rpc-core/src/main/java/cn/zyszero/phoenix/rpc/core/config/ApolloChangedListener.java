package cn.zyszero.phoenix.rpc.core.config;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Apollo config change listener.
 * @Author: zyszero
 * @Date: 2024/4/25 4:04
 */
@Data
@Slf4j
public class ApolloChangedListener implements ApplicationContextAware {
    ApplicationContext applicationContext;


    @ApolloConfigChangeListener({"app1","application"})
    // ("${apollo.bootstrap.namespaces}")
    private void changeHandler(ConfigChangeEvent changeEvent) {
        for (String key : changeEvent.changedKeys()) {
            ConfigChange change = changeEvent.getChange(key);
            log.info("Found change - {}", change.toString());
        }

        // 更新相应的bean的属性值，主要是存在@ConfigurationProperties注解的bean
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
    }

}
