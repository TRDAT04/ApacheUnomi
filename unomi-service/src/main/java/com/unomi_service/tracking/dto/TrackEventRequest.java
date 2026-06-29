package com.unomi_service.tracking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;


@Data
public class TrackEventRequest {

    @NotBlank(message = "sessionId is required")
    private String sessionId;
    private String profileId;

    @NotBlank(message = "eventType is required")
    private String eventType;

    private Map<String, Object> source;
    private Map<String, Object> target;

    @NotNull(message = "properties is required")
    private Map<String, Object> properties;
    private Map<String, Object> context;
}
