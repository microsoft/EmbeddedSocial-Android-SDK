/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.sdk.ui;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public interface AppProfile {
    @StringRes int getName();
    @DrawableRes int getImage();
}
