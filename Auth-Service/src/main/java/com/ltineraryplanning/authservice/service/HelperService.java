package com.ltineraryplanning.authservice.service;

import com.ltineraryplanning.authservice.dto.ResponseDTO;
import org.bouncycastle.asn1.ocsp.ResponderID;

import java.text.ParseException;

public interface HelperService {
    String getUserName(String auth) throws ParseException;

    ResponseDTO sendResetLink(String username);
}
