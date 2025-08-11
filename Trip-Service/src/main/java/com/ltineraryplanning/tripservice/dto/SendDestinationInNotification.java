package com.ltineraryplanning.tripservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NotBlank
@Builder
@ToString
public class SendDestinationInNotification {
    private String from;
    private String to;
    private LocalDate startDate;
    private LocalDate endDate;
}
