package com.ltineraryplanning.authservice.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ltineraryplanning.authservice.enums.Gender;
import com.ltineraryplanning.authservice.enums.Role;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "ltineraryplanning_users")
public class User {

    @Id
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    private String uid;

    private String roles;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    private String gender;

    private boolean isActive;

    private boolean isVerifiedMobile;
    private boolean isVerifiedEmail;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserOtp> otp;
}