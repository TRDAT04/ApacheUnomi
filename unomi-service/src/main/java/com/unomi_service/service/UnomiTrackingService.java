package com.unomi_service.service;

import com.unomi_service.config.UnomiProperties;
import com.unomi_service.dto.ProductTrackingRequest;
import com.unomi_service.dto.PurchaseTrackingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UnomiTrackingService {

    private final RestClient unomiRestClient;
    private final UnomiProperties unomiProperties;

    // -------------------------------------------------------------------------
    // Track product view (event: view)
    // -------------------------------------------------------------------------
    public String trackProductView(ProductTrackingRequest request) {

        Map<String, Object> source = buildPageSource("product-detail-page");

        Map<String, Object> target = new LinkedHashMap<>();
        target.put("itemType", "product");
        target.put("itemId", request.getProductId());
        target.put("scope", unomiProperties.scope());

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("productId", request.getProductId());

        if (request.getProductName() != null) {
            properties.put("productName", request.getProductName());
        }
        if (request.getCategory() != null) {
            properties.put("category", request.getCategory());
        }
        if (request.getPrice() != null) {
            properties.put("price", request.getPrice());
        }

        Map<String, Object> event = buildEvent("view", source, target, properties);

        return postEventCollector(event, request.getSessionId(), null);
    }

    // -------------------------------------------------------------------------
    // Track purchase (event: purchase) – triggers VIP plugin
    // -------------------------------------------------------------------------
    public String trackPurchase(PurchaseTrackingRequest request) {

        Map<String, Object> source = buildPageSource("checkout-page");

        Map<String, Object> target = new LinkedHashMap<>();
        target.put("itemType", "order");
        target.put("itemId", request.getOrderId());
        target.put("scope", unomiProperties.scope());

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("orderId", request.getOrderId());
        properties.put("amount", request.getAmount());

        Map<String, Object> event = buildEvent("purchase", source, target, properties);

        return postEventCollector(event, request.getSessionId(), request.getProfileId());
    }

    // -------------------------------------------------------------------------
    // Get context (profile + segments) for a session
    // -------------------------------------------------------------------------
    public String getContext(String sessionId) {
        Map<String, Object> source = buildPageSource("context-query");

        Map<String, Object> contextRequest = new LinkedHashMap<>();
        contextRequest.put("source", source);
        contextRequest.put("requiredProfileProperties", List.of("*"));
        contextRequest.put("requiredSessionProperties", List.of("*"));
        contextRequest.put("requireSegments", true);
        contextRequest.put("requireScores", true);

        log.debug("Fetching context from Unomi – sessionId={}", sessionId);
        return unomiRestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/cxs/context.json")
                        .queryParam("sessionId", sessionId)
                        .build())
                .body(contextRequest)
                .retrieve()
                .body(String.class);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    private Map<String, Object> buildPageSource(String itemId) {
        Map<String, Object> source = new LinkedHashMap<>();
        source.put("itemType", "page");
        source.put("itemId", itemId);
        source.put("scope", unomiProperties.scope());
        return source;
    }

    private Map<String, Object> buildEvent(String eventType,
                                           Map<String, Object> source,
                                           Map<String, Object> target,
                                           Map<String, Object> properties) {
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventType", eventType);
        event.put("scope", unomiProperties.scope());
        event.put("source", source);
        event.put("target", target);
        event.put("properties", properties);
        return event;
    }

    private String postEventCollector(Map<String, Object> event,
                                      String sessionId,
                                      String profileId) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("sessionId", sessionId);

        if (profileId != null && !profileId.isBlank()) {
            body.put("profileId", profileId);
        }

        // ⚠️ CHỈ 1 EVENT DUY NHẤT
        body.put("event", event);

        return unomiRestClient.post()
                .uri("/cxs/eventcollector")
                .body(body)
                .retrieve()
                .body(String.class);
    }
}