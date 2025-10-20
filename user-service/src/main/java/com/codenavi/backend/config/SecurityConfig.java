package com.codenavi.backend.config;

import com.codenavi.backend.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
//// --- 👇 수정된 부분: 디버그 모드를 활성화합니다. ---
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationConfiguration authenticationConfiguration;


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
                // ✅ 기본 폼 로그인 / HTTP Basic 로그인 비활성화
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // ✅ CSRF / CORS 비활성화 (API 서버에서는 보통 이렇게)
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                // ✅ 세션 사용 안 함 (JWT는 무상태)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // ✅ 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 1. 인증 없이 모두 접근 가능한 경로
                        .requestMatchers(
                                "/",
                                "/error",
                                "/api/auth/**",
                                "/api/problems",     // 문제 리스트
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // 2. 인증된 사용자만 접근 가능한 경로
                        .requestMatchers(
                                "/api/problems/**", // 문제 상세, 추천, 실행, 해설 등
                                "/api/users/me/**", // 내 정보 관련
                                "/api/solutions/**" // 풀이 관련
                        ).authenticated()

                        // 3. 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

