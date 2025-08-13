package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.service;

import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto.AddCommentDTO;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto.AddSuggestionDTO;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto.CommunitySuggestionPoll;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto.ResponseDTO;
import jakarta.validation.Valid;

import java.text.ParseException;

public interface SuggestionService {

    ResponseDTO add(String Auth, AddSuggestionDTO addSuggestionDTO, String tripID) throws ParseException;


    ResponseDTO postComment(String auth, String suggestionId, AddCommentDTO addCommentDTO) throws ParseException;

    ResponseDTO updateComment(String auth, String suggestionId, String commentId, AddCommentDTO addCommentDTO) throws ParseException;

    ResponseDTO deleteComment(String auth, String suggestionId, String commentId);

    ResponseDTO deleteSuggestion(String auth, String suggestionId);

    ResponseDTO viewSuggestion(String auth, String suggestionId);

    ResponseDTO viewMyAllSuggestion(String auth) throws ParseException;

    ResponseDTO createSuggestionPoll(String auth, @Valid CommunitySuggestionPoll communitySuggestionPoll, String suggestionId) throws ParseException ;

    ResponseDTO getAllPolls(String auth, String suggestionId);
}

