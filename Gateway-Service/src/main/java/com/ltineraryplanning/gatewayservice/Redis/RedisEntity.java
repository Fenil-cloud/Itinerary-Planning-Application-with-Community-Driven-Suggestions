package com.ltineraryplanning.gatewayservice.Redis;


import lombok.Data;

import java.io.Serializable;

@Data
public class RedisEntity implements Serializable {

    String access_token;
    String refresh_token;
}
