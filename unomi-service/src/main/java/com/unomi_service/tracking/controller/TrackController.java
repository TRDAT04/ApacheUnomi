package com.unomi_service.tracking.controller;

import com.unomi_service.tracking.dto.TrackEventRequest;
import com.unomi_service.tracking.dto.TrackResponse;
import com.unomi_service.tracking.service.CdpIngestionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/track")
public class TrackController {

    private final CdpIngestionService cdpIngestionService;

    @PostMapping
    public ResponseEntity<TrackResponse> track(@Valid @RequestBody TrackEventRequest request){
        TrackResponse response = cdpIngestionService.track(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/context")
    public ResponseEntity<String> getContext(@RequestParam @NotBlank String sessionId) {
        String context = cdpIngestionService.getContext(sessionId);
        return ResponseEntity.ok(context);
    }
}
