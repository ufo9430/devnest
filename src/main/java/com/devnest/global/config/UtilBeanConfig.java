package com.devnest.global.config;

import com.devnest.board.util.TimeFormatter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilBeanConfig {
    @Bean
    public TimeFormatter timeAgoUtil(){
        return new TimeFormatter();
    }
}
