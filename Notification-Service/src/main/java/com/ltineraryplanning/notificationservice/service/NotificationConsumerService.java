package com.ltineraryplanning.notificationservice.service;

import com.ltineraryplanning.notificationservice.record.AuthDto;
import com.ltineraryplanning.notificationservice.record.ResetLink;
import com.ltineraryplanning.notificationservice.record.TripDto;
import jakarta.mail.MessagingException;

public interface NotificationConsumerService {
    void consumeAuthEmailTopic(AuthDto authDto) throws MessagingException;
    void consumeUpcomingTripEmailTopic(TripDto tripDto) throws  MessagingException;
    void consumeResetLinkTopic(ResetLink resetLink) throws  MessagingException;
}
