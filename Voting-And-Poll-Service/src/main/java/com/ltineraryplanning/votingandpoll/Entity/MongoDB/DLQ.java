package com.ltineraryplanning.votingandpoll.Entity.MongoDB;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Document("SugPollDLQ")
public class DLQ {
    @Id
    private String id;

    private String tittle;

    private String exception;

    private Object object;

    private String timestamp;
}