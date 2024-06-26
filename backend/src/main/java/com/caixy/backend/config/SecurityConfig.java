package com.caixy.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * SpringbootSecurity配置
 *
 * @name: com.caixy.backend.config.SecurityConfig
 * @author: CAIXYPROMISE
 * @since: 2024-04-02 12:31
 **/
@Configuration
@EnableWebSecurity
public class SecurityConfig
{
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        http.csrf(AbstractHttpConfigurer::disable) // 使用新的配置方式禁用CSRF保护
                .authorizeRequests(authz -> authz
                        .anyRequest().permitAll() // 允许所有请求
                );
        return http.build();
    }
}
