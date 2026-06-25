package com.unomi_service.service;

import com.unomi_service.config.RuleConfig;
import com.unomi_service.rule.DeployResult;
import com.unomi_service.rule.ValidationResult;

import java.util.Map;

/**
 * Service for managing Unomi rules via configuration.
 */
public interface RuleEngineService {

    ValidationResult validate(RuleConfig config);

    Map<String, Object> buildRule(RuleConfig config);

    DeployResult deployRule(RuleConfig config);
}
