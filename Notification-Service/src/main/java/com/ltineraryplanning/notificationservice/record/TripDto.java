package com.ltineraryplanning.notificationservice.record;

import java.time.LocalDate;

public record TripDto(
        String email,
        String tripName,
        String fname,
        LocalDate startDate,
        LocalDate endDate,
        String destination
) {
}
