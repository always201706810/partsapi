// com/projectwz/partsforecast/PartsForecastApplication.java
package com.projectwz.partsforecast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.scheduling.annotation.EnableAsync; // 启用异步支持

@SpringBootApplication
@EnableAsync // 启用 @Async 注解
public class PartsForecastApplication {

	public static void main(String[] args) {
		SpringApplication.run(PartsForecastApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}