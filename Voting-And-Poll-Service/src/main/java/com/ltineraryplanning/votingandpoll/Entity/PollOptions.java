package com.ltineraryplanning.votingandpoll.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class PollOptions {
    @Id
    @GeneratedValue
    private Long optionId;
    private String optionText;
    private Long votes;
    @ManyToOne
    @JoinColumn(name = "poll_id", nullable = false)
    private Polls poll;

    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PollVotes> pollVotes;
}
