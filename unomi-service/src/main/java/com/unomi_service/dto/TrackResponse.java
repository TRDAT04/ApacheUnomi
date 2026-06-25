package com.unomi_service.dto;

import java.time.Instant;

/**
 * Response trả về sau khi track event thành công.
 * Bao gồm requestId để client có thể trace log.
 */
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
