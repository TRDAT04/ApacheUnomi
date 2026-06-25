package com.unomi_service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.Map;

/**
 * Internal model đại diện cho một event đã được normalize và enrich hoàn toàn.
 * <p>
 * Đây là "canonical event format" lưu thông trong CDP ingestion pipeline:
 * <pre>
 *   TrackEventRequest → [EventNormalizer] → NormalizedEvent → [EventEnricher] → NormalizedEvent
 *                    → [UnomiPayloadBuilder] → Map(Unomi JSON) → [UnomiClient] → Unomi
 * </pre>
 *
 * Class này KHÔNG expose ra ngoài (không dùng trong response/request).
 * Thiết kế immutable (@Builder) để an toàn khi pass qua pipeline.
 */
@Getter
@Builder(toBuilder = true)
@ToString
public class NormalizedEvent {

    // ── Traceability ──────────────────────────────────────────────────────────
    /** UUID tự sinh, dùng để trace request xuyên suốt log */
    private final String requestId;

    /** Thời điểm event được nhận vào hệ thống */
    private final Instant timestamp;

    // ── Identity ──────────────────────────────────────────────────────────────
    private final String sessionId;

    /** Null nếu user chưa đăng nhập (anonymous session) */
    private final String profileId;

    /** Unomi scope – lấy từ config */
    private final String scope;

    // ── Event ─────────────────────────────────────────────────────────────────
    /** Loại sự kiện gốc từ client: "view", "purchase", "add-to-cart", ... */
    private final String eventType;

    // ── Source (nơi event xảy ra) ─────────────────────────────────────────────
    private final String sourceItemType;
    private final String sourceItemId;

    // ── Target (đối tượng bị tác động) ───────────────────────────────────────
    private final String targetItemType;
    private final String targetItemId;

    // ── Payload ───────────────────────────────────────────────────────────────
    /** Toàn bộ properties từ request, forward nguyên vẹn sang Unomi */
    private final Map<String, Object> properties;

    /** Client metadata (userAgent, ip, locale...) */
    private final Map<String, Object> context;
}
