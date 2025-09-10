/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.telemetry;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.analytics.AnalyticsTransmissionTarget;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.sdk.Options;

import android.app.Application;
import android.os.Build;

/**
 * Provides an implementation-agnostic interface to telemetry solutions
 */
public class Telemetry {
    /**
     * Create a custom event with a given name
     * @param name name of the event
     * @return named event
     */
    public static Event newEvent(String name) {
        return new AppCenterEvent(name);
    }

    /**
     * Initialize the analytics library
     * @param application application context
     * @param tenantToken token used to authenticate with the analytics solution
     */
    public static void setAnalyticsSolution(Application application, String tenantToken) {
        if (!isTelemetryEnabled()) {
            return;
        }

        AppCenter.startFromLibrary(application, Analytics.class);
        AnalyticsTransmissionTarget target = Analytics.getTransmissionTarget(tenantToken);
        AppCenterEvent.setTarget(target);

        Event event = newEvent("Init");
        event.log();
    }

    /**
     * Determines if telemetry is supported and enabled on this device
     * @return true if telemetry is enabled, false otherwise
     */
    public static boolean isTelemetryEnabled() {
        Options options = GlobalObjectRegistry.getObject(Options.class);
        if (options == null || options.getTelemetryToken() == null) {
            // No telemetry token found
            return false;
        }

        // AppCenter min SDK supported is 16
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
}
