package com.future.common.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;


@Data
@TableName(value = "auth_user", autoResultMap = true)
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String phone;
    private String nickname;
    // todo 防止从数据库加载此字段数据
    private String password;
    private Date createTime;
}
