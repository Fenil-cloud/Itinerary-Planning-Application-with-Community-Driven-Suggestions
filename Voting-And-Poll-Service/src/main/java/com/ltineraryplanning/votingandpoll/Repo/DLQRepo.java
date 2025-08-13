package com.ltineraryplanning.votingandpoll.Repo;

import com.ltineraryplanning.votingandpoll.Entity.MongoDB.DLQ;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DLQRepo extends MongoRepository<DLQ,String> {
}
