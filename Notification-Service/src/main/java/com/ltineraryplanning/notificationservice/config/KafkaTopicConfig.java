package com.ltineraryplanning.notificationservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopicConfig {
    @Value("${kafkaTopic.topic}")
    private String notificationTopicName;

    public String getNotificationTopicName(){
        return notificationTopicName;
    }
}

