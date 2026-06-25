package org.example.unomi;

import org.apache.unomi.api.Event;
import org.apache.unomi.api.Profile;
import org.apache.unomi.api.actions.Action;
import org.apache.unomi.api.actions.ActionExecutor;
import org.apache.unomi.api.services.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;

public class AddToNumberAction implements ActionExecutor {

    private static final Logger logger = LoggerFactory.getLogger(AddToNumberAction.class);

    @Override
    public int execute(Action action, Event event) {
        Profile profile = event.getProfile();
        if (profile == null) {
            return EventService.NO_CHANGE;
        }

        String eventProperty = (String) action.getParameterValues().get("eventProperty");
        String profileProperty = (String) action.getParameterValues().get("profileProperty");
        String fallbackValue = (String) action.getParameterValues().get("fallbackValue");

        if (eventProperty == null || profileProperty == null) {
            logger.warn("Missing required parameters for addToNumberAction");
            return EventService.NO_CHANGE;
        }

        // Get value from event
        // We expect eventProperty to be like "properties.amount"
        // Let's strip "properties." prefix for easier extraction from event.getProperties()
        Object eventValueObj = null;
        if (eventProperty.startsWith("properties.")) {
            String propName = eventProperty.substring("properties.".length());
            eventValueObj = event.getProperties().get(propName);
        } else {
            eventValueObj = event.getProperty(eventProperty);
        }

        if (eventValueObj == null && fallbackValue != null) {
            eventValueObj = fallbackValue;
        }

        if (eventValueObj == null) {
            return EventService.NO_CHANGE;
        }

        double valueToAdd = 0.0;
        try {
            valueToAdd = Double.parseDouble(eventValueObj.toString());
        } catch (NumberFormatException e) {
            logger.warn("Could not parse event property value to double: {}", eventValueObj);
            return EventService.NO_CHANGE;
        }

        // Get current profile value
        // profileProperty is like "properties.totalSpent"
        String profilePropName = profileProperty.startsWith("properties.") 
                                 ? profileProperty.substring("properties.".length()) 
                                 : profileProperty;
                                 
        Object currentProfileValueObj = profile.getProperty(profilePropName);
        double currentProfileValue = 0.0;
        if (currentProfileValueObj != null) {
            try {
                currentProfileValue = Double.parseDouble(currentProfileValueObj.toString());
            } catch (NumberFormatException e) {
                logger.warn("Could not parse profile property value to double: {}", currentProfileValueObj);
                currentProfileValue = 0.0;
            }
        }

        double newValue = currentProfileValue + valueToAdd;
        
        // Update profile
        profile.setProperty(profilePropName, newValue);

        return EventService.PROFILE_UPDATED;
    }
}
