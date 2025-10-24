package com.codenavi.backend.config;

import com.codenavi.backend.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ✅ WebConfig에서 정의한 CORS 정책 사용
                .cors(Customizer.withDefaults())

                // ✅ CSRF 비활성화 (JWT 환경)
                .csrf(AbstractHttpConfigurer::disable)

                // ✅ 세션 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ 요청 경로별 접근 권한
                .authorizeHttpRequests(auth -> auth
                        // Swagger & OpenAPI 문서 허용
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/webjars/**"
                        ).permitAll()

                        // 로그인/회원가입 등 인증 관련 엔드포인트 허용
                        .requestMatchers("/api/auth/**").permitAll()

                        // 문제 목록·상세 조회는 로그인 없이도 허용
                        .requestMatchers(
                                "/api/problems",
                                "/api/problems/**"
                        ).permitAll()

                        // 그 외 요청은 인증 필요
                        .anyRequest().authenticated()
                );

        // ✅ JWT 필터 추가 (기본 인증 필터 앞에 배치)
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
