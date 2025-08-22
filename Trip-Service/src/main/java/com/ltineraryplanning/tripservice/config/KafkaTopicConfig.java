//package com.ltineraryplanning.tripservice.config;
//
//import org.apache.kafka.clients.admin.NewTopic;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.config.TopicBuilder;
//
//@Configuration
//public class KafkaTopicConfig {
//
//    @Value("${kafkaTopic.createEventTopic}")
//    private String createEventTopic;
//
//    @Value("${kafkaTopic.updateEventTopic}")
//    private String updateEventTopic;
//
//
//    @Value("${kafkaTopic.shareEventForUpdate}")
//    private String shareEventForUpdate;
//    @Bean
//    public NewTopic createEventTopic(){
//        return TopicBuilder.name(createEventTopic)
//                .partitions(3)
//                .replicas(1)
//                .build();
//    }
//
//    @Bean
//    public NewTopic updateEventTopic(){
//        return TopicBuilder.name(updateEventTopic)
//                .partitions(3)
//                .replicas(1)
//                .build();
//    }
//
//
//    @Bean
//    public NewTopic shareEventForUpdate(){
//        return TopicBuilder.name(shareEventForUpdate)
//                .partitions(3)
//                .replicas(1)
//                .build();
//    }
//
//
//}
