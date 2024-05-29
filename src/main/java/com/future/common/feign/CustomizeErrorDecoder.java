package com.future.common.feign;

import com.fasterxml.jackson.databind.JsonNode;
import com.future.common.exception.BusinessException;
import com.future.common.json.JSONUtil;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * openfeign自定义错误处理
 * 问题：在调用feign过程中，需要经常编写代码判断errorCode是否不等于0，是则编写代码抛出业务异常，否则继续执行当前业务代码
 * 解决：使用openfeign自定义错误处理后，调用feign不再需要编写代码判断errorCode
 */
public class CustomizeErrorDecoder extends ErrorDecoder.Default {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.BAD_REQUEST.value() ||
                response.status() == HttpStatus.FORBIDDEN.value() ||
                response.status() == HttpStatus.UNAUTHORIZED.value()) {
            String JSON = response.body().toString();
            try {
                JsonNode node = JSONUtil.ObjectMapperInstance.readTree(JSON);
                return new BusinessException(node.get("errorCode").asInt(), node.get("errorMessage").asText());
            } catch (IOException ex) {
                // 当发生http 400错误时，返回数据不为json格式，则继续使用系统默认处理错误
                response = response.toBuilder()
                        .status(response.status())
                        .reason(response.reason())
                        .request(response.request())
                        .headers(response.headers())
                        .body(JSON, Util.UTF_8)
                        .build();
                return super.decode(methodKey, response);
            }
        }
        return super.decode(methodKey, response);
    }
}