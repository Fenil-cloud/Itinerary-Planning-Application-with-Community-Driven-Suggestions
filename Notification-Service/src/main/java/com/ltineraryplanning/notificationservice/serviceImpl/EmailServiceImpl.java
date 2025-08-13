package com.ltineraryplanning.notificationservice.serviceImpl;

import com.ltineraryplanning.notificationservice.enums.EmailTemplates;
import com.ltineraryplanning.notificationservice.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Async
    @Override
    public void sendEmailVerification(String toEmail, String url, String fname) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        messageHelper.setFrom("itineryservice.jdbc@service.com");
//        log.info("Template :: {}",EmailTemplates.AUTH_LINK.getTemplate());
        final String templateName = EmailTemplates.AUTH_LINK.getTemplate();
        Map<String,Object> variables = new HashMap<>();
        variables.put("email",toEmail);
        variables.put("url",url);
        variables.put("fname",fname);
        Context context = new Context();
        context.setVariables(variables);
        messageHelper.setSubject(EmailTemplates.AUTH_LINK.getSubject());
        try{
            String htmlTemplate = templateEngine.process(templateName,context);
            log.info(htmlTemplate);
            messageHelper.setText(htmlTemplate,true);
            messageHelper.setTo(toEmail);
            javaMailSender.send(mimeMessage);
            log.info("EMAIL-AUTH-LINK - Email sent to {} with template {} ",toEmail,templateName);

        }
        catch (MessagingException e){
            log.warn("WARNING - Can't send email to {}",toEmail);
        }
    }

    @Override
    public void sendUpcomingTripNotification(String tripName, String fname, String destination, LocalDate startDate, LocalDate endDate, String toEmail) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        messageHelper.setFrom("itineryservice.jdbc@service.com");
//        log.info("Template :: {}",EmailTemplates.AUTH_LINK.getTemplate());
        final String templateName = EmailTemplates.UPCOMING_TRIP_NOTIFICATION.getTemplate();
        Map<String,Object> variables = new HashMap<>();
        variables.put("email",toEmail);
        variables.put("tripName",tripName);
        variables.put("fname",fname);
        variables.put("destination",destination);
        variables.put("startDate",startDate);
        variables.put("endDate",endDate);
        Context context = new Context();
        context.setVariables(variables);
        messageHelper.setSubject(EmailTemplates.UPCOMING_TRIP_NOTIFICATION.getSubject());
        try{
            String htmlTemplate = templateEngine.process(templateName,context);
//            log.info(htmlTemplate);
            messageHelper.setText(htmlTemplate,true);
            messageHelper.setTo(toEmail);
            javaMailSender.send(mimeMessage);
            log.info("EMAIL-UPCOMING-TRIP - Email sent to {} with template {} ",toEmail,templateName);

        }
        catch (MessagingException e){
            log.warn("WARNING - Can't send email to {}",toEmail);
        }
    }
}
