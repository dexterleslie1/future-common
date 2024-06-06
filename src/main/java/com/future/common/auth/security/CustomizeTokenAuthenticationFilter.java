package com.future.common.auth.security;

import com.future.common.auth.config.FutureAuthProperties;
import com.future.common.auth.entity.AuthTokenType;
import com.future.common.auth.entity.User;
import com.future.common.auth.service.TokenService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// 验证用户是否登录拦截器
@Component
public class CustomizeTokenAuthenticationFilter extends OncePerRequestFilter {
    //    @Autowired
//    TokenStore tokenStore;
    @Autowired
    TokenService tokenService;
    @Autowired
    FutureAuthProperties futureAuthProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath().toLowerCase();
        // 不拦截的接口
        if (this.futureAuthProperties.getIgnoreUris() != null) {
            return this.futureAuthProperties.getIgnoreUris().contains(path)
                    || this.futureAuthProperties.getDefaultIgnoreUris().contains(path);
        } else
            return this.futureAuthProperties.getDefaultIgnoreUris().contains(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 获取请求中携带的token并在本地查询是否有此token，
        // 是，则构造Authentication对象并注入到请求上下文中
        String token = obtainBearerToken(request);
        if (!StringUtils.isBlank(token)) {
            User user = this.tokenService.validate(token, AuthTokenType.Access);

            if (user != null) {
                CustomizeUser customizeUser = new CustomizeUser(user.getId());
                CustomizeAuthentication authentication = new CustomizeAuthentication(customizeUser);
                authentication.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    String obtainBearerToken(HttpServletRequest request) {
        String bearerStr = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(bearerStr)) {
            return bearerStr;
        }

        return bearerStr.replace("Bearer ", "");
    }
}
