package com.unomi_service.rule;

/**
 * Supported action types in the Rule Engine Layer.
 * Each value maps to a specific Apache Unomi 2.4 action plugin type.
 */
public enum ActionType {

    /**
     * Increments a numeric profile property by 1.
     * Unomi action: {@code incrementPropertyAction}
     * Required fields: profileProperty
     */
    INCREMENT,

    /**
     * Adds the value of an event property to a numeric profile property.
     * Unomi action: {@code addToNumberAction}
     * Required fields: eventProperty, profileProperty
     */
    SUM,

    /**
     * Copies an event property value directly to a profile property.
     * Unomi action: {@code setPropertyAction}
     * Required fields: eventProperty, profileProperty
     */
    SET_PROPERTY,

    /**
     * Appends an event property value to a profile set/list property.
     * Unomi action: {@code addToProfileSetsAction}
     * Required fields: eventProperty, profileProperty
     */
    ADD_TO_SET
}
