package com.ltineraryplanning.authservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class UserOtp {
    @Id
    @GeneratedValue
    private Long id;

    private String OTP;

    private LocalDateTime sentDate;

    private LocalDateTime expiryDate;

    private LocalDateTime verificationTime;

    @ManyToOne
    @JoinColumn(name="users_id")
    private User user;
}
