package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.kafka.consumer;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document("Poll")
public class PollDetailsDTO {
    @Id
    private String pollID;
    private String suggestionId;
    private String tripId;
    private String question;
    private String createdAt;
    private String expireTime;
    private int maxVotes;
    private String maxVoteOptionID;
    private List<Options> options;
}
