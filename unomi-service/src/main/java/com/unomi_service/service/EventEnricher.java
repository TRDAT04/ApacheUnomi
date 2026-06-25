package com.unomi_service.service;

import com.unomi_service.dto.NormalizedEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

/**
 * Bước [3] trong CDP Ingestion Pipeline: Enrich NormalizedEvent với metadata hệ thống.
 * <p>
 * Chịu trách nhiệm thêm các trường không đến từ client:
 * - requestId: UUID duy nhất để trace log end-to-end
 * - timestamp: thời điểm event đến hệ thống
 * <p>
 * Tách riêng ra khỏi EventNormalizer để dễ mở rộng sau này
 * (ví dụ: enrich thêm geo-IP, user-agent parsing, A/B test assignment...).
 */
@Component
public class EventEnricher {

    /**
     * Enrich một NormalizedEvent bằng cách thêm requestId và timestamp.
     * Sử dụng toBuilder() để giữ immutability của NormalizedEvent.
     *
     * @param event event sau bước normalize
     * @return event đã được enrich, sẵn sàng cho bước build payload
     */
    public NormalizedEvent enrich(NormalizedEvent event) {
        return event.toBuilder()
                .requestId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .build();
    }
}
