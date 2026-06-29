package com.unomi_service.rule.controller;

import com.unomi_service.rule.dto.DeployResult;
import com.unomi_service.rule.config.RuleConfig;
import com.unomi_service.rule.service.RuleEngineService;
import com.unomi_service.rule.dto.ValidationResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/rules")
public class RuleController {

    private final RuleEngineService ruleEngineService;

    @PostMapping("/validate")
    public ResponseEntity<ValidationResult> validate(@Valid @RequestBody RuleConfig config) {
        ValidationResult result = ruleEngineService.validate(config);
        if (result.isValid()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/build")
    public ResponseEntity<Map<String, Object>> buildRule(@Valid @RequestBody RuleConfig config) {
        Map<String, Object> rulePayload = ruleEngineService.buildRule(config);
        return ResponseEntity.ok(rulePayload);
    }

    @PostMapping("/deploy")
    public ResponseEntity<DeployResult> deployRule(@Valid @RequestBody RuleConfig config) {
        DeployResult result = ruleEngineService.deployRule(config);
        return ResponseEntity.ok(result);
    }
}
