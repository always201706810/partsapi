// com/projectwz/partsforecast/PartsForecastApplication.java
package com.projectwz.partsforecast;
import org.mybatis.spring.annotation.MapperScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.scheduling.annotation.EnableAsync; // 启用异步支持

@MapperScan("com.projectwz.partsforecast.mapper") // 已添加: 扫描 MyBatis 的 Mapper 接口
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