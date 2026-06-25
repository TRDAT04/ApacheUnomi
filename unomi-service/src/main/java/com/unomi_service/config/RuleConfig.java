package com.unomi_service.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * Top-level rule configuration. This is the only object you need to define
 * in order to generate and deploy a valid Apache Unomi 2.4 rule.
 *
 * <p>Example (JSON):
 * <pre>
 * {
 *   "name": "Track Purchase",
 *   "description": "Increments purchase count and sums total spent on every purchase event",
 *   "eventType": "purchase",
 *   "priority": 5,
 *   "raiseEventOnlyOnceForProfile": false,
 *   "actions": [
 *     { "type": "INCREMENT", "profileProperty": "purchaseCount" },
 *     { "type": "SUM", "eventProperty": "amount", "profileProperty": "totalSpent" }
 *   ]
 * }
 * </pre>
 */
@Data
public class RuleConfig {

    /**
     * Optional rule ID (itemId in Unomi).
     * If null or blank, the builder auto-generates: {@code <scope>-<eventType>-rule}.
     */
    private String ruleId;

    /** Human-readable rule name shown in Unomi admin UI. */
    @NotBlank(message = "rule name must not be blank")
    private String name;

    /** Optional description for documentation purposes. */
    private String description;

    /**
     * Unomi scope — if null, the service falls back to the configured
     * {@code unomi.scope} application property (e.g., "myweb").
     */
    private String scope;

    /**
     * The event type that triggers this rule (e.g., "purchase", "product_view").
     * Maps directly to Unomi's {@code eventTypeCondition.eventTypeId}.
     */
    @NotBlank(message = "eventType must not be blank")
    private String eventType;

    /**
     * Rule evaluation priority. Lower numbers = higher priority.
     * Defaults to 5 if not specified.
     */
    private int priority = 5;

    /**
     * If true, the rule fires only once per profile lifetime (e.g., first-purchase bonus).
     * Defaults to false (fires on every matching event).
     */
    private boolean raiseEventOnlyOnceForProfile = false;

    /**
     * If true, the rule fires only once per session.
     * Defaults to false.
     */
    private boolean raiseEventOnlyOnceForSession = false;

    /** List of actions to execute when the rule condition is met. */
    @NotEmpty(message = "actions must not be empty — a rule with no actions is a no-op")
    @Valid
    private List<RuleActionConfig> actions;
}
