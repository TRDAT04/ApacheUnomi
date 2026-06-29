package com.unomi_service.rule.service;

import com.unomi_service.rule.config.RuleConfig;
import com.unomi_service.rule.dto.DeployResult;
import com.unomi_service.rule.dto.ValidationResult;

import java.util.Map;

/**
 * Service for managing Unomi rules via configuration.
 */
public interface RuleEngineService {

    ValidationResult validate(RuleConfig config);

    Map<String, Object> buildRule(RuleConfig config);

    DeployResult deployRule(RuleConfig config);
}
