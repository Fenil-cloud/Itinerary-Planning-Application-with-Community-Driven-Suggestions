package com.ltineraryplanning.authservice.dto;

//import com.bank.web.app.auth.Validation.UniqueEmailValidation;
import com.ltineraryplanning.authservice.enums.Gender;
import com.ltineraryplanning.authservice.enums.Role;
import com.ltineraryplanning.authservice.validation.EmailValidation;
import com.ltineraryplanning.authservice.validation.MobileNumValidation;
import com.ltineraryplanning.authservice.validation.UserNameValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {
//    @UniqueEmailValidation
    @NotBlank(message = "Username is required")
    @UserNameValidation
    private String username;
//    @UniqueEmailValidation
    @Pattern(regexp = "^(USER|user)$", message = "Role must be either 'USER' or 'user'")
    private String roles;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last is required")
    private String lastName;

    @EmailValidation
    @NotBlank(message = "Email is required")
    @Pattern(regexp = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.com$", message = "Email Not Valid Format")
    private String email;

    @MobileNumValidation
    @Pattern(regexp = "^\\+91\\d{10}$",message = "Contact Number Not Valid")
    private String phoneNumber;

    @Pattern(
            regexp = "^(?i)(male|female|other)$",
            message = "Gender must be male, female, or other (case-insensitive)"
    )
    private String gender;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$",message = "Password must be Strong and length between 8-15")
    private String password;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$",message = "Password must be Strong and length between 8-15")
    private String confirmPassword;
}

