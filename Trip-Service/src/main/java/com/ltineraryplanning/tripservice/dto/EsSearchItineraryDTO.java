package com.ltineraryplanning.tripservice.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class EsSearchItineraryDTO {
    private Set<String> tripNames;
}
