package com.future.common.auth;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用 auth 插件注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AuthDataSourceInitConfiguration.class)
public @interface EnableFutureAuthPlugin {

}
