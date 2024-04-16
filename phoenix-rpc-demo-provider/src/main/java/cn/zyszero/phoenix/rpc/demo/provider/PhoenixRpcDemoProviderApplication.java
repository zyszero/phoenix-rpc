package cn.zyszero.phoenix.rpc.demo.provider;

import cn.zyszero.phoenix.rpc.core.api.RpcRequest;
import cn.zyszero.phoenix.rpc.core.api.RpcResponse;
import cn.zyszero.phoenix.rpc.core.config.ProviderConfig;
import cn.zyszero.phoenix.rpc.core.transport.SpringBootTransport;
import cn.zyszero.phoenix.rpc.demo.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
@Import(ProviderConfig.class)
public class PhoenixRpcDemoProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhoenixRpcDemoProviderApplication.class, args);
    }


    @Autowired
    private SpringBootTransport transport;


    @Bean
    public ApplicationRunner runner() {
        return args -> {
            testAll();
        };
    }

    private void testAll() {
        //  test 1 parameter method
        System.out.println("Provider Case 1. >>===[基本测试：1个参数]===");
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService("cn.zyszero.phoenix.rpc.demo.api.UserService");
        rpcRequest.setMethodSign("findById@1_int");
        rpcRequest.setArgs(new Object[]{100});

        RpcResponse rpcResponse = transport.invoke(rpcRequest);
        System.out.println("response: " + rpcResponse.getData());


        // test 2 parameters method
        System.out.println("Provider Case 2. >>===[基本测试：2个参数]===");
        RpcRequest rpcRequest1 = new RpcRequest();
        rpcRequest1.setService("cn.zyszero.phoenix.rpc.demo.api.UserService");
        rpcRequest1.setMethodSign("findById@2_int_java.lang.String");
        rpcRequest1.setArgs(new Object[]{100, "zyszero"});

        RpcResponse rpcResponse1 = transport.invoke(rpcRequest1);
        System.out.println("response: " + rpcResponse1.getData());

        // test 3 for List<User> method&parameter
        System.out.println("Provider Case 3. >>===[复杂测试：参数类型为List<User>]===");
        RpcRequest request3 = new RpcRequest();
        request3.setService("cn.zyszero.phoenix.rpc.demo.api.UserService");
        request3.setMethodSign("getList@1_java.util.List");
        List<User> userList = new ArrayList<>();
        userList.add(new User(100, "zyszero100"));
        userList.add(new User(101, "zyszero101"));
        request3.setArgs(new Object[]{userList});
        RpcResponse<Object> rpcResponse3 = transport.invoke(request3);
        System.out.println("return : " + rpcResponse3.getData());

        // test 4 for Map<String, User> method&parameter
        System.out.println("Provider Case 4. >>===[复杂测试：参数类型为Map<String, User>]===");
        RpcRequest request4 = new RpcRequest();
        request4.setService("cn.zyszero.phoenix.rpc.demo.api.UserService");
        request4.setMethodSign("getMap@1_java.util.Map");
        Map<String, User> userMap = new HashMap<>();
        userMap.put("P100", new User(100, "zyszero100"));
        userMap.put("P101", new User(101, "zyszero101"));
        request4.setArgs(new Object[]{userMap});
        RpcResponse<Object> rpcResponse4 = transport.invoke(request4);
        System.out.println("return : " + rpcResponse4.getData());
    }
}
