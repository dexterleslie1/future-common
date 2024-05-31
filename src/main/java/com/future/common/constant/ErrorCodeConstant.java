package com.future.common.constant;

/**
 * 错误代码常量
 * 为了解决多个系统之间需要统一错误代码问题
 */
public class ErrorCodeConstant {
    /**
     * 不需要返回特别错误代码的情况，使用这个公共错误代码
     */
    public final static int ErrorCodeCommon = 90000;
    /**
     * 前端可忽略错误代码
     */
    public final static int ErrorCodeIgnored = 91000;
    /**
     * 需要重新登录
     */
    public final static int ErrorCodeLoginRequired = 90001;
    /**
     * token已过期
     * 使用jwt token时，遇到 TokenExpiredException 时表示提供token已经过期
     */
    public final static int ErrorCodeTokenExpired = 90006;
    /**
     * 非法token
     * 针对jwt token，遇到 JWTDecodeException 时表示提供非法token
     */
    public final static int ErrorCodeInvalidToken = 90007;
    /**
     * 瞬时保留token不能用于刷新token
     */
    public final static int ErrorCodeTransientReserveTokenRefreshForbidden = 90002;
    /**
     * 并发请求token
     */
    public final static int ErrorCodeConcurrentRequestTokenForbidden = 90003;
    /**
     * 刷新token间隔小于最小值
     */
    public final static int ErrorCodeLessThanMinimumRefreshInterval = 90004;
}
