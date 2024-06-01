package com.future.common.http;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.net.InetSocketAddress;

@Slf4j
public class HttpUtil {

    final static String HeaderXForwardedFor = "x-forwarded-for";
    final static String IpValueUnknown = "unknown";
    final static String IpV6Loopback = "0:0:0:0:0:0:0:1";
    final static String IpV4Loopback = "127.0.0.1";

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader(HeaderXForwardedFor);
        if (!StringUtils.isBlank(ip) && !IpValueUnknown.equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.contains(",")) {
                ip = ip.split(",")[0];
            }
        }

        if (StringUtils.isBlank(ip) || IpValueUnknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return IpV6Loopback.equals(ip)?IpV4Loopback:ip;
    }

    /**
     * 从HttpServletRequest、ServerHttpRequest获取用户真实IP
     * https://blog.csdn.net/qq_32652767/article/details/112257082
     * @param request
     * @return
     */
    public static String getIpAddress(ServerHttpRequest request) {
        HttpHeaders headers = request.getHeaders();
        String ip = headers.getFirst(HeaderXForwardedFor);
        if (!StringUtils.isBlank(ip) && !IpValueUnknown.equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.contains(",")) {
                ip = ip.split(",")[0];
            }
        }

        if (StringUtils.isBlank(ip) || IpValueUnknown.equalsIgnoreCase(ip)) {
            InetSocketAddress inetSocketAddress = request.getRemoteAddress();
            ip = inetSocketAddress==null?IpV4Loopback:inetSocketAddress.getAddress().getHostAddress();
        }
        return IpV6Loopback.equals(ip)?IpV4Loopback:ip;
    }
}
