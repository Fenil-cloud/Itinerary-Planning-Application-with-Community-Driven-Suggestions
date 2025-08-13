package com.ltineraryplanning.votingandpoll.Repo;

import com.ltineraryplanning.votingandpoll.Entity.PollOptions;
import com.ltineraryplanning.votingandpoll.Entity.PollVotes;
import com.ltineraryplanning.votingandpoll.Entity.Polls;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PollOptionsRepo extends JpaRepository<PollOptions,Long> {

//    Optional<PollOptions> findTopByPoll_PollIdOrderByVotesDesc(Long pollId);
     List<PollOptions> findByPoll(Polls poll);


}
