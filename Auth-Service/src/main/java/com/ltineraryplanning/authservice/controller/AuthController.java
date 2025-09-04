package com.ltineraryplanning.authservice.controller;

import com.ltineraryplanning.authservice.dto.*;
import com.ltineraryplanning.authservice.service.AuthService;
import com.ltineraryplanning.authservice.service.HelperService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

    @Autowired
    AuthService authService;

    @Autowired
    HelperService helperService;

    @PostMapping("register")
    @CircuitBreaker(name = "authservice", fallbackMethod = "registerFallBack")
    @RateLimiter(name = "authRateLimiter", fallbackMethod = "registerLimitFallBack")
    public ResponseDTO register(@Valid @RequestBody RegisterRequest registerRequest, Errors errors) {
        //log.info("Request received");
        if (errors.hasErrors()) {
            return new ResponseDTO("200", errors.getAllErrors().get(0).getDefaultMessage(), null);
        }
        return authService.registerUser(registerRequest);
    }

    public ResponseDTO registerFallBack(@Valid @RequestBody RegisterRequest registerRequest, Errors errors,Throwable ex) {
        return new ResponseDTO("503", "Service not available", null);
    }

    public ResponseDTO registerLimitFallBack(@Valid @RequestBody RegisterRequest registerRequest, Errors errors,Throwable ex) {
        return new ResponseDTO("503", "Service not available", null);
    }



    @PostMapping("login")
    @CircuitBreaker(name = "authservice",fallbackMethod = "loginFallBack")
    public ResponseDTO login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    public ResponseDTO loginFallBack(LoginRequest loginRequest, Throwable ex) {
        return new ResponseDTO("503","Service Unavailable",null);
    }

        @GetMapping(value = "verify/{id}/{token}",produces = "application/json")
        public ResponseDTO verify(@PathVariable String id, @PathVariable String token) {
            return authService.verify(id,token);
    //        return ResponseEntity.ok(authService.verify(id, token));
        }
    @PostMapping("verify/mobile/{otp}")
    public ResponseDTO verifyMobile(@RequestHeader("Authorization") String auth,@PathVariable String otp) throws ParseException {
        return authService.verifyMobile(auth,otp);
    }

    @GetMapping("username")
    public String getUserName(@RequestHeader("Authorization") String auth) throws ParseException {
        return helperService.getUserName(auth);
    }

    @PostMapping("reset/{username}")
    public ResponseDTO resetPasswordLink(@PathVariable String username) throws ParseException {
        return helperService.sendResetLink(username);
    }

    @PostMapping("reset/{uid}/{token}")
    public ResponseDTO resetPassword(@PathVariable String uid, @PathVariable String token, @RequestBody NewPasswordDTO newPasswordDTO) throws ParseException {
        return authService.resetPassword(uid,token,newPasswordDTO);
    }

    @PostMapping("getEmailAndFirstName")
    public List<EmailAndFirstNameDTO> getEmailAndFirstName(@RequestBody List<String> usernames) throws ParseException {
        return authService.getEmailAndFirstName(usernames);
    }
}