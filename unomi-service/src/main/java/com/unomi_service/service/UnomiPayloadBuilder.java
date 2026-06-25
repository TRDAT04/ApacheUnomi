package com.unomi_service.service;

import com.unomi_service.dto.NormalizedEvent;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Bước [4] trong CDP Ingestion Pipeline: Build payload cho Apache Unomi /cxs/eventcollector.
 *
 * Format đúng mà Unomi đang nhận:
 *
 * {
 *   "sessionId": "...",
 *   "profileId": "...",   // optional
 *   "events": [
 *     {
 *       "eventType": "purchase",
 *       "scope": "myweb",
 *       "source": {
 *         "itemType": "page",
 *         "itemId": "checkout-page",
 *         "scope": "myweb"
 *       },
 *       "target": {
 *         "itemType": "order",
 *         "itemId": "ORD-001",
 *         "scope": "myweb"
 *       },
 *       "properties": {
 *         "orderId": "ORD-001",
 *         "amount": 35000000
 *       }
 *     }
 *   ]
 * }
 */
@Component
public class UnomiPayloadBuilder {

    /**
     * Build body hoàn chỉnh để POST lên /cxs/eventcollector.
     */
    public Map<String, Object> build(NormalizedEvent event) {
        Map<String, Object> body = new LinkedHashMap<>();

        body.put("sessionId", event.getSessionId());

        if (event.getProfileId() != null && !event.getProfileId().isBlank()) {
            body.put("profileId", event.getProfileId());
        }

        // QUAN TRỌNG: Unomi của bạn đang nhận "events" là LIST
        body.put("events", List.of(buildEventNode(event)));

        return body;
    }

    // ---------------------------------------------------------------------
    // Private builders
    // ---------------------------------------------------------------------

    private Map<String, Object> buildEventNode(NormalizedEvent event) {
        Map<String, Object> eventNode = new LinkedHashMap<>();
        eventNode.put("eventType", event.getEventType());
        eventNode.put("scope", event.getScope());
        eventNode.put("source", buildItemNode(
                event.getSourceItemType(),
                event.getSourceItemId(),
                event.getScope()
        ));
        eventNode.put("target", buildItemNode(
                event.getTargetItemType(),
                event.getTargetItemId(),
                event.getScope()
        ));
        eventNode.put("properties", event.getProperties());
        return eventNode;
    }

    private Map<String, Object> buildItemNode(String itemType, String itemId, String scope) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("itemType", itemType);
        item.put("itemId", itemId);
        item.put("scope", scope);
        return item;
    }
}