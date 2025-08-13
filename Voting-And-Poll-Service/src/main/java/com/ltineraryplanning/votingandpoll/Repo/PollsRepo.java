package com.ltineraryplanning.votingandpoll.Repo;

import com.ltineraryplanning.votingandpoll.Entity.Polls;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollsRepo extends JpaRepository<Polls,Long> {
}
