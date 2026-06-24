package com.unomi_service.controller;

import com.unomi_service.dto.ProductTrackingRequest;
import com.unomi_service.dto.TrackingResponse;
import com.unomi_service.service.UnomiTrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tracking")
public class TrackingController {

    private final UnomiTrackingService trackingService;

    @PostMapping("/product-view")
    public ResponseEntity<TrackingResponse> trackProductView(@Valid @RequestBody ProductTrackingRequest request) {
        String result = trackingService.trackProductView(request);
        return ResponseEntity.ok(TrackingResponse.success("productView", result));
    }
}