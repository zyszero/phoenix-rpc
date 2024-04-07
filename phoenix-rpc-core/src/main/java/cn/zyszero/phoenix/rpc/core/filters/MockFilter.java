package cn.zyszero.phoenix.rpc.core.filters;

import cn.zyszero.phoenix.rpc.core.api.Filter;
import cn.zyszero.phoenix.rpc.core.api.RpcRequest;
import cn.zyszero.phoenix.rpc.core.api.RpcResponse;
import cn.zyszero.phoenix.rpc.core.util.MethodUtils;
import cn.zyszero.phoenix.rpc.core.util.MockUtils;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Author: zyszero
 * @Date: 2024/4/7 7:09
 */
public class MockFilter implements Filter {
    @Override
    @SneakyThrows
    public Object preFilter(RpcRequest request) {
        Class<?> service = Class.forName(request.getService());
        Method method = findMethod(service, request.getMethodSign());
        Class<?> returnType = method.getReturnType();
        return MockUtils.mock(returnType);
    }


    Method findMethod(Class service, String methodSign) {
        return Arrays.stream(service.getMethods())
                .filter(method -> !MethodUtils.checkLocalMethod(method))
                .filter(sign -> methodSign.equals(MethodUtils.methodSign(sign)))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        return null;
    }
}
