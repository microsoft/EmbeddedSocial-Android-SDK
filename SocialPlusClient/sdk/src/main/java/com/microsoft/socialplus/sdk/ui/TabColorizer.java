/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.sdk.ui;

import android.support.annotation.ColorRes;

/**
 * Interface to set tab colors in SDK activities with tabs
 */
public interface TabColorizer {
    @ColorRes int getBackgroundColor();
    @ColorRes int getActiveTextColor();
    @ColorRes int getNotActiveTextColor();
    @ColorRes int getSelectorColor();
}
