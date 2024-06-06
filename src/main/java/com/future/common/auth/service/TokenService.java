package com.future.common.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.common.auth.entity.AuthToken;
import com.future.common.auth.entity.AuthTokenType;
import com.future.common.auth.entity.User;
import com.future.common.auth.mapper.AuthTokenMapper;
import com.future.common.auth.mapper.UserMapper;
import com.future.common.constant.ErrorCodeConstant;
import com.future.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class TokenService {
    /**
     * access token的过期秒数
     */
    public final static int TtlAccessTokenInSeconds = 2 * 3600;
    /**
     * refresh token的过期秒数
     */
    public final static int TtlRefreshTokenInSeconds = 30 * 24 * 3600;

    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    AuthTokenMapper authTokenMapper;
    @Autowired
    UserMapper userMapper;

    /**
     * 分配token
     *
     * @param userId
     * @param type
     */
    String assign(Long userId, AuthTokenType type) {
        String token = UUID.randomUUID().toString();

        if (log.isDebugEnabled())
            log.debug("随机生成的token {} 类型 {} 用户id {}", token, type, userId);

        // userId+type是唯一的
        QueryWrapper<AuthToken> queryWrapper = Wrappers.query();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("`type`", type);
        AuthToken authToken = this.authTokenMapper.selectOne(queryWrapper);
        if (authToken == null) {
            authToken = new AuthToken();
            authToken.setUserId(userId);
            authToken.setType(type);
            authToken.setToken(token);
            authToken.setCreateTime(new Date());
            this.authTokenMapper.insert(authToken);

            if (log.isDebugEnabled())
                log.debug("插入token记录 token {}", authToken);
        } else {
            authToken.setToken(token);
            authToken.setCreateTime(new Date());
            this.authTokenMapper.updateById(authToken);

            if (log.isDebugEnabled())
                log.debug("token已存在，更新token {}", authToken);
        }

        if (log.isDebugEnabled())
            log.debug("成功保存token {} 类型 {} 用户id {} 到数据库", token, type, userId);

        return token;
    }

    public User validate(String token, AuthTokenType type) throws BusinessException {
        if (log.isDebugEnabled())
            log.debug("请求校验token {} 类型 {}", token, type);

        QueryWrapper<AuthToken> queryWrapper = Wrappers.query();
        queryWrapper.eq("token", token);
        queryWrapper.eq("`type`", type);
        AuthToken authToken = this.authTokenMapper.selectOne(queryWrapper);
        if (authToken == null) {
            if (log.isDebugEnabled())
                log.debug("token {} 类型 {} 不存在", token, type);

            throw new BusinessException("不存在token");
        }

        // 校验token是否过期
        this.validateExpired(authToken);

        if (log.isDebugEnabled())
            log.debug("成功校验通过token {} 类型 {}", token, type);

        Long userId = authToken.getUserId();

        if (log.isDebugEnabled())
            log.debug("token {} 类型 {} 对应的用户id {}", token, type, userId);

        return this.userMapper.selectById(userId);
    }

    /**
     * 刷新 access token
     *
     * @param refreshToken
     * @return
     * @throws BusinessException
     */
    public String refreshAccessToken(String refreshToken) throws BusinessException {
        QueryWrapper<AuthToken> queryWrapper = Wrappers.query();
        queryWrapper.eq("token", refreshToken);
        queryWrapper.eq("`type`", AuthTokenType.Refresh);
        AuthToken authToken = this.authTokenMapper.selectOne(queryWrapper);

        if (authToken == null) {
            if (log.isDebugEnabled())
                log.debug("不存在token {} 类型 {}", refreshToken, AuthTokenType.Refresh);

            throw new BusinessException("不存在token");
        }

        // 校验refresh token是否过期
        this.validateExpired(authToken);

        Long userId = authToken.getUserId();

        if (log.isDebugEnabled())
            log.debug("token {} 类型 {} 对应的用户id {}", refreshToken, AuthTokenType.Refresh, userId);

        // 校验access token是否过期，否则不能提前刷新
        queryWrapper = Wrappers.query();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("`type`", AuthTokenType.Access);
        authToken = this.authTokenMapper.selectOne(queryWrapper);
        boolean expired = false;
        try {
            this.validateExpired(authToken);
        } catch (BusinessException ex) {
            if (ex.getErrorCode() == ErrorCodeConstant.ErrorCodeTokenExpired)
                expired = true;
        }
        if (!expired)
            throw new BusinessException("不能提前刷新access token");

        return this.assign(userId, AuthTokenType.Access);
    }

    /**
     * 判断token是否已经过期
     *
     * @param authToken
     */
    private void validateExpired(AuthToken authToken) throws BusinessException {
        Assert.isTrue(authToken != null, "请指定token");

        Date createTime = authToken.getCreateTime();
        Date timeNow = new Date();
        long secondsDelta = (timeNow.getTime() - createTime.getTime()) / 1000L;
        int ttlInSeconds = authToken.getType() == AuthTokenType.Access ?
                this.getTtlAccessToken() : this.getTtlRefreshTokenInSeconds();
        if (secondsDelta > ttlInSeconds) {
            if (log.isDebugEnabled())
                log.debug("token {} 已经过期 {} 秒", authToken, secondsDelta - ttlInSeconds);

            throw new BusinessException(ErrorCodeConstant.ErrorCodeTokenExpired, "token已过期");
        }
    }

    /**
     * 获取access token的ttl秒数
     *
     * @return
     */
    public int getTtlAccessToken() {
        return TtlAccessTokenInSeconds;
    }

    /**
     * 获取refresh token的ttl秒数
     *
     * @return
     */
    public int getTtlRefreshTokenInSeconds() {
        return TtlRefreshTokenInSeconds;
    }
}
