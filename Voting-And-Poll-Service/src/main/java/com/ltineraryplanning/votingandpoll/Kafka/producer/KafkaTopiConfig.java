package com.ltineraryplanning.votingandpoll.Kafka.producer;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopiConfig {
    @Value("${kafkaTopic.savePollDetails}")
    private String topic;

    @Value("${kafkaTopic.pollVote}")
    private String voteTopic;

    @Bean
    public NewTopic pollSuggestion(){
        return TopicBuilder.name(topic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic pollVote(){
        return TopicBuilder.name(voteTopic)
                .partitions(4)
                .replicas(2)
                .build();
    }
}
