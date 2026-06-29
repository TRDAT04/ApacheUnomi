package com.unomi_service.tracking.service;

import com.unomi_service.config.UnomiProperties;
import com.unomi_service.tracking.dto.NormalizedEvent;
import com.unomi_service.tracking.dto.TrackEventRequest;
import com.unomi_service.tracking.dto.TrackResponse;
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
public class CdpIngestionService {

    private final RestClient          unomiRestClient;
    private final UnomiProperties     unomiProperties;
    private final EventNormalizer     normalizer;
    private final EventEnricher       enricher;
    private final UnomiPayloadBuilder payloadBuilder;

    public TrackResponse track(TrackEventRequest request) {

        NormalizedEvent normalized = normalizer.normalize(request, unomiProperties);
        NormalizedEvent enriched = enricher.enrich(normalized);

        log.info("[CDP] requestId={} sessionId={} eventType={} target={}/{}",
                enriched.getRequestId(),
                enriched.getSessionId(),
                enriched.getEventType(),
                enriched.getTargetItemType(),
                enriched.getTargetItemId());

        Map<String, Object> payload = payloadBuilder.build(enriched);

        String unomiResponse = sendToUnomi(payload);

        log.debug("[CDP] requestId={} unomiResponse={}", enriched.getRequestId(), unomiResponse);
        return TrackResponse.success(
                enriched.getEventType(),
                enriched.getRequestId(),
                enriched.getTimestamp(),
                unomiResponse
        );
    }


    public String getContext(String sessionId) {
        Map<String, Object> source = buildPageSource("context-query");

        Map<String, Object> contextRequest = new LinkedHashMap<>();
        contextRequest.put("source",source);
        contextRequest.put("requiredProfileProperties", List.of("*"));
        contextRequest.put("requiredSessionProperties", List.of("*"));
        contextRequest.put("requireSegments",true);
        contextRequest.put("requireScores",true);

        log.debug("[CDP] getContext sessionId={}", sessionId);
        return unomiRestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/cxs/context.json")
                        .queryParam("sessionId", sessionId)
                        .build())
                .body(contextRequest)
                .retrieve()
                .body(String.class);
    }


    private String sendToUnomi(Map<String, Object> payload) {
        log.info("[CDP] sending payload to Unomi: {}", payload);

        String response = unomiRestClient.post()
                .uri("/cxs/eventcollector")
                .body(payload)
                .retrieve()
                .body(String.class);

        log.info("[CDP] unomi response: {}", response);
        return response;
    }

    private Map<String, Object> buildPageSource(String itemId) {
        Map<String, Object> source = new LinkedHashMap<>();
        source.put("itemType", "page");
        source.put("itemId",   itemId);
        source.put("scope",    unomiProperties.scope());
        return source;
    }
}
