package com.ltineraryplanning.authservice.controller;

import com.ltineraryplanning.authservice.dto.EmailAndFirstNameDTO;
import com.ltineraryplanning.authservice.dto.LoginRequest;
import com.ltineraryplanning.authservice.dto.RegisterRequest;
import com.ltineraryplanning.authservice.dto.ResponseDTO;
import com.ltineraryplanning.authservice.service.AuthService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("register")
    public ResponseDTO register(@Valid @RequestBody RegisterRequest registerRequest, Errors errors) {
        //log.info("Request received");
        if (errors.hasErrors()) {
            return new ResponseDTO("200", errors.getAllErrors().get(0).getDefaultMessage(), null);
        }
        return authService.registerUser(registerRequest);
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

    @PostMapping("getEmailAndFirstName")
    public List<EmailAndFirstNameDTO> getEmailAndFirstName(@RequestBody List<String> usernames){
        return authService.getEmailAndFirstName(usernames);
    }
}