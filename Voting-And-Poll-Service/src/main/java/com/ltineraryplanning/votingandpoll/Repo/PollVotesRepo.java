package com.ltineraryplanning.votingandpoll.Repo;

import com.ltineraryplanning.votingandpoll.Entity.PollVotes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PollVotesRepo extends JpaRepository<PollVotes,Long> {

    Optional<PollVotes> findByUserIdAndPollId(String userId, Long pollId);
//    Optional<PollVotes> findByPollId(Long pollId);
}
