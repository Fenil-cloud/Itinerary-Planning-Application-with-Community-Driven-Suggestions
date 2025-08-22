package com.ltineraryplanning.CQRS.entity;


import com.ltineraryplanning.tripservice.enums.TripType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "cqrs_trip")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TripView {
    //    @Id
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
    private List<DestinationView> destinations;
    private List<String> shareWithUsernames;
}
