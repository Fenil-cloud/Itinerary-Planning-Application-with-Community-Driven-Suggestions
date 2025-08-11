package com.ltineraryplanning.notificationservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EmailAndFirstNameDTO {
    private String email;
    private String firstName;
}
