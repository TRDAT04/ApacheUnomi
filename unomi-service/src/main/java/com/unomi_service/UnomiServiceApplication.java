package com.unomi_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class UnomiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UnomiServiceApplication.class, args);
	}

}
