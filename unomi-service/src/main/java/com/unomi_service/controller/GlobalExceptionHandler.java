package com.unomi_service.controller;

import com.unomi_service.exception.RuleDeploymentException;
import com.unomi_service.exception.RuleValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuleValidationException.class)
    public ResponseEntity<Map<String, Object>> handleRuleValidationException(RuleValidationException ex) {
        log.warn("Rule validation failed: {}", ex.getViolations());
        
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Rule Validation Failed");
        body.put("message", ex.getMessage());
        body.put("violations", ex.getViolations());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(RuleDeploymentException.class)
    public ResponseEntity<Map<String, Object>> handleRuleDeploymentException(RuleDeploymentException ex) {
        log.error("Rule deployment failed: {}", ex.getMessage(), ex);
        
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Rule Deployment Failed");
        body.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }
}
