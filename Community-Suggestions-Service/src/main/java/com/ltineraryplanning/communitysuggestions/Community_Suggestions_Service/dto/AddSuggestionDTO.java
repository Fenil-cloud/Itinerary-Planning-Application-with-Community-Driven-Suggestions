package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddSuggestionDTO {
//    private String tripID;
    private String destinationName;
    private String title;
    private String description;
    private List<String> tag;
}
