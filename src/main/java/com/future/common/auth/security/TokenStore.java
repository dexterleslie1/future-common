//package com.future.common.auth.security;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.future.common.auth.entity.User;
//import com.future.common.json.JSONUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class TokenStore {
//    @Autowired
//    RedisTemplate<String, String> redisTemplate;
//
//    public void store(String token, User user) throws JsonProcessingException {
//        this.redisTemplate.opsForValue().set(token, JSONUtil.ObjectMapperInstance.writeValueAsString(user));
//    }
//
//    public User get(String token) throws JsonProcessingException {
//        String JSON = this.redisTemplate.opsForValue().get(token);
//        if (StringUtils.isBlank(JSON)) {
//            return null;
//        }
//
//        return JSONUtil.ObjectMapperInstance.readValue(JSON, User.class);
//    }
//}
