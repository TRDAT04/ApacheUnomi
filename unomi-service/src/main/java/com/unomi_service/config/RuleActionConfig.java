package com.unomi_service.config;

import com.unomi_service.rule.ActionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Describes a single action to be attached to a rule.
 *
 * <p>Example (JSON):
 * <pre>
 * {
 *   "type": "SUM",
 *   "eventProperty": "amount",
 *   "profileProperty": "totalSpent"
 * }
 * </pre>
 */
@Data
public class RuleActionConfig {

    /**
     * The action type to execute.
     * Must not be null — determines which Unomi action plugin is invoked.
     */
    @NotNull(message = "action type must not be null")
    private ActionType type;

    /**
     * The source field name from the incoming event's {@code properties} map.
     * Required for: SUM, SET_PROPERTY, ADD_TO_SET.
     * Not required for: INCREMENT (increments by 1, no event value needed).
     */
    private String eventProperty;

    /**
     * The target field name on the Unomi profile.
     * The builder will automatically prefix this with {@code properties.} for
     * Elasticsearch mapping compatibility.
     * Example: "totalSpent" → stored as "properties.totalSpent"
     */
    @NotNull(message = "profileProperty must not be null")
    private String profileProperty;

    /**
     * Optional default value used when the eventProperty is absent on the event.
     * Must be a numeric string (e.g., "0") for SUM/INCREMENT actions to prevent
     * Elasticsearch type-mismatch errors.
     */
    private String defaultValue;
}
