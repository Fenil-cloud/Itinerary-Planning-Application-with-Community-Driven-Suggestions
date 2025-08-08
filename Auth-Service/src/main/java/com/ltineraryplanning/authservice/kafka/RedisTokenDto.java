package com.ltineraryplanning.authservice.kafka;

import lombok.Data;

@Data
public class RedisTokenDto {

    private String id;
    private String refresh_token;
    private String access_token;
}