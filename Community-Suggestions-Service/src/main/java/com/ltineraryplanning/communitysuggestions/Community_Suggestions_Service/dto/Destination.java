package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
public class Destination {

    private Long destinationId;
    private String from;
    private String to;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
