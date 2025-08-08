package com.ltineraryplanning.authservice.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TwilioConfing {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @PostConstruct
    public void init() {
        System.out.println("Twilio SID: " + accountSid);
        System.out.println("Twilio Token: " + authToken);
        Twilio.init(accountSid, authToken);
    }

}
