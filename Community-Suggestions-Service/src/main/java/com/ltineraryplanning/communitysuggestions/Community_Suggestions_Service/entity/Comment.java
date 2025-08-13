package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private String commentId;
    private String userId;
    private String text;
    private String createdAt;
}