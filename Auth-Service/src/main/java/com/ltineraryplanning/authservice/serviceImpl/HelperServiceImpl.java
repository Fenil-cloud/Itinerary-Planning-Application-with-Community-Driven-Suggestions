package com.ltineraryplanning.authservice.serviceImpl;

import com.ltineraryplanning.authservice.repo.UserRepo;
import com.ltineraryplanning.authservice.service.HelperService;
import lombok.experimental.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@Service
public class HelperServiceImpl implements HelperService {
    @Autowired
    private UserRepo repo;

    @Autowired
    private ExtractTokenService tokenService;


    @Override
    public String getUserName(String auth) throws ParseException {
        Map<String,Object> map = tokenService.extractValue(auth);
        String uid = map.get("uid").toString();
        return repo.findByUid(uid).getUsername();

    }
}
