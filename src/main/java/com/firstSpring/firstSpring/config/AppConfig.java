package com.firstSpring.firstSpring.config;

import com.firstSpring.firstSpring.service.mappers.UserMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *  Configuration class to define a Bean of the mapper (to convert entities to dto's and the other way)
 *
 * @author Daniel Mora Cantillo
 */
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    @Bean
    public UserMapper userMapper() {
        return UserMapper.INSTANCE;
    }

}
