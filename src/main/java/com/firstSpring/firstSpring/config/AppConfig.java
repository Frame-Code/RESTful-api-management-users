package com.firstSpring.firstSpring.config;

import com.firstSpring.firstSpring.service.mappers.UserMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Artist-Code
 */
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    @Bean
    public UserMapper userMapper() {
        return UserMapper.INSTANCE;
    }

}
