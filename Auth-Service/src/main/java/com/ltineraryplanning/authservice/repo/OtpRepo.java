package com.ltineraryplanning.authservice.repo;

import com.ltineraryplanning.authservice.model.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpRepo extends JpaRepository<UserOtp,Long> {
}
