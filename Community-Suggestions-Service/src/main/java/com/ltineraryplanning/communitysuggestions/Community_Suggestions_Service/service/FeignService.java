package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.text.ParseException;

@FeignClient(name = "GATEWAY-SERVICE")
public interface FeignService {

    @GetMapping("/api/v1/auth/username")
    String getUserName(@RequestHeader("Authorization") String auth);
}
