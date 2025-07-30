package com.kosuri.stores;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories("com.kosuri.stores.*")
@ComponentScan(basePackages = { "com.kosuri.stores.*" })
@EntityScan("com.kosuri.stores.*")
@EnableTransactionManagement
@EnableScheduling
public class StoresApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoresApplication.class, args);
		System.out.println("Hello World - code deploy");
		System.out.println("Checking if push to git works");
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
