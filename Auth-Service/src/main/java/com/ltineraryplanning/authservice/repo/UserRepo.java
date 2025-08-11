package com.ltineraryplanning.authservice.repo;

import com.ltineraryplanning.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepo extends JpaRepository<User,String> {

    User findByUsername(String username);
    User findByEmail(String username);
    User findByUid(String username);
    Boolean existsByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByPhoneNumber(String phone);

    @Query(value = "SELECT email, first_name " +
                    "FROM ltineraryplanning_users " +
                    "WHERE username IN (:usernames)",
            nativeQuery = true
    )
    List<Object[]> findEmailAndFirstNameByUsernames(@Param("usernames") List<String> usernames);

}
