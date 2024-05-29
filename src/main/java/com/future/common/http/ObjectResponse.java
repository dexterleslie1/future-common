package com.future.common.http;


import io.swagger.annotations.ApiModelProperty;

public class ObjectResponse<T> extends BaseResponse {
    @ApiModelProperty("返回数据")
    private T data;

    public ObjectResponse() {
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

