package com.ltineraryplanning.authservice.dto;

public record ResponseDTO(
        String status,
        String message,
        Object object
) {
}
