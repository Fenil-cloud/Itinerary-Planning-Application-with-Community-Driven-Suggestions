package com.ltineraryplanning.authservice.service;

import com.ltineraryplanning.authservice.dto.*;
import jakarta.validation.Valid;

import java.text.ParseException;
import java.util.List;

public interface AuthService {

    ResponseDTO registerUser(@Valid RegisterRequest registerRequest);

    ResponseDTO login(LoginRequest loginRequest);

    ResponseDTO verify(String id, String token);

    ResponseDTO verifyMobile(String auth, String otp) throws ParseException;

    ResponseDTO resetPassword(String uid, String token,NewPasswordDTO newPasswordDTO);

    List<EmailAndFirstNameDTO> getEmailAndFirstName(List<String> usernames);
}
