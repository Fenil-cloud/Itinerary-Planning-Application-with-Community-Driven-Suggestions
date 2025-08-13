package com.ltineraryplanning.votingandpoll.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public
class PollVotes {
    @Id
    @GeneratedValue
    private Long voterId;

    private String userId;

    private String userName;

    private LocalDateTime createdAt;

    private Long pollId;

    @ManyToOne
    @JoinColumn(name = "optionId", nullable = false)
    private PollOptions option;
}
