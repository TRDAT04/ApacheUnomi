package com.unomi_service.controller;

import com.unomi_service.dto.TrackEventRequest;
import com.unomi_service.dto.TrackResponse;
import com.unomi_service.service.CdpIngestionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * CDP Ingestion Controller – single endpoint design.
 * <p>
 * Thay thế toàn bộ các endpoint cũ (/product-view, /purchase)
 * bằng một endpoint duy nhất theo chuẩn Segment/RudderStack.
 *
 * <h3>Endpoints</h3>
 * <ul>
 *   <li>POST /api/v1/track      – gửi bất kỳ event nào</li>
 *   <li>GET  /api/v1/track/context – lấy profile + segments của session</li>
 * </ul>
 *
 * <h3>Ví dụ – track event view sản phẩm</h3>
 * <pre>{@code
 * POST /api/v1/track
 * {
 *   "sessionId": "sess-abc123",
 *   "eventType": "view",
 *   "properties": {
 *     "productId": "PROD-99",
 *     "productName": "iPhone 15",
 *     "price": 25000000
 *   }
 * }
 * }</pre>
 *
 * <h3>Ví dụ – track event purchase (kích hoạt VIP plugin)</h3>
 * <pre>{@code
 * POST /api/v1/track
 * {
 *   "sessionId": "sess-abc123",
 *   "profileId": "profile-xyz",
 *   "eventType": "purchase",
 *   "properties": {
 *     "orderId": "ORD-001",
 *     "amount": 35000000
 *   }
 * }
 * }</pre>
 *
 * <h3>Ví dụ – event tùy ý (future-proof)</h3>
 * <pre>{@code
 * POST /api/v1/track
 * {
 *   "sessionId": "sess-abc123",
 *   "eventType": "add-to-cart",
 *   "properties": {
 *     "productId": "PROD-5",
 *     "quantity": 2
 *   }
 * }
 * }</pre>
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/track")
public class TrackController {

    private final CdpIngestionService cdpIngestionService;

    /**
     * CDP Track endpoint – nhận mọi loại event.
     * <p>
     * Validate → Normalize → Enrich → Build Unomi payload → Send.
     * Không có logic per-eventType tại tầng controller.
     *
     * @param request unified track event request
     * @return TrackResponse chứa requestId để trace
     */
    @PostMapping
    public ResponseEntity<TrackResponse> track(
            @Valid @RequestBody TrackEventRequest request) {

        TrackResponse response = cdpIngestionService.track(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Lấy context (profile properties + segments + scores) của một session.
     * Dùng để kiểm tra trạng thái profile sau khi track event.
     *
     * @param sessionId session cần kiểm tra
     * @return raw JSON từ Unomi context API
     */
    @GetMapping("/context")
    public ResponseEntity<String> getContext(
            @RequestParam @NotBlank String sessionId) {

        String context = cdpIngestionService.getContext(sessionId);
        return ResponseEntity.ok(context);
    }
}
