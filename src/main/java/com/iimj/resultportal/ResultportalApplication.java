package com.iimj.resultportal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.iimj.resultportal.config.RedisLoader;
import com.iimj.resultportal.service.CandidateService;

@SpringBootApplication
public class ResultportalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResultportalApplication.class, args);
	}

	@Bean
    CommandLineRunner loadCache(CandidateService candidatesService, RedisLoader redisLoader) {
        return args -> {
            redisLoader.loadAllCandidatesToCache();
        };
    }
	
}
