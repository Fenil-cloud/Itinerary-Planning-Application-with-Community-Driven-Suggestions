package com.ltineraryplanning.notificationservice.record;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
public class NotificationDTO {
    private List<EmailAndFirstNameDTO> emailAndFirstName;
    private LocalDate tripStartDate;
    private LocalDate tripEndDate;
    private List<SendDestinationInNotification> destinations;
    private String tripName;
}
