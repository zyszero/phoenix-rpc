package cn.zyszero.phoenix.rpc.demo.provider;

import cn.zyszero.phoenix.rpc.core.api.RpcRequest;
import cn.zyszero.phoenix.rpc.core.api.RpcResponse;
import cn.zyszero.phoenix.rpc.core.provider.ProviderBootstrap;
import cn.zyszero.phoenix.rpc.core.provider.ProviderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Import(ProviderConfig.class)
public class PhoenixRpcDemoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhoenixRpcDemoProviderApplication.class, args);
    }


    @Autowired
    private ProviderBootstrap providerBootstrap;

    // 使用 HTTP + json 实现序列化和通信

    @RequestMapping("/rpc")
    public RpcResponse invoke(@RequestBody RpcRequest request) {
        return providerBootstrap.invoke(request);
    }


    @Bean
    public ApplicationRunner runner() {
        return args -> {
            RpcRequest rpcRequest = new RpcRequest();
            rpcRequest.setService("cn.zyszero.phoenix.rpc.demo.api.UserService");
            rpcRequest.setMethod("findById");
            rpcRequest.setArgs(new Object[]{100});
            RpcResponse rpcResponse = invoke(rpcRequest);
            System.out.println("response: " + rpcResponse.getData());
        };
    }
}
