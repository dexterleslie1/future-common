-- 用于初始化 auth 数据库

-- 创建用户表
CREATE TABLE IF NOT EXISTS `auth_user`
(
    id         BIGINT(20) NOT NULL auto_increment COMMENT '主键ID',
    phone      VARCHAR(64)  NOT NULL COMMENT '手机号码',
    nickname   VARCHAR(30) NULL DEFAULT NULL COMMENT '昵称',
    `password` VARCHAR(128) NOT NULL COMMENT '登录密码',
    createTime DATETIME     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

-- 创建token表
create table if not exists `auth_token`
(
    id      bigint(20) not null primary key auto_increment comment '主键',
    userId  bigint(20) not null comment '用户id',
    `type`  varchar(16) not null comment 'token类型：refresh和access token',
    token   varchar(64) not null comment 'token的值',
    createTime datetime not null comment '创建时间'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;

-- 创建历史token表，用于记录用户的历史token
/*create table if not exists `auth_token_assign_history`
(
    id  bigint(20) not null primary key auto_increment comment '主键',
    userId  bigint(20) not null comment '用户id',
    `type`  varchar(16) not null comment 'token类型：refresh和access token',
    token   varchar(64) not null comment 'token的值',
    createTime datetime not null comment '创建时间'
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;*/
