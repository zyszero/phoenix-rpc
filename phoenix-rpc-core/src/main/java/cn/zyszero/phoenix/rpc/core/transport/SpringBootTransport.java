package cn.zyszero.phoenix.rpc.core.transport;

import cn.zyszero.phoenix.rpc.core.api.RpcRequest;
import cn.zyszero.phoenix.rpc.core.api.RpcResponse;
import cn.zyszero.phoenix.rpc.core.provider.ProviderInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zyszero
 * @Date: 2024/4/16 23:35
 */
@RestController
public class SpringBootTransport {
    @Autowired
    private ProviderInvoker providerInvoker;

    // 使用 HTTP + json 实现序列化和通信

    @RequestMapping("/phoenix-rpc")
    public RpcResponse<Object> invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invoke(request);
    }
}
