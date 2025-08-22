package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service;

import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.ServiceImpl.AdvCommentImpl;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.ServiceImpl.SpringAIServiceImpl;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.entity.Suggestion;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.repo.SuggestionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableFeignClients
@EnableAsync
@EnableCaching
public class CommunitySuggestionsServiceApplication implements CommandLineRunner {

	@Autowired
	private SuggestionRepo suggestionRepo;

	@Autowired
	private AdvCommentImpl comment;

	@Autowired
	private SpringAIServiceImpl service;

	public static void main(String[] args) {
		SpringApplication.run(CommunitySuggestionsServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		System.out.println("--------------start---------------");
		service.addToVectorDb();
//		service.result();
//		System.out.println("--------------end--------------");
//		if(!suggestionRepo.existsById("test-1")){
//			Suggestion suggestion = new Suggestion();
//			suggestion.setSuggestionId("test-1");
//			suggestion.setTitle("GOA");
//			suggestion.setDescription("GOA trip");
//			suggestion.setCreatedAt(LocalDateTime.now().toString());
//			suggestion.setTripId("1");
//			suggestion.setUserId("USER1");
//			suggestionRepo.save(suggestion);
//			System.out.println("save....");
//		}
//		comment.addVote("202","253");

	}
}
