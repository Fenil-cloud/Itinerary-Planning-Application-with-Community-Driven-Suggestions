package com.ltineraryplanning.tripservice.dto.elastic;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TotalDTO {
    private Long value;
    private  String relation;
}
