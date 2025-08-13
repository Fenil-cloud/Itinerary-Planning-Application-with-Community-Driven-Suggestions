package com.ltineraryplanning.votingandpoll.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
public class Polls {

    @Id
    @GeneratedValue
    private Long pollId;

    private String tripId;

    private String suggestionId;

    private String question;

    private String userName;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private String type;

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PollOptions> options;

}
