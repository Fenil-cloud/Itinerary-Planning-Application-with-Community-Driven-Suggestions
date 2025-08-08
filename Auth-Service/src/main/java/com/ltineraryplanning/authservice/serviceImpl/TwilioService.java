package com.ltineraryplanning.authservice.serviceImpl;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {
    @Value("${twilio.phone.number}")
    private String phone;

    public String sendOTP(String phoneNumber,String body) {
        Message message = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(phone),
                body
        ).create();

        return message.getSid();


    }
}
