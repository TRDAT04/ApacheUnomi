package com.unomi_service.tracking.service;

import com.unomi_service.tracking.dto.NormalizedEvent;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;


@Component
public class EventEnricher {

    public NormalizedEvent enrich(NormalizedEvent event) {
        return event.toBuilder()
                .requestId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .build();
    }
}
