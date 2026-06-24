package org.example.unomi;

import org.apache.unomi.api.Event;
import org.apache.unomi.api.Profile;
import org.apache.unomi.api.actions.Action;
import org.apache.unomi.api.actions.ActionExecutor;
import org.apache.unomi.api.services.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VipActionExecutor implements ActionExecutor {

    private static final Logger logger =
            LoggerFactory.getLogger(VipActionExecutor.class);

    private static final double VIP_THRESHOLD = 30_000_000.0;

    @Override
    public int execute(Action action, Event event) {

        // Trong Unomi 2.x, properties của event lấy qua getProperties()
        Object amountObj = event.getProperties() != null
                ? event.getProperties().get("amount")
                : null;

        if (amountObj == null) {
            logger.warn("VIP plugin: event '{}' missing 'amount' property – skipped", event.getItemId());
            return EventService.NO_CHANGE;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountObj.toString());
        } catch (NumberFormatException e) {
            logger.error("VIP plugin: cannot parse 'amount' value '{}' – skipped", amountObj);
            return EventService.NO_CHANGE;
        }

        if (amount <= 0) {
            return EventService.NO_CHANGE;
        }

        Profile profile = event.getProfile();
        if (profile == null) {
            logger.warn("VIP plugin: no profile attached to event – skipped");
            return EventService.NO_CHANGE;
        }

        Object currentTotal = profile.getProperty("totalSpent");
        double total = (currentTotal instanceof Number)
                ? ((Number) currentTotal).doubleValue()
                : 0.0;

        total += amount;
        profile.setProperty("totalSpent", total);

        logger.info("VIP plugin – profileId={} totalSpent={}", profile.getItemId(), total);

        if (total >= VIP_THRESHOLD) {
            profile.setProperty("isVIP", true);
            logger.info("VIP plugin – profileId={} BECAME VIP!", profile.getItemId());
        }

        return EventService.PROFILE_UPDATED;
    }
}