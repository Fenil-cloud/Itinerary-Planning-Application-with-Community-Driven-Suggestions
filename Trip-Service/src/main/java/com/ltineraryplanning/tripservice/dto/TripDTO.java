package com.ltineraryplanning.tripservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ltineraryplanning.tripservice.enums.TripType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TripDTO {
    @NotNull(message = "Number of members is required")
    private Long numberOfMembers;
    @NotBlank(message = "Trip Name must not be blank")
    private String tripName;
    @Future(message = "Trip start date must be future date ")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate startDate;
    @Future(message = "Trip end date must be future date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate endDate;
    @NotBlank(message = "Must specify trip public or not")
    private Boolean isPublic;
    @NotBlank(message = "Must specify trip private or not")
    private Boolean isPrivate;
    @Enumerated(STRING)
    @NotNull(message = "Trip type must be specified")
    private TripType tripType;
    @NotNull(message = "Destinations list cannot be null")
    @Size(min = 1, message = "At least one destination must be provided")
    private List<DestinationDTO> destinations;
    private List<String> shareWithUsernames;
}
