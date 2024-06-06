package com.future.common.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.future.common.auth.entity.AuthToken;
import com.future.common.auth.entity.AuthTokenType;
import com.future.common.auth.entity.User;
import com.future.common.auth.mapper.AuthTokenMapper;
import com.future.common.auth.mapper.UserMapper;
import com.future.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class TokenService {
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

        // todo 保证一个userId只对应一个token+type
        AuthToken authToken = new AuthToken();
        authToken.setUserId(userId);
        authToken.setType(type);
        authToken.setToken(token);
        authToken.setCreateTime(new Date());
        this.authTokenMapper.insert(authToken);

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

        Long userId = authToken.getUserId();

        if (log.isDebugEnabled())
            log.debug("token {} 类型 {} 对应的用户id {}", refreshToken, AuthTokenType.Refresh, userId);

        return this.assign(userId, AuthTokenType.Access);
    }

//    String get(Long userId) {
//        return null;
//    }
}
