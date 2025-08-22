package com.ltineraryplanning.notificationservice.enums;

import lombok.Getter;

public enum EmailTemplates {
    UPCOMING_TRIP_NOTIFICATION("upcoming-trip-notification.html","Ready For Your Next Trip"),
    AUTH_LINK("auth-template.html","Email Verification"),
    RESET_LINK("reset-template.html","Password Reset");
    @Getter
    private final String template;

    @Getter
    private final String subject;

    EmailTemplates(String template,String subject){
        this.template = template;
        this.subject = subject;
    }
}

