package com.ltineraryplanning.authservice.repo;

import com.ltineraryplanning.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User,String> {

    User findByUsername(String username);
    User findByEmail(String username);
    User findByUid(String username);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByPhoneNumber(String phone);


}
