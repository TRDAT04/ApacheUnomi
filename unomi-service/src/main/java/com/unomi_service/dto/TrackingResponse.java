package com.unomi_service.dto;

public record TrackingResponse(
        String status,
        String eventType,
        String unomiResponse
) {
    public static TrackingResponse success(String eventType, String unomiResponse) {
        return new TrackingResponse("success", eventType, unomiResponse);
    }
}
