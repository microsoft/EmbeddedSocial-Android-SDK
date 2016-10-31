/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.sdk;

import android.content.Context;

import com.microsoft.socialplus.server.model.view.TopicView;

public interface IReportHandler {
    /**
     * Handles generating a report
     * @param context
     * @param reportContent topic friendly name
     */
    public void generateReport(Context context, String reportContent);


    /**
     *
     * @param context current application context
     * @param topic view of content with menu open
     * @return string to display; null if new option should not be added
     */
    public String getDisplayString(Context context, TopicView topic);
}
