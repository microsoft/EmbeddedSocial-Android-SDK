/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.sdk.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class EmbeddedSocialActivity extends AppCompatActivity {
    public final Bundle getHostAppExtras() {
        return getIntent().getExtras();
    }
}