package com.future.common.http;


import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class PageResponse<T> extends BaseResponse {
    @ApiModelProperty(value = "当前页")
    private int pageNum;    //当前页,从请求那边传过来。
    @ApiModelProperty(value = "每页显示的数据数")
    private int pageSize;    //每页显示的数据条数。
    @ApiModelProperty(value = "总记录数")
    private int totalRecord;    //总的记录条数。查询数据库得到的数据
    @ApiModelProperty(value = "总页数")
    private int totalPage;    //总页数，通过totalRecord和pageSize计算可以得来


    //将每页要显示的数据放在list集合中
    private List<T> data;

    /**
     *
     */
    public PageResponse(){
    }

    //通过pageNum，pageSize，totalRecord计算得来tatalPage和startIndex
    //构造方法中将pageNum，pageSize，totalRecord获得
    public PageResponse(int pageNum, int pageSize, int totalRecord) {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalRecord = totalRecord;

        //totalPage 总页数
        if(totalRecord%pageSize==0){
            this.totalPage = totalRecord / pageSize;
        }else{
            this.totalPage = (totalRecord + pageSize) / pageSize;
        }
    }

    //通过pageNum，pageSize，totalRecord计算得来tatalPage和startIndex
    //构造方法中将pageNum,totalRecord获得
    public PageResponse(int pageNum, int totalRecord) {
        this.pageNum = pageNum;
        this.pageSize = 10;
        this.totalRecord = totalRecord;

        //totalPage 总页数
        if(totalRecord%pageSize==0){
            this.totalPage = totalRecord / pageSize;
        }else{
            this.totalPage = (totalRecord + pageSize) / pageSize;
        }
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(int totalRecord) {
        if(pageSize <= 0){
            throw new IllegalStateException("先设置每页记录数");
        }
        this.totalRecord = totalRecord;

        int totalPageTemporary = 0;
        if(this.totalRecord%this.pageSize == 0){
            totalPageTemporary = this.totalRecord/this.pageSize;
        }else {
            totalPageTemporary = (this.totalRecord+this.pageSize)/this.pageSize;
        }
        this.totalPage = totalPageTemporary;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
