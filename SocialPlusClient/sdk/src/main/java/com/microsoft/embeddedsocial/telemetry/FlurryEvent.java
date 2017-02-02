/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.telemetry;

import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

class FlurryEvent implements Event {
    private String name;
    private Map<String, String> params;

    public FlurryEvent(String name) {
        this.name = name;
        this.params = new HashMap<>();
    }

    @Override
    public void addParam(String key, String value) {
        params.put(key, value);
    }

    @Override
    public void log() {
        FlurryAgent.logEvent(name, params);
    }
}
