package com.future.common.auth.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDto {
    private Long id;
    private String phone;
    private String nickname;
    private Date createTime;
}
