package com.unomi_service.tracking.dto;

import java.time.Instant;

public record TrackResponse(
        String status,
        String eventType,
        String requestId,
        Instant timestamp,
        String unomiResponse
) {
    public static TrackResponse success(String eventType,
                                        String requestId,
                                        Instant timestamp,
                                        String unomiResponse) {
        return new TrackResponse("success", eventType, requestId, timestamp, unomiResponse);
    }
}
