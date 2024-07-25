package com.future.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.future.common.constant.ErrorCodeConstant;
import com.future.common.http.ObjectResponse;
import com.future.common.json.JSONUtil;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 引用ExceptionController默认会自动设置以下properties
 * spring.mvc.throw-exception-if-no-handler-found=true
 * spring.resources.add-mappings=false
 */
@ControllerAdvice
@Slf4j
// 默认properties配置
@PropertySource("classpath:default-config-for-exception-controller.properties")
public class ExceptionController {

    @Autowired
    MessageSource messageSource;

    /**
     * 引用 ExceptionController 后自动配置 i18n MessageSource
     * 解决问题：避免调用者没有配置 MessageSource bean 导致运行时调用 MessageSource#getMessage报告没有找到code对应的i18n信息
     */
    @Configuration
    public static class Config {
        @Bean
        public MessageSource messageSource() {
            ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
            messageSource.addBasenames("classpath:i18n/messages-common");
            messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
            return messageSource;
        }
    }

    /**
     * @param e
     * @return
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public @ResponseBody
    ResponseEntity<ObjectResponse<String>> handleIllegalArgumentException(IllegalArgumentException e) {
        if(log.isDebugEnabled())
            log.debug("参数校验失败，message={}", e.getMessage());

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
        response.setErrorMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON_UTF8).body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public @ResponseBody
    ResponseEntity<ObjectResponse<String>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
        String message = this.messageSource.getMessage("common.missing.required.parameter", new String[]{e.getParameterName()}, LocaleContextHolder.getLocale());

        if(log.isDebugEnabled())
            log.debug("参数校验失败，缺失必要的参数，message={}", message);

        response.setErrorMessage(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON_UTF8).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public @ResponseBody
    ResponseEntity<ObjectResponse<String>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
        String message = this.messageSource.getMessage("common.parameter.type.mismatch", new String[]{e.getName(), String.valueOf(e.getValue())}, LocaleContextHolder.getLocale());

        if(log.isDebugEnabled())
            log.debug("参数类型不匹配导致转换失败，message={}", message);

        response.setErrorMessage(message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON_UTF8).body(response);
    }

    /**
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public @ResponseBody
    ResponseEntity<ObjectResponse<Object>> handleBusinessException(BusinessException e) {
        ObjectResponse<Object> response = new ObjectResponse<>();
        response.setErrorCode(e.getErrorCode());
        response.setErrorMessage(e.getMessage());
        response.setData(e.getData());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON_UTF8).body(response);
    }

    /**
     * @param e
     * @return
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseBody
    public ResponseEntity<ObjectResponse<String>> handleNotFound(NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        ObjectResponse<String> response = new ObjectResponse<>();

        HttpServletRequest request =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                        .getRequest();
        Locale locale = Locale.CHINA;
        String lang = request.getParameter("lang");
        if (!StringUtils.isBlank(lang)) {
            lang = lang.replace("-", "_");
            try {
                locale = LocaleUtils.toLocale(lang);
            } catch (Exception ignored) {

            }
        }

        String message = this.messageSource.getMessage("common.resource.not.found", new String[]{e.getRequestURL()}, locale);
        response.setErrorMessage(message);
        response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON_UTF8).body(response);
    }

    /**
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ObjectResponse<String>> handleException(
            HttpServletRequest request,
            Exception e) throws Exception {
        // AccessDeniedException和AuthenticationException不需要全局异常处理
        // AccessDeniedException由spring-security AccessDeniedHandler处理
        // AuthenticationException由spring-security AuthenticationEntryPoint处理
        //
        // https://stackoverflow.com/questions/72615257/spring-accessdeniedhandler-interface-dont-get-called-when-i-have-exceptionhandl
        // https://github.com/spring-projects/spring-security/issues/6908
        // https://github.com/Allurx/spring-security-demo/blob/master/src/main/java/red/zyc/security/handler/GlobalExceptionHandler.java
        if (e instanceof AccessDeniedException
                || e instanceof AuthenticationException) {
            throw e;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(";" + e.getMessage());

        String queryString = request.getQueryString();
        if (queryString != null) {
            builder.append(";").append(queryString);
        }

        Map<String, String[]> parameters = request.getParameterMap();
        if (parameters != null && parameters.size() > 0) {
            builder.append(";params=");
            Set<String> keys = parameters.keySet();
            int size = keys.size();
            int counter = 0;
            for (String key : keys) {
                String[] arrayTemporary = parameters.get(key);
                builder.append(key + "=" + (arrayTemporary == null ? "" : String.join(",", arrayTemporary)));
                if (counter + 1 < size) {
                    builder.append(",");
                }
                counter++;
            }
        }
        log.error(builder.toString(), e);

        // 专门处理HystrixRuntimeException并且cause为FeignException异常
        if (e instanceof HystrixRuntimeException && (e.getCause() != null && e.getCause() instanceof FeignException)) {
            e = (FeignException) e.getCause();
        }

        int errorCode = ErrorCodeConstant.ErrorCodeCommon;
        String errorMessage;
        int httpStatus = HttpStatus.BAD_REQUEST.value();
        if (e instanceof ResourceAccessException) {
            errorMessage = this.messageSource.getMessage("common.server.busy.retry.later", null, LocaleContextHolder.getLocale());
        } else if (e instanceof FeignException) {
            FeignException feignException = (FeignException) e;
            httpStatus = feignException.status();
            ByteBuffer byteBuffer = feignException.responseBody().orElse(null);
            if (byteBuffer != null && byteBuffer.array().length > 0) {
                String resonseStr = new String(byteBuffer.array(), StandardCharsets.UTF_8);
                if (StringUtils.isBlank(resonseStr)) {
                    errorMessage = this.messageSource.getMessage("common.server.busy.retry.later", null, LocaleContextHolder.getLocale());
                } else {
                    try {
                        ObjectResponse<String> responseTemporary = JSONUtil.ObjectMapperInstance.readValue(resonseStr, new TypeReference<ObjectResponse<String>>() {
                        });
                        errorCode = responseTemporary.getErrorCode();
                        errorMessage = responseTemporary.getErrorMessage();
                    } catch (JsonProcessingException jsonProcessingException) {
                        errorMessage = this.messageSource.getMessage("common.server.busy.retry.later", null, LocaleContextHolder.getLocale());
                    }
                }
            } else {
                errorMessage = this.messageSource.getMessage("common.server.busy.retry.later", null, LocaleContextHolder.getLocale());
            }
        } else if (e instanceof HttpClientErrorException) {
            HttpClientErrorException httpClientErrorException = (HttpClientErrorException) e;
            httpStatus = httpClientErrorException.getRawStatusCode();
            String str = httpClientErrorException.getResponseBodyAsString(StandardCharsets.UTF_8);
            if (StringUtils.isBlank(str)) {
                errorMessage = this.messageSource.getMessage("common.server.busy.retry.later", null, LocaleContextHolder.getLocale());
            } else {
                try {
                    ObjectResponse<String> responseTemporary = JSONUtil.ObjectMapperInstance.readValue(str, new TypeReference<ObjectResponse<String>>() {
                    });
                    errorCode = responseTemporary.getErrorCode();
                    errorMessage = responseTemporary.getErrorMessage();
                } catch (JsonProcessingException jsonProcessingException) {
                    errorMessage = this.messageSource.getMessage("common.server.busy.retry.later", null, LocaleContextHolder.getLocale());
                }
            }
        } else {
            errorMessage = this.messageSource.getMessage("common.server.busy.retry.later", null, LocaleContextHolder.getLocale());
        }

        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        return ResponseEntity.status(httpStatus).contentType(MediaType.APPLICATION_JSON_UTF8).body(response);
    }

    // 文件上传大小超过服务器限制异常处理
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseEntity<ObjectResponse<String>> handleError(MaxUploadSizeExceededException e) {
        log.error(e.getMessage(), e);

        Throwable cause = e.getRootCause();
        String message;
        if (cause instanceof SizeLimitExceededException) {
            SizeLimitExceededException exception = (SizeLimitExceededException) cause;
            long permittedSize = exception.getPermittedSize();
            message = this.messageSource.getMessage("common.file.upload.size.limit.exceeded", new String[]{String.valueOf(permittedSize / 1024 / 1024)}, LocaleContextHolder.getLocale());
        } else if (cause instanceof FileSizeLimitExceededException) {
            FileSizeLimitExceededException exception = (FileSizeLimitExceededException) cause;
            long permittedSize = exception.getPermittedSize();
            String fileName = exception.getFileName();
            message = this.messageSource.getMessage("common.file.upload.single.file.size.limit.exceeded", new String[]{fileName, String.valueOf(permittedSize / 1024 / 1024)}, LocaleContextHolder.getLocale());
        } else {
            message = this.messageSource.getMessage("common.file.upload.max.upload.size.exceeded", null, LocaleContextHolder.getLocale());
        }
        ObjectResponse<String> response = new ObjectResponse<>();
        response.setErrorMessage(message);
        response.setErrorCode(ErrorCodeConstant.ErrorCodeCommon);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON_UTF8).body(response);
    }
}
