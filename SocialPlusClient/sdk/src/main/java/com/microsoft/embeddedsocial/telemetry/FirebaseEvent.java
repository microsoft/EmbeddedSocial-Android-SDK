/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.telemetry;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

class FirebaseEvent implements Event {
    private FirebaseAnalytics firebaseAnalytics;
    private String name;
    private Bundle params;

    public FirebaseEvent(Context context, String name) {
        this.firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        this.name = name;
        this.params = new Bundle();
    }

    @Override
    public void addParam(String key, String value) {
        params.putString(key, value);
    }

    @Override
    public void log() {
        firebaseAnalytics.logEvent(name, params);
    }
}
