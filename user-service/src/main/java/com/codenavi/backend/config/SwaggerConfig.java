package com.codenavi.backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger (SpringDoc OpenAPI) 관련 설정을 위한 클래스입니다.
 */
@OpenAPIDefinition(
        info = @Info(title = "CodeNavi API 명세서",
                description = "CodeNavi 백엔드 서비스의 API 명세서입니다.",
                version = "v1.0.0"))
@Configuration
public class SwaggerConfig {

    /**
     * Swagger UI에 JWT 인증을 위한 'Authorize' 버튼을 추가합니다.
     * @return OpenAPI 객체
     */
    @Bean
    public OpenAPI openAPI() {
        // JWT 인증 스키마의 이름을 "bearerAuth"로 정의합니다.
        String securitySchemeName = "bearerAuth";

        // API 요청 헤더에 인증 정보를 담을 방식을 정의합니다.
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);

        Components components = new Components()
                .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP) // 인증 타입은 HTTP
                        .scheme("bearer")               // 스키마는 bearer
                        .bearerFormat("JWT"));          // 포맷은 JWT

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components);
    }
}
