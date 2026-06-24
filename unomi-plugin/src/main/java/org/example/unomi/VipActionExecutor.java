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

    @Override
    public int execute(Action action, Event event) {

        Object amountObj = event.getProperty("amount");

        if (amountObj == null) {
            return EventService.NO_CHANGE;
        }

        double amount = Double.parseDouble(amountObj.toString());

        Profile profile = event.getProfile();
        if (profile == null) {
            return EventService.NO_CHANGE;
        }

        Double total = (Double) profile.getProperty("totalSpent");
        if (total == null) total = 0.0;

        total += amount;
        profile.setProperty("totalSpent", total);

        logger.info("VIP plugin totalSpent = {}", total);

        if (total >= 30_000_000) {
            profile.setProperty("isVIP", true);
            logger.info("PROFILE BECAME VIP!");
        }

        return EventService.PROFILE_UPDATED;
    }
}