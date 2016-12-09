package com.suhailkandanur.fresco;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class FrescoApplication {
	private static final Logger logger = LoggerFactory.getLogger(FrescoApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(FrescoApplication.class, args);
	}
}
