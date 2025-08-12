package com.ltineraryplanning.tripservice.dto.elastic;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HitsDTO {
    private  Long max_score;
    private TotalDTO  total;
    private List<HitsArrayDTO> hits;
}
