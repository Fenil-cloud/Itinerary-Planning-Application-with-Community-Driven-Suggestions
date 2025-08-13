package com.ltineraryplanning.communitysuggestions.Community_Suggestions_Service.entity;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("voteDLQ")
public class DLQ {
    @Id
    private String id;

    private String tittle;

    private String exception;

    private Object object;

    private String timestamp;
}