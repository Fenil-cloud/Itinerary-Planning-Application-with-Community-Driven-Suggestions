package com.ltineraryplanning.votingandpoll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class VotingAndPollServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VotingAndPollServiceApplication.class, args);
	}

}
