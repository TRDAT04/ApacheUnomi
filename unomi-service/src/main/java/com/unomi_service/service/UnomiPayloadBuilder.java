package com.unomi_service.service;

import com.unomi_service.dto.NormalizedEvent;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class UnomiPayloadBuilder {


    public Map<String, Object> build(NormalizedEvent event) {
        Map<String, Object> body = new LinkedHashMap<>();

        body.put("sessionId", event.getSessionId());

        if (event.getProfileId() != null && !event.getProfileId().isBlank()) {
            body.put("profileId", event.getProfileId());
        }

        body.put("events", List.of(buildEventNode(event)));

        return body;
    }


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
        
        // Fix Unomi 2.x NestedNullException for built-in rules (e.g., sessionPageReferrer)
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("pageInfo", new LinkedHashMap<String, Object>());
        item.put("properties", properties);
        
        return item;
    }
}