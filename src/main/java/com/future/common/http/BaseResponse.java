package com.future.common.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModelProperty;

public abstract class BaseResponse {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    @ApiModelProperty("错误代码")
    private int errorCode = 0;
    @ApiModelProperty("错误信息")
    private String errorMessage = null;

    public BaseResponse() {
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String toString() {
        try {
            String JSON = OBJECT_MAPPER.writeValueAsString(this);
            return JSON;
        } catch (JsonProcessingException var2) {
            return super.toString();
        }
    }
}