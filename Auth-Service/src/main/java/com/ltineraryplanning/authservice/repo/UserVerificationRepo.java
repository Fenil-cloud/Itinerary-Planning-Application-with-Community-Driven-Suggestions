package com.ltineraryplanning.authservice.repo;

import com.ltineraryplanning.authservice.dto.UserRepresentation;
import com.ltineraryplanning.authservice.model.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserVerificationRepo extends JpaRepository<UserVerification,String> {
}
