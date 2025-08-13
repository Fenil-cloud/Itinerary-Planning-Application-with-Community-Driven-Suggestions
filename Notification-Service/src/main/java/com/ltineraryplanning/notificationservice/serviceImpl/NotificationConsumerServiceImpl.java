package com.ltineraryplanning.notificationservice.serviceImpl;

import com.ltineraryplanning.notificationservice.record.AuthDto;
import com.ltineraryplanning.notificationservice.record.TripDto;
import com.ltineraryplanning.notificationservice.service.EmailService;
import com.ltineraryplanning.notificationservice.service.NotificationConsumerService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationConsumerServiceImpl implements NotificationConsumerService {
    @Autowired
    EmailService emailService;

    @KafkaListener(topics = "${kafkaTopic.topic}")
    @Override
    public void consumeAuthEmailTopic(AuthDto authDto) throws MessagingException {
        log.info("Consuming the message from Auth-Topic:: {} ",authDto);

        try {
            emailService.sendEmailVerification(
                    authDto.email(),
                    authDto.url(),
                    authDto.fname()
            );
        }catch (MessagingException exception){
            log.error("Email sending failed...{}",exception.getMessage());
        }
    }


    @KafkaListener(topics = "${kafkaTopic.trip}")
    @Override
    public void consumeUpcomingTripEmailTopic(TripDto tripDto) throws MessagingException {
        log.info("Consuming the message from Trip-Topic:: {} ",tripDto);
        try {
            emailService.sendUpcomingTripNotification(
                    tripDto.email(),
                    tripDto.destination(),
                    tripDto.fname(),
                    tripDto.startDate(),
                    tripDto.endDate(),
                    tripDto.tripName()
            );
        }catch (MessagingException exception){
            log.error("Email sending failed...{}",exception.getMessage());
        }

    }
}
