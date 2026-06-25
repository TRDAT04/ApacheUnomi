package com.unomi_service.exception;

import lombok.Getter;

import java.util.List;

/**
 * Exception thrown when a rule configuration fails validation.
 */
@Getter
public class RuleValidationException extends RuntimeException {

    private final List<String> violations;

    public RuleValidationException(String message, List<String> violations) {
        super(message);
        this.violations = violations;
    }
}
