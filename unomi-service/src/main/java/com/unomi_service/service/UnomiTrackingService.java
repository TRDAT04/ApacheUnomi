package com.unomi_service.service;

import com.unomi_service.config.UnomiProperties;
import com.unomi_service.dto.ProductTrackingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UnomiTrackingService {

    private final RestClient unomiRestClient;
    private final UnomiProperties unomiProperties;

    public String trackProductView(ProductTrackingRequest request) {
        Map<String, Object> source = new LinkedHashMap<>();
        source.put("itemType", "page");
        source.put("itemId", "product-detail-page");
        source.put("scope", unomiProperties.scope());

        Map<String, Object> target = new LinkedHashMap<>();
        target.put("itemType", "product");
        target.put("itemId", request.getProductId());
        target.put("scope", unomiProperties.scope());

        Map<String, Object> eventProperties = new LinkedHashMap<>();
        eventProperties.put("productId", request.getProductId());

        if (request.getProductName() != null && !request.getProductName().isBlank()) {
            eventProperties.put("productName", request.getProductName());
        }
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            eventProperties.put("category", request.getCategory());
        }
        if (request.getPrice() != null) {
            eventProperties.put("price", request.getPrice());
        }

        Map<String, Object> event = new LinkedHashMap<>();
        event.put("eventType", "view");
        event.put("scope", unomiProperties.scope());
        event.put("source", source);
        event.put("target", target);
        event.put("properties", eventProperties);

        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("sessionId", request.getSessionId());
        requestBody.put("events", List.of(event));

        System.out.println("=== Unomi eventcollector request ===");
        System.out.println(requestBody);

        return unomiRestClient.post()
                .uri("/cxs/eventcollector")
                .body(requestBody)
                .retrieve()
                .body(String.class);
    }

    public String getContext(String sessionId) {
        Map<String, Object> source = new LinkedHashMap<>();
        source.put("itemType", "page");
        source.put("itemId", "product-detail-page");
        source.put("scope", unomiProperties.scope());

        Map<String, Object> contextRequest = new LinkedHashMap<>();
        contextRequest.put("source", source);
        contextRequest.put("requiredProfileProperties", List.of("*"));
        contextRequest.put("requiredSessionProperties", List.of("*"));
        contextRequest.put("requireSegments", true);
        contextRequest.put("requireScores", true);

        return unomiRestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/cxs/context.json")
                        .queryParam("sessionId", sessionId)
                        .build())
                .body(contextRequest)
                .retrieve()
                .body(String.class);
    }
}