package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResponseDTO(String status, String message, @JsonProperty("data") Object object){

}