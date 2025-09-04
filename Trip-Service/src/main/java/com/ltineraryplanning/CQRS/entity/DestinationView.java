package com.ltineraryplanning.CQRS.entity;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DestinationView {
    private Long destinationId;
    private Long tripId;
    private String from;
    private String to;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDelete;
}
