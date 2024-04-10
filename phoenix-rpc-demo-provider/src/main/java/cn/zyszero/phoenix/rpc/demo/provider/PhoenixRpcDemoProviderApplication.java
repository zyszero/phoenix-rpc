package cn.zyszero.phoenix.rpc.demo.provider;

import cn.zyszero.phoenix.rpc.core.api.RpcRequest;
import cn.zyszero.phoenix.rpc.core.api.RpcResponse;
import cn.zyszero.phoenix.rpc.core.provider.ProviderConfig;
import cn.zyszero.phoenix.rpc.core.provider.ProviderInvoker;
import cn.zyszero.phoenix.rpc.demo.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Import(ProviderConfig.class)
public class PhoenixRpcDemoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhoenixRpcDemoProviderApplication.class, args);
    }


    @Autowired
    private ProviderInvoker providerInvoker;

    // 使用 HTTP + json 实现序列化和通信

    @RequestMapping("/")
    public RpcResponse<Object> invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invoke(request);
    }


    @Autowired
    UserService userService;

    @RequestMapping("/ports")
    public RpcResponse<String> ports(@RequestParam("ports") String ports) {
        userService.setTimeoutPorts(ports);
        RpcResponse<String> rpcResponse = new RpcResponse<>();
        rpcResponse.setStatues(true);
        rpcResponse.setData("OK: " + ports);
        return rpcResponse;

    }

    @Bean
    public ApplicationRunner runner() {
        return args -> {
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
