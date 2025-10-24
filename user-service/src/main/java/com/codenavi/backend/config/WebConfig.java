package com.codenavi.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 전역 CORS 설정 (SecurityConfig와 연동)
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // ✅ 로컬 개발용 React (Vite, CRA 등)
                .allowedOrigins("http://localhost:3000")

                // ✅ EC2 서버에서 접근 허용 (Swagger 등)
                .allowedOrigins("http://43.203.237.132")

                // ✅ 추후 도메인 연결 시 여기에 추가
                // .allowedOrigins("https://yourdomain.com")

                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
