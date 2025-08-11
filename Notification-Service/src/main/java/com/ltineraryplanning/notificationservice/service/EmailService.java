package com.ltineraryplanning.notificationservice.service;

import com.ltineraryplanning.notificationservice.dto.SendDestinationInNotification;
import jakarta.mail.MessagingException;

import java.time.LocalDate;
import java.util.List;

public interface EmailService {
    void sendEmailVerification(String toEmail,String url,String fname) throws MessagingException;
    void sendUpcomingTripNotification(String tripName, String firstName, List<SendDestinationInNotification> destinations,LocalDate startDate, LocalDate endDate, String toEmail) throws MessagingException;
}
