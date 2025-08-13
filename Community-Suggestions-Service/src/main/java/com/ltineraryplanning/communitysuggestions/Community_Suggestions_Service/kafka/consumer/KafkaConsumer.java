package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.kafka.consumer;

import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.ServiceImpl.AdvCommentImpl;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto.CommunitySuggestionPoll;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.entity.DLQ;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.entity.Suggestion;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.repo.DLQRepo;
import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.repo.PollRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class KafkaConsumer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private PollRepo pollRepo;

    @Autowired
    private DLQRepo dlqRepo;

    @Autowired
    private AdvCommentImpl advQuery;


    @KafkaListener(topics = "${KafkaTopic.savePollDetails}")
    public void ConsumeSavePollTopic(PollDetailsDTO pollDetailsDTO, Acknowledgment ack){

        int retries = 3;
        String Exc = "";
        log.info("Consuming Suggestion-Topic:: {} ", pollDetailsDTO);
        while (retries-- > 0) {
            try {
//                service.SaveNewPoll(suggestionPoll);
                pollRepo.save(pollDetailsDTO);
                ack.acknowledge();
                log.info("message proceeded success!");
                break;
            }catch (Exception e) {
                log.warn("Error While Consuming Topic :: {}", e.getMessage());
                Exc = e.getMessage();
                log.info("Save Suggestion Poll DLQ.ERR");
                DLQ dlq = new DLQ();
                dlq.setTittle("SugPoll.DLQ");
                dlq.setObject(pollDetailsDTO);
                dlq.setException(Exc);
                dlq.setTimestamp(LocalDateTime.now().toString());
                dlqRepo.save(dlq);
                kafkaTemplate.send("newSuggestionPoll.DLQ",pollDetailsDTO);
            }
        }

    }

    @KafkaListener(topics = "${KafkaTopic.pollVote}")
    public void PollVoteTopic(VoteDataDTO voteDataDTO, Acknowledgment ack){

        int retries = 3;
        String Exc = "";
        log.info("Consuming Vote Details Topic:: {} ", voteDataDTO);
        while (retries-- > 0) {
            try {

//                pollRepo.save(voteDataDTO);
                advQuery.addVote(voteDataDTO.getPollId(),voteDataDTO.getOptionId());
                Optional<PollDetailsDTO> pollDetailsDTO = pollRepo.findById(voteDataDTO.getPollId());
                pollDetailsDTO.get().setMaxVoteOptionID(voteDataDTO.getMaxVoteId());
                pollDetailsDTO.get().setMaxVotes(voteDataDTO.getMaxVotes());
                pollRepo.save(pollDetailsDTO.get());
                ack.acknowledge();
                log.info("vote proceeded success!");
                break;
            }catch (Exception e) {
                log.warn("Error While Consuming Topic-pollVote :: {}", e.getMessage());
                Exc = e.getMessage();
                log.info("Save Suggestion Poll DLQ");
                DLQ dlq = new DLQ();
                dlq.setTittle("SugPoll.DLQ");
                dlq.setObject(voteDataDTO);
                dlq.setException(Exc);
                dlq.setTimestamp(LocalDateTime.now().toString());
                dlqRepo.save(dlq);
                kafkaTemplate.send("voteDetails.DLQ",voteDataDTO);
            }
        }

    }
}
