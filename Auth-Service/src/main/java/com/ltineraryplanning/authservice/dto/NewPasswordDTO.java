package com.ltineraryplanning.authservice.dto;

import lombok.Data;

@Data
public class NewPasswordDTO {
    private String password;
    private String confirmPassword;
}
