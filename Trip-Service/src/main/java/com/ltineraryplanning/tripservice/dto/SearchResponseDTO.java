package com.ltineraryplanning.tripservice.dto;

import com.ltineraryplanning.tripservice.enums.TripType;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SearchResponseDTO {
    private String tripName;
    private String userId;
    private Long numberOfMembers;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPublic;
    private Boolean isPrivate;
    private TripType tripType;
    private List<DestinationDTO> destinations;
}
