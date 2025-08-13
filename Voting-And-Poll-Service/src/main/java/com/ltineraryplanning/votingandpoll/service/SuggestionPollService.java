package com.ltineraryplanning.votingandpoll.service;

import com.ltineraryplanning.votingandpoll.Kafka.CommunitySuggestionPoll;
import com.ltineraryplanning.votingandpoll.dto.ResponseDTO;

import java.text.ParseException;

public interface SuggestionPollService {

//    ResponseDTO votePoll(Long pollId, Long optionID);

    ResponseDTO votePoll(Long pollId, Long optionID, String auth) throws ParseException;
}
