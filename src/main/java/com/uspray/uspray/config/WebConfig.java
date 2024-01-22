package com.uspray.uspray.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/login/oauth2/code/apple")
            .allowedOrigins("https://appleid.apple.com")
            .allowedMethods("POST")
            .allowCredentials(true);
    }
}

