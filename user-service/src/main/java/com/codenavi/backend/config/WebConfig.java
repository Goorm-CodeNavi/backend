package com.codenavi.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 웹 관련 전역 설정을 위한 클래스입니다. (CORS 등)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 1. 모든 경로에 대해 CORS 설정을 적용합니다.
                .allowedOrigins("http://localhost:3000") // 2. React 개발 서버의 주소를 허용합니다.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 3. 허용할 HTTP 메소드를 지정합니다.
                .allowCredentials(true) // 4. 쿠키, 인증 헤더 등을 포함한 요청을 허용합니다.
                .allowedHeaders("*"); // 5. 모든 종류의 헤더를 허용합니다.
    }
}
