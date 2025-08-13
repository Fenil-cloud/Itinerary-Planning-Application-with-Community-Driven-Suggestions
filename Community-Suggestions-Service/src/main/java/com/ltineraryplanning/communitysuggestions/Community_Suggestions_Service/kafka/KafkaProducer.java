package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.kafka;

import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto.CommunitySuggestionPoll;
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
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, CommunitySuggestionPoll> kafkaTemplate;

    @Value("${KafkaTopic.sugPoll}")
    private String topic;

    public void sendSuggestionPoll(CommunitySuggestionPoll communitySuggestionPoll){
//        log.info("Topic : {}", topic);
        log.info("Sending Poll for Auth <{}>",communitySuggestionPoll);
        Message<CommunitySuggestionPoll> message = MessageBuilder
                .withPayload(communitySuggestionPoll)
                .setHeader(KafkaHeaders.TOPIC,topic)
                .build();
        kafkaTemplate.send(message);
    }
}
