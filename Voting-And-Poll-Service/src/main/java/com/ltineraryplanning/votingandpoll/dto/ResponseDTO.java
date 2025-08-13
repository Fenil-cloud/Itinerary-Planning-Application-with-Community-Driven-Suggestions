package com.ltineraryplanning.votingandpoll.dto;

public record ResponseDTO(
        String status,
        String message,
        Object object
) {
}
