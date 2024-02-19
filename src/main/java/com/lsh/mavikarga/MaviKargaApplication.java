package com.lsh.mavikarga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MaviKargaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MaviKargaApplication.class, args);
	}

}
