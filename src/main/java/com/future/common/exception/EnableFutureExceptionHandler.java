package com.future.common.exception;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 此注解启用future的全局异常处理特性
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ExceptionHandlerConfiguration.class})
public @interface EnableFutureExceptionHandler {

}
