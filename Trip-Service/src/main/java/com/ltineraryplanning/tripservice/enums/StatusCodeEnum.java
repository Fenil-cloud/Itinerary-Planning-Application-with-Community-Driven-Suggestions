package com.ltineraryplanning.tripservice.enums;

import lombok.Getter;

@Getter
public enum StatusCodeEnum {
    OK("200"),
    CREATED("201"),
    NOT_FOUND("404"),
    UNAUTHORIZED("401"),
    BAD_REQUEST("400"),
    EXCEPTION("500"),
    ERROR("501");

    StatusCodeEnum(String statusCode) {
        this.statusCode = statusCode;
    }

    private final String statusCode;
}
