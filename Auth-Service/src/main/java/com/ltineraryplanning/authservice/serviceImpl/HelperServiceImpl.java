package com.ltineraryplanning.authservice.serviceImpl;

import com.ltineraryplanning.authservice.dto.ResponseDTO;
import com.ltineraryplanning.authservice.kafka.AuthLinkProducer;
import com.ltineraryplanning.authservice.kafka.ResetLink;
import com.ltineraryplanning.authservice.model.User;
import com.ltineraryplanning.authservice.model.UserVerification;
import com.ltineraryplanning.authservice.repo.UserRepo;
import com.ltineraryplanning.authservice.repo.UserVerificationRepo;
import com.ltineraryplanning.authservice.service.HelperService;
import lombok.experimental.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class HelperServiceImpl implements HelperService {
    @Autowired
    private UserRepo repo;

    @Autowired
    private UserVerificationRepo  userVerificationRepo;

    @Autowired
    private ExtractTokenService tokenService;

    @Autowired
    private AuthLinkProducer authLinkProducer;


    @Override
    public String getUserName(String auth) throws ParseException {
        Map<String,Object> map = tokenService.extractValue(auth);
        String uid = map.get("uid").toString();
        return repo.findByUid(uid).getUsername();

    }

    @Override
    public ResponseDTO sendResetLink(String username) {
        String token = UUID.randomUUID().toString();
        User user =  repo.findByUsername(username);
        if(user == null){
            return new ResponseDTO("200","User not found",null);
        }
        Optional<UserVerification> userVerification = userVerificationRepo.findById(user.getUid());
        if(userVerification.isEmpty()){
            return new ResponseDTO("200","Something went wrong",null);
        }
        userVerification.get().setType("reset");
        userVerification.get().setExpiryDate(LocalDateTime.now().plusHours(24));
        userVerification.get().setSendAt(LocalDateTime.now());
        userVerification.get().setToken(token);
        userVerificationRepo.save(userVerification.get());
        userVerification.get().setIsCheck(false);

        String url = "http://localhost:8222/api/v1/auth/reset/"+user.getUid()+"/"+token;
        ResetLink resetLink = new ResetLink();
        resetLink.setUrl(url);
        resetLink.setEmail(userVerification.get().getEmail());
        authLinkProducer.sendResetLink(resetLink);
        return new ResponseDTO("200","Password reset link send",null);
    }
}
