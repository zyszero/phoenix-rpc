package cn.zyszero.phoenix.rpc.core.cluster;

import cn.zyszero.phoenix.rpc.core.api.Router;
import cn.zyszero.phoenix.rpc.core.meta.InstanceMeta;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * 灰度路由.
 * <p>
 * 可以做一些灰度用户，某一次请求上加灰度标记 ctx.gray=true
 * <p>
 * 结合蓝绿 + 灰度：
 * 100个节点 都是 normal
 * 100个节点 都是灰度
 * 按比例调拨流量
 *
 * @Author: zyszero
 * @Date: 2024/4/16 0:31
 */
@Slf4j
public class GrayRouter implements Router<InstanceMeta> {

    private int grayRatio;

    private final Random random = new Random();

    public GrayRouter(int grayRatio) {
        this.grayRatio = grayRatio;
    }

    @Override
    public List<InstanceMeta> route(List<InstanceMeta> providers) {
        // ctx.gray=true 灰度 VIP/白名单
        if (providers == null || providers.size() <= 1) {
            return providers;
        }

        List<InstanceMeta> normalNodes = new ArrayList<>();
        List<InstanceMeta> grayNodes = new ArrayList<>();

        providers.forEach(provider -> {
            if ("true".equals(provider.getParameters().get("gray"))) {
                grayNodes.add(provider);
            } else {
                normalNodes.add(provider);
            }
        });

        log.debug(" grayRouter grayNodes/normalNodes,grayRatio ===> {}/{},{}",
                grayNodes.size(), normalNodes.size(), grayRatio);

        if (normalNodes.isEmpty() || grayNodes.isEmpty()) {
            return providers;
        }

        if (grayRatio <= 0) {
            return normalNodes;
        } else if (grayRatio >= 100) {
            return grayNodes;
        }

        // 在 A 的情况下， 返回 normal nodes ===> 不管 LB 是什么，都是返回 normal nodes
        // 在 B 的情况下， 返回 gray nodes ===> 不管 LB 是什么，都是返回 gray nodes
        if (random.nextInt(100) < grayRatio) {
            log.debug(" grayRouter grayNodes ===> {}", grayNodes);
            return grayNodes;
        } else {
            log.debug(" grayRouter normalNodes ===> {}", normalNodes);
            return normalNodes;
        }

    }

    public void setGrayRatio(int grayRatio) {
        this.grayRatio = grayRatio;
    }
}
