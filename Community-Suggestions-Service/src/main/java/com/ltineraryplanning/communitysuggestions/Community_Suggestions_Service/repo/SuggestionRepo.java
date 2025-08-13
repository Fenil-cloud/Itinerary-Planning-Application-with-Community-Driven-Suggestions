package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.repo;

import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.entity.Suggestion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SuggestionRepo extends MongoRepository<Suggestion,String> {

    List<Suggestion> findByUserId(String userId);
}
