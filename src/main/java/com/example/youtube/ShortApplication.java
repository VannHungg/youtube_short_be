package com.example.youtube;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ShortApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShortApplication.class, args);
	}

}
