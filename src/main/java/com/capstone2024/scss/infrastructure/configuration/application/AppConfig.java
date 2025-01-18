package com.capstone2024.scss.infrastructure.configuration.application;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.TimeZone;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @PostConstruct
//    void started() {
//        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
//    }
}
