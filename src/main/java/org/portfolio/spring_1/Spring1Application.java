package org.portfolio.spring_1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Spring1Application {
	public static void main(String[] args) {
		SpringApplication.run(Spring1Application.class, args);
	}
}
