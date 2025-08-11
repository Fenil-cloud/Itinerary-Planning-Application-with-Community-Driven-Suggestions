package com.ltineraryplanning.tripservice.dto;

import com.ltineraryplanning.tripservice.entity.Destination;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificationDTO {
    private List<EmailAndFirstNameDTO> emailAndFirstName;
    private LocalDate tripStartDate;
    private LocalDate tripEndDate;
    private List<SendDestinationInNotification> destinations;
    private String tripName;
}
