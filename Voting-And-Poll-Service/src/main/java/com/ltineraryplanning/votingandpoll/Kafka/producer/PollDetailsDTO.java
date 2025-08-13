package com.ltineraryplanning.votingandpoll.Kafka.producer;

import com.ltineraryplanning.votingandpoll.Entity.Polls;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PollDetailsDTO {
    private String pollID;
    private String suggestionId;
    private String tripId;
    private String question;
    private String createdAt;
    private String expireTime;
    private List<Options> options;

}
