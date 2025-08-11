package com.ltineraryplanning.notificationservice.record;

import com.ltineraryplanning.notificationservice.dto.EmailAndFirstNameDTO;
import com.ltineraryplanning.notificationservice.dto.SendDestinationInNotification;

import java.time.LocalDate;
import java.util.List;

public record NotificationDTO(
        List<EmailAndFirstNameDTO> emailAndFirstName,
        LocalDate tripStartDate,
        LocalDate tripEndDate,
        List<SendDestinationInNotification> destinations,
        String tripName
) {
}
