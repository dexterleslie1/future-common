package com.future.common.exception;


import java.io.IOException;

public class BusinessException extends IOException {
    private int errorCode;
    private String errorMessage;
    private Object data;

    public BusinessException(String errorMessage) {
        this(90000, errorMessage);
    }

    public BusinessException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = 0;
        this.errorMessage = null;
        this.data = null;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

