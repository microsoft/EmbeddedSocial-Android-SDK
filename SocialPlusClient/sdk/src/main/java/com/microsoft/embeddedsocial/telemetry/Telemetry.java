/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.telemetry;

import android.content.Context;

public class Telemetry {

    private static Analytics analytics = Analytics.FLURRY;

    public static Event newEvent(Context context, String name) {
        switch (analytics) {
            case FIREBASE:
                return new FirebaseEvent(context, name);
            default: // FLURRY
                return new FlurryEvent(name);
        }
    }

    // TODO provide constructor which takes a map or variable number of params to immediately populate the event params

    public static void setAnalyticsSolution(Analytics solution) {
        analytics = solution;
    }

    enum Analytics {
        FLURRY,
        FIREBASE;
    }
}
