package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class AddSuggestionDTO {
//    private String tripID;
@NotBlank(message = "Destination Name is required")
private String destinationName;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Tags list cannot be null")
    @Size(min = 1, message = "At least one tag is required")
    private List<@NotBlank(message = "Tag cannot be blank") String> tag;
}
