package com.future.common.http;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;

/**
 * 包装HttpServletRequest的工具类
 */
public class RequestUtils {
    /**
     * Bearer字符串前缀
     */
    public final static String BearerPrefix = "Bearer ";
    /**
     * 空字符床
     */
    public final static String EmptyString = "";

    public static String ObtainBearerToken(HttpServletRequest request) {
        String bearerStr = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(bearerStr)) {
            return bearerStr;
        }

        return bearerStr.replace(BearerPrefix, EmptyString);
    }
}
