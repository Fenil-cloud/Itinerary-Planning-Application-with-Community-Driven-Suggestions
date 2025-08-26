package com.ltineraryplanning.tripservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DestinationDTO {
    @NotBlank(message = "Please specify where to trip start for particular start date")
    private String from;
    @NotBlank(message = "Please specify where to trip end for particular end date")
    private String to;
    @Future(message = "Trip start date must be future date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate startDate;
    @Future(message = "Trip end date must be future date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate endDate;
}
