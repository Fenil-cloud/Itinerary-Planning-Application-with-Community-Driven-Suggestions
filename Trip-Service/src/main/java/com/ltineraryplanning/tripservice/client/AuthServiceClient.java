package com.ltineraryplanning.tripservice.client;

import com.ltineraryplanning.tripservice.dto.EmailAndFirstNameDTO;
import com.ltineraryplanning.tripservice.dto.ResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "auth-service" ,
        url = "${application.config.gateway-url}"
)
public interface AuthServiceClient {
    @GetMapping("/api/v1/auth/getEmailAndFirstName")
    List<EmailAndFirstNameDTO> getEmailAndFirstName(@RequestBody List<String> usernames);
}
