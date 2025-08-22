package com.ltineraryplanning.authservice.kafka;

import lombok.Data;

@Data
public class ResetLink {
    private String url;
    private String email;

}
