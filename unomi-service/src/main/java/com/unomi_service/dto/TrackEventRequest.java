package com.unomi_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * DTO đầu vào thống nhất cho CDP Ingestion API.
 * <p>
 * Mọi loại event (view, purchase, add-to-cart, custom...) đều dùng DTO này,
 * tương tự chuẩn Segment/RudderStack Track call.
 * <p>
 * Ví dụ event view sản phẩm:
 * <pre>{@code
 * {
 *   "sessionId": "sess-abc123",
 *   "eventType": "view",
 *   "properties": { "productId": "PROD-99", "productName": "iPhone 15", "price": 25000000 }
 * }
 * }</pre>
 *
 * Ví dụ event purchase:
 * <pre>{@code
 * {
 *   "sessionId": "sess-abc123",
 *   "profileId": "profile-xyz",
 *   "eventType": "purchase",
 *   "properties": { "orderId": "ORD-001", "amount": 35000000 }
 * }
 * }</pre>
 */
@Data
public class TrackEventRequest {

    /**
     * Session ID – bắt buộc.
     * Dùng để liên kết event với session trong Apache Unomi.
     */
    @NotBlank(message = "sessionId is required")
    private String sessionId;

    /**
     * Profile ID – tuỳ chọn.
     * Truyền khi user đã đăng nhập để Unomi merge anonymous profile → known profile.
     */
    private String profileId;

    /**
     * Loại sự kiện – bắt buộc.
     * Có thể là bất kỳ string nào: "view", "purchase", "add-to-cart", "search", v.v.
     * Unomi sẽ nhận đúng giá trị này làm eventType.
     */
    @NotBlank(message = "eventType is required")
    private String eventType;

    /**
     * Nguồn phát sinh event – tuỳ chọn.
     * Nếu không truyền, hệ thống tự detect dựa theo eventType.
     * Format: { "itemType": "page", "itemId": "checkout-page" }
     */
    private Map<String, Object> source;

    /**
     * Đối tượng mà event tác động lên – tuỳ chọn.
     * Nếu không truyền, hệ thống tự detect từ eventType + properties.
     * Format: { "itemType": "order", "itemId": "ORD-001" }
     */
    private Map<String, Object> target;

    /**
     * Payload chính của event – bắt buộc.
     * Chứa dữ liệu tuỳ thuộc vào eventType:
     * - view: { productId, productName, category, price }
     * - purchase: { orderId, amount }
     * - add-to-cart: { productId, quantity }
     * - ... bất kỳ key-value nào khác
     */
    @NotNull(message = "properties is required")
    private Map<String, Object> properties;

    /**
     * Metadata từ client – tuỳ chọn.
     * Ví dụ: { "userAgent": "Mozilla/...", "ip": "1.2.3.4", "locale": "vi-VN" }
     */
    private Map<String, Object> context;
}
