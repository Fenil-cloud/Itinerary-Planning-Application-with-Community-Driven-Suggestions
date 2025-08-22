package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.service;

import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto.ResponseDTO;

public interface AiService {

    ResponseDTO askAiResponse(String auth, String question);
}
