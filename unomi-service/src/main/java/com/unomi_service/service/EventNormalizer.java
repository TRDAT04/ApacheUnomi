package com.unomi_service.service;

import com.unomi_service.config.UnomiProperties;
import com.unomi_service.dto.NormalizedEvent;
import com.unomi_service.dto.TrackEventRequest;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Stream;


@Component
public class EventNormalizer {

    private static final Map<String, String> EVENT_TO_TARGET_ITEM_TYPE = Map.of(
            "product_view",     "product",
            "purchase",         "order",
            "add_to_cart",      "product",
            "remove_from_cart", "product",
            "wishlist",         "product",
            "search",           "query",
            "click",            "element",
            "page",             "page",
            "login",            "profile"
    );


    private static final Map<String, String> EVENT_TO_PREFERRED_ID_KEY = Map.of(
            "product_view",     "productId",
            "purchase",         "orderId",
            "add_to_cart",      "productId",
            "remove_from_cart", "productId",
            "wishlist",         "productId",
            "search",           "query",
            "click",            "elementId",
            "login",            "userId"
    );

    private static final Map<String, String> EVENT_TO_SOURCE_PAGE = Map.of(
            "product_view",     "product-detail-page",
            "purchase",         "checkout-page",
            "add_to_cart",      "product-detail-page",
            "remove_from_cart", "product-detail-page",
            "wishlist",         "product-detail-page",
            "search",           "search-results-page",
            "click",            "web-page",
            "login",            "login-page"
    );


    public NormalizedEvent normalize(TrackEventRequest request, UnomiProperties props) {

        String eventType = request.getEventType().toLowerCase();

        String sourceItemType = extractOverrideOrDefault(request.getSource(), "itemType", "page");
        String sourceItemId   = extractOverrideOrDefault(
                request.getSource(), "itemId",
                EVENT_TO_SOURCE_PAGE.getOrDefault(eventType, props.sourceItemId())
        );

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


    String detectItemType(String eventType) {
        return EVENT_TO_TARGET_ITEM_TYPE.getOrDefault(eventType, "item");
    }


    String extractItemId(Map<String, Object> properties, String eventType) {
        String preferredKey = EVENT_TO_PREFERRED_ID_KEY.get(eventType);
        if (preferredKey != null && properties.containsKey(preferredKey)) {
            return String.valueOf(properties.get(preferredKey));
        }

        return Stream.of("id", "itemId", "entityId")
                .filter(properties::containsKey)
                .map(k -> String.valueOf(properties.get(k)))
                .findFirst()
                .orElse("unknown");
    }


    private String extractOverrideOrDefault(Map<String, Object> overrideMap,
                                            String key,
                                            String defaultValue) {
        if (overrideMap != null && overrideMap.containsKey(key)) {
            return String.valueOf(overrideMap.get(key));
        }
        return defaultValue;
    }
}
