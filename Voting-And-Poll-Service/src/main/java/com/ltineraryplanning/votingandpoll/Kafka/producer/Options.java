package com.ltineraryplanning.votingandpoll.Kafka.producer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Options {
    private String optionId;
    private String text;
    private int votes;
    private String url;
}