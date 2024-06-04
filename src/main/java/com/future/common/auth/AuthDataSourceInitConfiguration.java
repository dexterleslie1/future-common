package com.future.common.auth;

import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

public class AuthDataSourceInitConfiguration {
    /**
     * 创建DataSourceInitializer bean注入到spring容器SQL脚本才会被执行
     *
     * @param dataSource
     * @return
     */
    @Bean
    AuthDataSourceInitializer authDataSourceInitializer(DataSource dataSource) {
        return new AuthDataSourceInitializer(dataSource);
    }
}
