package com.ltineraryplanning.authservice.service;

import com.ltineraryplanning.authservice.dto.LoginRequest;
import com.ltineraryplanning.authservice.dto.NewPasswordDTO;
import com.ltineraryplanning.authservice.dto.RegisterRequest;
import com.ltineraryplanning.authservice.dto.ResponseDTO;
import jakarta.validation.Valid;

import java.text.ParseException;

public interface AuthService {

    ResponseDTO registerUser(@Valid RegisterRequest registerRequest);

    ResponseDTO login(LoginRequest loginRequest);

    ResponseDTO verify(String id, String token);

    ResponseDTO verifyMobile(String auth, String otp) throws ParseException;

    ResponseDTO resetPassword(String uid, String token,NewPasswordDTO newPasswordDTO);
}
