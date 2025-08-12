package com.ltineraryplanning.tripservice.dto.elastic;

import com.ltineraryplanning.tripservice.dto.EsSearchItineraryDTO;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HitsArrayDTO {
    private  String _index;
    private String  _type;
    private String _id;
    private Double _score;
    private EsSearchItineraryDTO _source;
}
