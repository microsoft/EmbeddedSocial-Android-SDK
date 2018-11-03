/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.telemetry;

import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.analytics.AnalyticsTransmissionTarget;
import com.microsoft.appcenter.analytics.EventProperties;
import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.data.Preferences;
import com.microsoft.embeddedsocial.sdk.Options;

/**
 * Event implementation using Microsoft App Center
 */
class AppCenterEvent implements Event {
    private static AnalyticsTransmissionTarget target;
    private final String name;
    private final EventProperties properties;

    public AppCenterEvent(String name) {
        this.name = name;
        properties = new EventProperties();
        UserAccount currentUser = UserAccount.getInstance();
        if (currentUser.isSignedIn()) {
            properties.set("UserHandle", currentUser.getUserHandle());
        }
        properties.set("InstanceId", Preferences.getInstance().getInstanceId());
    }

    @Override
    public void addParam(String key, String value) {
        properties.set(key, value);
    }

    @Override
    public void log() {
        // Reset the transmission token if it was lost by the JVM
        if (target == null) {
            Options options = GlobalObjectRegistry.getObject(Options.class);
            if (options != null) {
                String telemetryToken = options.getTelemetryToken();
                if (telemetryToken != null) {
                    setTarget(Analytics.getTransmissionTarget(telemetryToken));
                }
            }
        }

        target.trackEvent(name, properties);
    }

    /**
     * Sets the transmission target for tracked events
     * @param target
     */
    public static void setTarget(AnalyticsTransmissionTarget target) {
        AppCenterEvent.target = target;
    }
}