package com.ltineraryplanning.authservice.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
//    return TopicBuilder.name("transactionEmailTopic")
//            .partitions(6)
//    .replicas(1) // 👈 This line
//    .build();

    @Value("${kafkaTopic.topic}")
    private String topic;

    @Bean
    public NewTopic authTopic(){
        return TopicBuilder.name(topic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
