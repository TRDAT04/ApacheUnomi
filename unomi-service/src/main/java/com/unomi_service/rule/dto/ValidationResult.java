package com.unomi_service.rule.dto;

import com.unomi_service.rule.config.RuleConfig;

import java.util.List;

/**
 * Result of validating a RuleConfig.
 */
public record ValidationResult(boolean isValid, List<String> violations) {
}
