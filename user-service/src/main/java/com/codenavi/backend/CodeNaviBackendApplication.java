package com.codenavi.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CodeNaviBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(CodeNaviBackendApplication.class, args);
	}
}
