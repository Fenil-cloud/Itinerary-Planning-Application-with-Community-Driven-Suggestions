package com.ltineraryplanning.authservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class UserVerification {
    @Id
    private String userId;
    private String email;
    private String mobile;
    private String token;
    private String type;
    private Boolean isCheck;
    private boolean isVerified;
    private boolean isVerifiedMobile;
    private Integer otp;
    private LocalDateTime sendAt;
    private LocalDateTime expiryDate;
}
