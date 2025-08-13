package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.service;

import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto.ResponseDTO;

import java.text.ParseException;

public interface VoteService {
    ResponseDTO likeSuggestion(String auth, String suggestionId) throws ParseException;

    ResponseDTO disLikeSuggestion(String auth, String suggestionId) throws ParseException;

    void removeVote(String id, String userId) throws ParseException;
}
