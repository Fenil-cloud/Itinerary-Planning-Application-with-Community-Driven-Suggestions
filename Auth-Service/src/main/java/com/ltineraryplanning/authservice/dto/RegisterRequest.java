package com.ltineraryplanning.authservice.dto;

//import com.bank.web.app.auth.Validation.UniqueEmailValidation;
import com.ltineraryplanning.authservice.enums.Gender;
import com.ltineraryplanning.authservice.enums.Role;
import com.ltineraryplanning.authservice.validation.EmailValidation;
import com.ltineraryplanning.authservice.validation.MobileNumValidation;
import com.ltineraryplanning.authservice.validation.UserNameValidation;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.apache.kafka.common.security.token.delegation.DelegationToken;

import java.util.Set;

@Data
public class RegisterRequest {
//    @UniqueEmailValidation
    @UserNameValidation
    private String username;
//    @UniqueEmailValidation
    @Pattern(regexp = "^(USER|user|organizer|ORGANIZER)$", message = "Role must be one of 'USER', 'TRIP_ORGANIZER'")
    private String roles;
    private String firstName;
    private String lastName;
    @EmailValidation
    private String email;
    @MobileNumValidation
    private String phoneNumber;
    private String gender;
    private String password;
    private String confirmPassword;
}

