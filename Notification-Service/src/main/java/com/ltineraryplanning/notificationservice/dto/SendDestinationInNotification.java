package com.ltineraryplanning.notificationservice.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SendDestinationInNotification {
    private String from;
    private String to;
    private LocalDate startDate;
    private LocalDate endDate;
}
