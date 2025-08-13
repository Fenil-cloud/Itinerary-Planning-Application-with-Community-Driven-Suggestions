package com.ltineraryplanning.votingandpoll.Kafka.producer;

import com.ltineraryplanning.votingandpoll.Kafka.CommunitySuggestionPoll;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class kafkaProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafkaTopic.savePollDetails}")
    private String topic;

    @Value("${kafkaTopic.pollVote}")
    private String voteTopic;

    public void sendSuggestionPoll(PollDetailsDTO pollDetailsDTO){
//        log.info("Topic : {}", topic);
        log.info("Sending Poll for Auth <{}>",pollDetailsDTO);
        Message<PollDetailsDTO> message = MessageBuilder
                .withPayload(pollDetailsDTO)
                .setHeader(KafkaHeaders.TOPIC,topic)
                .build();
        kafkaTemplate.send(message);
    }

    public void sendSuggestionPollVote(VoteDataDTO voteDataDTO){
//        log.info("Topic : {}", topic);
        log.info("Sending Poll vote for Suggestion <{}>",voteDataDTO);
        Message<VoteDataDTO> message = MessageBuilder
                .withPayload(voteDataDTO)
                .setHeader(KafkaHeaders.TOPIC,voteTopic)
                .build();
        kafkaTemplate.send(message);
    }
}
