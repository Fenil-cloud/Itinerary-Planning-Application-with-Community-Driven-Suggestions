package com.ltineraryplanning.authservice.dto;

//import com.bank.web.app.auth.Validation.UniqueEmailValidation;
import com.ltineraryplanning.authservice.enums.Gender;
import com.ltineraryplanning.authservice.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {
//    @UniqueEmailValidation
    private String username;
//    @UniqueEmailValidation
    private String roles;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String gender;
    private String password;
    private String confirmPassword;
}

