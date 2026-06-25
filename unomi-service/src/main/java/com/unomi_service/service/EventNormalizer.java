package com.unomi_service.service;

import com.unomi_service.config.UnomiProperties;
import com.unomi_service.dto.NormalizedEvent;
import com.unomi_service.dto.TrackEventRequest;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Bước [2] trong CDP Ingestion Pipeline: Normalize raw request → NormalizedEvent.
 * <p>
 * Chịu trách nhiệm:
 * - Detect itemType cho target dựa theo eventType (Map lookup, không có if/else)
 * - Extract itemId từ properties theo thứ tự ưu tiên
 * - Detect source page dựa theo eventType
 * - Merge source/target override từ client nếu có
 */
@Component
public class EventNormalizer {

    /**
     * Map từ eventType → targetItemType.
     * Không có if/else – chỉ cần thêm entry vào Map để hỗ trợ event mới.
     */
    private static final Map<String, String> EVENT_TO_TARGET_ITEM_TYPE = Map.of(
            "view",             "product",
            "purchase",         "order",
            "add-to-cart",      "product",
            "remove-from-cart", "product",
            "wishlist",         "product",
            "search",           "query",
            "click",            "element",
            "page",             "page"
    );

    /**
     * Map từ eventType → key ưu tiên trong properties để lấy itemId.
     * Ví dụ: event "purchase" → dùng properties["orderId"] làm itemId.
     */
    private static final Map<String, String> EVENT_TO_PREFERRED_ID_KEY = Map.of(
            "view",             "productId",
            "purchase",         "orderId",
            "add-to-cart",      "productId",
            "remove-from-cart", "productId",
            "wishlist",         "productId",
            "search",           "query",
            "click",            "elementId"
    );

    /**
     * Map từ eventType → source page name (sourceItemId cho Unomi).
     */
    private static final Map<String, String> EVENT_TO_SOURCE_PAGE = Map.of(
            "view",             "product-detail-page",
            "purchase",         "checkout-page",
            "add-to-cart",      "product-detail-page",
            "remove-from-cart", "product-detail-page",
            "wishlist",         "product-detail-page",
            "search",           "search-results-page",
            "click",            "web-page"
    );

    /**
     * Normalize một TrackEventRequest thành NormalizedEvent chứa đầy đủ thông tin
     * cho các bước tiếp theo trong pipeline (enrich → build → send).
     *
     * @param request  request thô từ client
     * @param props    config của Unomi (scope, sourceItemId...)
     * @return NormalizedEvent đã được normalize (chưa enrich)
     */
    public NormalizedEvent normalize(TrackEventRequest request, UnomiProperties props) {

        String eventType = request.getEventType().toLowerCase();

        // ── Detect / override source ──────────────────────────────────────────
        String sourceItemType = extractOverrideOrDefault(request.getSource(), "itemType", "page");
        String sourceItemId   = extractOverrideOrDefault(
                request.getSource(), "itemId",
                EVENT_TO_SOURCE_PAGE.getOrDefault(eventType, props.sourceItemId())
        );

        // ── Detect / override target ──────────────────────────────────────────
        String targetItemType = extractOverrideOrDefault(
                request.getTarget(), "itemType",
                detectItemType(eventType)
        );
        String targetItemId = extractOverrideOrDefault(
                request.getTarget(), "itemId",
                extractItemId(request.getProperties(), eventType)
        );

        return NormalizedEvent.builder()
                .sessionId(request.getSessionId())
                .profileId(request.getProfileId())
                .eventType(eventType)
                .scope(props.scope())
                .sourceItemType(sourceItemType)
                .sourceItemId(sourceItemId)
                .targetItemType(targetItemType)
                .targetItemId(targetItemId)
                .properties(request.getProperties())
                .context(request.getContext())
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Package-private helpers – testable riêng
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Detect targetItemType từ eventType.
     * Mặc định trả về "item" nếu không có mapping.
     */
    String detectItemType(String eventType) {
        return EVENT_TO_TARGET_ITEM_TYPE.getOrDefault(eventType, "item");
    }

    /**
     * Extract itemId từ properties theo thứ tự ưu tiên:
     * 1. Preferred key theo eventType (orderId, productId...)
     * 2. Các key chung: "id", "itemId", "entityId"
     * 3. Fallback: "unknown"
     */
    String extractItemId(Map<String, Object> properties, String eventType) {
        // 1. Preferred key theo eventType
        String preferredKey = EVENT_TO_PREFERRED_ID_KEY.get(eventType);
        if (preferredKey != null && properties.containsKey(preferredKey)) {
            return String.valueOf(properties.get(preferredKey));
        }

        // 2. Fallback theo các key chung
        return Stream.of("id", "itemId", "entityId")
                .filter(properties::containsKey)
                .map(k -> String.valueOf(properties.get(k)))
                .findFirst()
                .orElse("unknown");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Private utilities
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Nếu overrideMap có key, trả về giá trị đó. Ngược lại trả về defaultValue.
     */
    private String extractOverrideOrDefault(Map<String, Object> overrideMap,
                                            String key,
                                            String defaultValue) {
        if (overrideMap != null && overrideMap.containsKey(key)) {
            return String.valueOf(overrideMap.get(key));
        }
        return defaultValue;
    }
}
