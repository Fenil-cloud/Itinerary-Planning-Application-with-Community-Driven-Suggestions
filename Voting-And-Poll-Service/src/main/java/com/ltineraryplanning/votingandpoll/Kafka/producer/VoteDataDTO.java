package com.ltineraryplanning.votingandpoll.Kafka.producer;

import lombok.Data;

@Data
public class VoteDataDTO {
    private String pollId;
    private String optionId;
    private int maxVotes;
    private String maxVoteId;
}
