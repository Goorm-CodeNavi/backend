package com.codenavi.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = CodeNaviBackendApplication.class,
		properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
class CodeNaviBackendApplicationTests {

	@Test
	void contextLoads() {
	}
}
