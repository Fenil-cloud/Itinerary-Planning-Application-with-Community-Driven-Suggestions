package com.ltineraryplanning.votingandpoll.Kafka;

import lombok.Data;

import java.util.List;

@Data
public class CommunitySuggestionPoll {
    private String suggestionID;
    private String tripId;
    private String question;
    private int expireTime;
    private String type;
    private List<String> options;
    private String uId;
    private String userName;
}
