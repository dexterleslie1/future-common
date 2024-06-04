package com.future.common.auth;

import org.springframework.context.annotation.Bean;

public class AuthConfiguration {
    /**
     * 创建短信相关业务bean
     *
     * @return
     */
    @Bean
    SmsService smsService() {
        return new SmsService();
    }

    /**
     * 创建用户相关业务bean
     *
     * @return
     */
    @Bean
    UserService userService() {
        return new UserService();
    }

    /**
     * 验证码相关业务
     *
     * @return
     */
    @Bean
    VerificationCodeService verificationCodeService() {
        return new VerificationCodeService();
    }
}
