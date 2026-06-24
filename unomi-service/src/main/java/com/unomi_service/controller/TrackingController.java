package com.unomi_service.controller;

import com.unomi_service.dto.ProductTrackingRequest;
import com.unomi_service.dto.PurchaseTrackingRequest;
import com.unomi_service.dto.TrackingResponse;
import com.unomi_service.service.UnomiTrackingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/tracking")
public class TrackingController {

    private final UnomiTrackingService trackingService;

    /**
     * Track sự kiện xem sản phẩm.
     * Unomi sẽ ghi nhận event type = "view".
     */
    @PostMapping("/product-view")
    public ResponseEntity<TrackingResponse> trackProductView(
            @Valid @RequestBody ProductTrackingRequest request) {
        String result = trackingService.trackProductView(request);
        return ResponseEntity.ok(TrackingResponse.success("productView", result));
    }

    /**
     * Track sự kiện mua hàng.
     * Unomi sẽ ghi nhận event type = "purchase" → kích hoạt VIP plugin.
     */
    @PostMapping("/purchase")
    public ResponseEntity<TrackingResponse> trackPurchase(
            @Valid @RequestBody PurchaseTrackingRequest request) {
        String result = trackingService.trackPurchase(request);
        return ResponseEntity.ok(TrackingResponse.success("purchase", result));
    }

    /**
     * Lấy context hiện tại của một session: profile properties + segments + scores.
     * Dùng để kiểm tra xem profile có được đánh dấu VIP chưa.
     */
    @GetMapping("/context")
    public ResponseEntity<String> getContext(
            @RequestParam @NotBlank String sessionId) {
        String result = trackingService.getContext(sessionId);
        return ResponseEntity.ok(result);
    }
}