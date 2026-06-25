package com.unomi_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Map;


@Getter
@Builder(toBuilder = true)
@ToString
public class NormalizedEvent {
    private final String requestId;
    private final Instant timestamp;
    private final String sessionId;
    private final String profileId;
    private final String scope;
    private final String eventType;
    private final String sourceItemType;
    private final String sourceItemId;
    private final String targetItemType;
    private final String targetItemId;

    private final Map<String, Object> properties;

    private final Map<String, Object> context;
}
