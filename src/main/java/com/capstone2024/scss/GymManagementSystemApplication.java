package com.capstone2024.scss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GymManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(GymManagementSystemApplication.class, args);
	}

}
