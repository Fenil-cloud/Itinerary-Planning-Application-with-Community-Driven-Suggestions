package com.ltineraryplanning.notificationservice.serviceImpl;

import com.ltineraryplanning.notificationservice.record.*;
import com.ltineraryplanning.notificationservice.service.EmailService;
import com.ltineraryplanning.notificationservice.service.NotificationConsumerService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

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
        try {
            log.info("DTO ::{}",notificationDTO);
            List<EmailAndFirstNameDTO> emailAndName= notificationDTO.getEmailAndFirstName();
            for (EmailAndFirstNameDTO data : emailAndName){
                emailService.sendUpcomingTripNotification(
                        notificationDTO.getTripName(),
                        data.getFirstName(),
                        notificationDTO.getDestinations(),
                        notificationDTO.getTripStartDate().toString(),
                        notificationDTO.getTripEndDate().toString(),
                        data.getEmail());
            }
//            emailService.sendUpcomingTripNotification(
//                    notificationDTO.getEmailAndFirstName().get(0).getEmail(),
//                    String.valueOf(notificationDTO.getDestinations().get(0)),
//                    notificationDTO.getEmailAndFirstName().get(0).getFirstName(),
//                    notificationDTO.getTripStartDate(),
//                    notificationDTO.getTripEndDate(),
//                    notificationDTO.getTripName()
//            );
        }catch (Exception exception){
            log.error("Email sending failed...{}",exception.getMessage());
        }

    }

    @KafkaListener(topics = "${kafkaTopic.reset}")
    @Override
    public void consumeResetLinkTopic(ResetLink resetLink) throws MessagingException {
        log.info("Consuming the message from Reset-Topic:: {} ",resetLink);
        emailService.sendResetLinkNotification(resetLink.getUrl(),resetLink.getEmail());
    }
}
