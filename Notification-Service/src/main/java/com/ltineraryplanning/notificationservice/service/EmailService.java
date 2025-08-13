package com.ltineraryplanning.notificationservice.service;

import jakarta.mail.MessagingException;

import java.time.LocalDate;

public interface EmailService {
    void sendEmailVerification(String toEmail,String url,String fname) throws MessagingException;
    void sendUpcomingTripNotification(String tripName, String fname, String destination, LocalDate startDate, LocalDate endDate,String toEmail) throws MessagingException;
}
