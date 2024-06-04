-- 用于初始化 auth 数据库

-- 创建 auth_user 表
CREATE TABLE `auth_user`
(
    id         BIGINT(20) NOT NULL auto_increment COMMENT '主键ID',
    phone      VARCHAR(64)  NOT NULL COMMENT '手机号码',
    nickname   VARCHAR(30) NULL DEFAULT NULL COMMENT '昵称',
    `password` VARCHAR(128) NOT NULL COMMENT '登录密码',
    createTime DATETIME     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=INNODB DEFAULT CHARSET=utf8mb4 collate=utf8mb4_general_ci;
