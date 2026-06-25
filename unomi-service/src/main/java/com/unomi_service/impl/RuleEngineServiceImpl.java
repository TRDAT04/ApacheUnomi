package com.unomi_service.impl;

import com.unomi_service.config.RuleConfig;
import com.unomi_service.config.UnomiProperties;
import com.unomi_service.exception.RuleDeploymentException;
import com.unomi_service.exception.RuleValidationException;
import com.unomi_service.rule.*;
import com.unomi_service.service.RuleEngineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Instant;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RuleEngineServiceImpl implements RuleEngineService {

    private final RuleValidator validator;
    private final UnomiRuleBuilder builder;
    private final RestClient unomiRestClient;
    private final UnomiProperties unomiProperties;

    @Override
    public ValidationResult validate(RuleConfig config) {
        log.debug("Validating RuleConfig for eventType: {}", config.getEventType());
        return validator.validate(config);
    }

    @Override
    public Map<String, Object> buildRule(RuleConfig config) {
        ValidationResult validationResult = validate(config);
        if (!validationResult.isValid()) {
            throw new RuleValidationException("RuleConfig validation failed", validationResult.violations());
        }

        // Inject default scope if not provided
        if (!StringUtils.hasText(config.getScope())) {
            config.setScope(unomiProperties.scope());
        }

        return builder.build(config);
    }

    @Override
    public DeployResult deployRule(RuleConfig config) {
        Map<String, Object> unomiPayload = buildRule(config);
        
        @SuppressWarnings("unchecked")
        Map<String, Object> metadata = (Map<String, Object>) unomiPayload.get("metadata");
        String ruleId = (String) metadata.get("id");

        log.info("Deploying rule '{}' to Apache Unomi...", ruleId);

        try {
            String unomiResponse = unomiRestClient.post()
                    .uri("/cxs/rules")
                    .body(unomiPayload)
                    .retrieve()
                    .body(String.class);
            
            log.info("Successfully deployed rule '{}'", ruleId);
            return new DeployResult(ruleId, "SUCCESS", unomiResponse, Instant.now());
            
        } catch (RestClientResponseException e) {
            log.error("Failed to deploy rule '{}'. Unomi returned HTTP {}: {}", ruleId, e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuleDeploymentException("Failed to deploy rule to Unomi. Status: " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("Failed to deploy rule '{}'. Error: {}", ruleId, e.getMessage(), e);
            throw new RuleDeploymentException("Failed to deploy rule to Unomi", e);
        }
    }
}
