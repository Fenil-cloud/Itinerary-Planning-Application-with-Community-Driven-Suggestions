package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Document("suggestions")
public class Suggestion {
    @Id
    private String suggestionId;
    private String tripId;
    private String destinationName;
    private String userId;
    private String username;
    private String title;
    private String description;
    private List<String> tags;

    //  todo --- Voting ---

    private int upVotes;
    private int downVotes;
    private List<String> upVoters = new ArrayList<>();
    private List<String> downVoters = new ArrayList<>();

    // todo --- Vote Count ---
//    private int votes;
//    private List<String> voters;
    private List<Comment> comments;
    private String createdAt;
}
