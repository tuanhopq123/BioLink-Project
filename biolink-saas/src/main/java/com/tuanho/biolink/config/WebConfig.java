package com.tuanho.biolink.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**") // Cho phép tất cả các đường dẫn API
        .allowedOrigins("http://localhost:5173") // Chỉ cho phép Frontend của mình truy cập
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true) // QUAN TRỌNG: Cho phép gửi Cookie (Refresh Token)
        .maxAge(3600);
  }
}