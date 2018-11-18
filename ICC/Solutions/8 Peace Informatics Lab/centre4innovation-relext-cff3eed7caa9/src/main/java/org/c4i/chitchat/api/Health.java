package org.c4i.chitchat.api;

import com.codahale.metrics.health.HealthCheck;

/**
 * Check if the settings in settings.yml are correct.
 * @author Arvid Halma
 * @version 15-11-2015 - 12:13
 */
public class Health extends HealthCheck {
    private Config configuration;

    public Health(Config configuration) {
        this.configuration = configuration;
    }

    @Override
    protected Result check() throws Exception {
        return Result.healthy();
    }
}