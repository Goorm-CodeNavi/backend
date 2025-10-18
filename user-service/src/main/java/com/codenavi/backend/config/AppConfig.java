package com.codenavi.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 애플리케이션의 전반적인 설정을 담당하는 클래스입니다.
 */
@Configuration
public class AppConfig {

    /**
     * 외부 API를 호출하기 위한 HTTP 클라이언트인 RestTemplate을 Spring Bean으로 등록합니다.
     * 이렇게 등록해두면 다른 클래스에서 @Autowired를 통해 주입받아 사용할 수 있습니다.
     * @return RestTemplate 인스턴스
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

