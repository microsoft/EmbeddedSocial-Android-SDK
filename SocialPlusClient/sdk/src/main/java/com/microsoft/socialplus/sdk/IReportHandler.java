/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.sdk;

import android.content.Context;

public interface IReportHandler {
    /**
     * Handles generating a report
     * @param context
     * @param reportContent topic friendly name
     */
    public void generateReport(Context context, String reportContent);

    /**
     * Provides the string to show the user
     */
    public String getDisplayString();
}
