package com.ltineraryplanning.authservice.service;

import java.text.ParseException;

public interface HelperService {
    String getUserName(String auth) throws ParseException;
}
