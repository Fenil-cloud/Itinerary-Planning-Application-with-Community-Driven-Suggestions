package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto;

import com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.enums.TripType;
import lombok.*;
import org.springframework.data.annotation.Id;

import javax.print.attribute.standard.Destination;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Trip {
    private Long tripId;
    private String userId;
    private Long numberOfMembers;
    private String tripName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean allowComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isPublic;
    private Boolean isPrivate;
    private TripType tripType;
    private List<Destination> destinations = new ArrayList<>();
    private List<String> shareWithUsernames = new ArrayList<>();
}