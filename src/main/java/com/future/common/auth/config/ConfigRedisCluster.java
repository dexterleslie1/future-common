package com.future.common.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:default-config-for-redis-cluster.properties")
public class ConfigRedisCluster {
}
