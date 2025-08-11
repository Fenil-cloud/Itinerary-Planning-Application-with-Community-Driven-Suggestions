package com.ltineraryplanning.tripservice.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ResponseDTO {
    String status;
    String message;
    Object data;
}
