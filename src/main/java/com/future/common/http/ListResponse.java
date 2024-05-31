package com.future.common.http;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * List对象返回
 */
public class ListResponse<T> extends BaseResponse {
    @ApiModelProperty(value = "返回数据")
    private List<T> data;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
