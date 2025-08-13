package com.ltineraryplanning.votingandpoll.Kafka;

import com.ltineraryplanning.votingandpoll.Entity.MongoDB.DLQ;
import com.ltineraryplanning.votingandpoll.Repo.DLQRepo;
import com.ltineraryplanning.votingandpoll.ServiceImpl.SaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class KafkaConsumer {

    @Autowired
    private DLQRepo dlqRepo;

    @Autowired
    private SaveService service;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;


    @KafkaListener(topics = "${kafkaTopic.sugPoll}")
    public void consumeSuggestionPollTopic(CommunitySuggestionPoll suggestionPoll, Acknowledgment ack){

        int retries = 3;
        String Exc = "";
        log.info("Consuming Suggestion-Topic:: {} ", suggestionPoll);
        while (retries-- > 0) {
            try {
            service.SaveNewPoll(suggestionPoll);
            ack.acknowledge();
            log.info("message proceeded success!");
            break;
        }catch (Exception e) {
                log.warn("Error While Consuming Topic :: {}", e.getMessage());
                Exc = e.getMessage();
                DLQ dlq = new DLQ();
                dlq.setTittle("SugPoll.DLQ");
                dlq.setObject(suggestionPoll);
                dlq.setException(Exc);
                dlq.setTimestamp(LocalDateTime.now().toString());
                dlqRepo.save(dlq);
                kafkaTemplate.send("newSuggestionPoll.DLQ",suggestionPoll);
        }
        }
//        log.info("Save Suggestion Poll DLQ.ERR");

    }

}
