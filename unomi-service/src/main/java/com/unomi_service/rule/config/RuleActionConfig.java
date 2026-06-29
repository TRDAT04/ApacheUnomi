package com.unomi_service.rule.config;

import com.unomi_service.rule.dto.ActionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class RuleActionConfig {


    @NotNull(message = "action type must not be null")
    private ActionType type;


    private String eventProperty;

    @NotNull(message = "profileProperty must not be null")
    private String profileProperty;


    private String defaultValue;
}
