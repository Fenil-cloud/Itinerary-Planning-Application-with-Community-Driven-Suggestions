package com.ltineraryplanning.notificationservice.service;

import com.ltineraryplanning.notificationservice.record.AuthDto;
import com.ltineraryplanning.notificationservice.record.NotificationDTO;
import jakarta.mail.MessagingException;

public interface NotificationConsumerService {
    void consumeAuthEmailTopic(AuthDto authDto) throws MessagingException;
    void consumeUpcomingTripEmailTopic(NotificationDTO notificationDTO) throws  MessagingException;
}
