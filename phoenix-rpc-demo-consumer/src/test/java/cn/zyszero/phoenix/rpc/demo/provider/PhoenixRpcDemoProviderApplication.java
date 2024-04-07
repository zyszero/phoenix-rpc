package cn.zyszero.phoenix.rpc.demo.provider;

import cn.zyszero.phoenix.rpc.core.api.RpcRequest;
import cn.zyszero.phoenix.rpc.core.api.RpcResponse;
import cn.zyszero.phoenix.rpc.core.provider.ProviderConfig;
import cn.zyszero.phoenix.rpc.core.provider.ProviderInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zyszero
 * @Date: 2024/4/8 3:28
 */
@SpringBootApplication
@RestController
@Import({ProviderConfig.class})
public class PhoenixRpcDemoProviderApplication {

    @Autowired
    ProviderInvoker providerInvoker;

    @RequestMapping("/")
    public RpcResponse<Object> invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invoke(request);
    }


    @Bean
    ApplicationRunner providerRun() {
        return x -> {
            // test 1 parameter method
            RpcRequest rpcRequest = new RpcRequest();
            rpcRequest.setService("cn.zyszero.phoenix.rpc.demo.api.UserService");
            rpcRequest.setMethodSign("findById@1_int");
            rpcRequest.setArgs(new Object[]{100});

            RpcResponse rpcResponse = invoke(rpcRequest);
            System.out.println("response: " + rpcResponse.getData());


            // test 2 parameter method
            RpcRequest rpcRequest1 = new RpcRequest();
            rpcRequest1.setService("cn.zyszero.phoenix.rpc.demo.api.UserService");
            rpcRequest1.setMethodSign("findById@2_int_java.lang.String");
            rpcRequest1.setArgs(new Object[]{100, "zyszero"});

            RpcResponse rpcResponse1 = invoke(rpcRequest1);
            System.out.println("response: " + rpcResponse1.getData());

        };
    }
}
