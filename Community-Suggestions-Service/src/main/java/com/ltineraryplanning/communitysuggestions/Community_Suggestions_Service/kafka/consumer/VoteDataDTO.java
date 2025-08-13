package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.kafka.consumer;

import lombok.Data;

@Data
public class VoteDataDTO {
    private String pollId;
    private String optionId;
    private int maxVotes;
    private String maxVoteId;
}
