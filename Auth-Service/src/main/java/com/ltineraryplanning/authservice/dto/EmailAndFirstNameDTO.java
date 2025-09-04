package com.ltineraryplanning.authservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data

@ToString
public class EmailAndFirstNameDTO {
    private String email;
    private String firstName;
}
