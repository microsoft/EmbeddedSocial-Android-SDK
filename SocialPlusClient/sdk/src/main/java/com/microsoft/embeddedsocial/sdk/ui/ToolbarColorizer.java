/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.sdk.ui;

import android.support.annotation.ColorRes;

/**
 * Interface to set toolbar colors in SDK activities
 */
public interface ToolbarColorizer {
    @ColorRes int getBackgroundColor();
    @ColorRes int getTextColor();
}
