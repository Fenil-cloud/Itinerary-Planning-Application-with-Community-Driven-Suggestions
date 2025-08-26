package com.ltineraryplanning.notificationservice.service;

import com.ltineraryplanning.notificationservice.record.SendDestinationInNotification;
import jakarta.mail.MessagingException;

import java.time.LocalDate;
import java.util.List;

public interface EmailService {
    void sendEmailVerification(String toEmail,String url,String fname) throws MessagingException;
    void sendUpcomingTripNotification(String tripName, String fname, List<SendDestinationInNotification> destination, String startDate, String endDate, String toEmail) throws MessagingException;
    void sendResetLinkNotification(String url,String toEmail) throws MessagingException;

}
