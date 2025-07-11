package com.tupperware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class AutenticacionApplication {

	public static void main(String[] args) {
		//.env
		Dotenv env = Dotenv.load();
		env.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		SpringApplication.run(AutenticacionApplication.class, args);
	}
	
}
