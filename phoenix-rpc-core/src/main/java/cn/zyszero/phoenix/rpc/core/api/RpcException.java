package cn.zyszero.phoenix.rpc.core.api;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RPC 统一异常类.
 * @Author: zyszero
 * @Date: 2024/4/10 0:57
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RpcException extends RuntimeException {


    private String errorCode;

    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


    public RpcException(Throwable cause, String errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    // X => 技术类异常：
    // Y => 业务类异常：
    // Z => unknown, 搞不清楚，再归类到X或Y
    // TODO refactor to error enum
    public static final String SOCKET_TIMEOUT_EX = "X001" + "-" + "http_invoke_timeout";
    public static final String NO_SUCH_METHOD_EX = "X002" + "-" + "method_not_exists";
    public static final String UNKNOWN_EX = "Z001" + "-" + "unknown";


}
