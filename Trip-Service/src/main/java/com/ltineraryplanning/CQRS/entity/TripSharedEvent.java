package com.ltineraryplanning.CQRS.entity;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TripSharedEvent {
    private Long tripId;
    private List<String> shareWithUsernames;
}