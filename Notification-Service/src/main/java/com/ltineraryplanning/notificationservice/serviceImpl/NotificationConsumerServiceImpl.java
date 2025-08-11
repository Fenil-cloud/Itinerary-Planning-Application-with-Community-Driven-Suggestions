package com.ltineraryplanning.notificationservice.serviceImpl;

import com.ltineraryplanning.notificationservice.dto.EmailAndFirstNameDTO;
import com.ltineraryplanning.notificationservice.record.AuthDto;
import com.ltineraryplanning.notificationservice.record.NotificationDTO;

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
    public void consumeUpcomingTripEmailTopic(NotificationDTO notificationDTO) throws MessagingException {
        log.info("Consuming the message from Trip-Topic:: {} ",notificationDTO);
        for (EmailAndFirstNameDTO recipient : notificationDTO.emailAndFirstName()) {
            try {
                emailService.sendUpcomingTripNotification(
                        notificationDTO.tripName(),        // tripName
                        recipient.getFirstName(),          // fname
                        notificationDTO.destinations(),    // destinations
                        notificationDTO.tripStartDate(),   // startDate
                        notificationDTO.tripEndDate(),     // endDate
                        recipient.getEmail()
                );
            } catch (MessagingException exception) {
                log.error("Email sending failed for {}... {}", recipient.getEmail(), exception.getMessage());
            }
        }
    }
}
