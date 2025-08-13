package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCommentDTO {

    @NotNull(message = "Comment text required!")
    @NotBlank(message = "Can't post blank comment!")
    private String text;
}
