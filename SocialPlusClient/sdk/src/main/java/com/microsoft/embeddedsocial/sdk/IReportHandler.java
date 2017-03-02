/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.sdk;

import android.content.Context;

import com.microsoft.embeddedsocial.server.model.view.TopicView;

public interface IReportHandler {
    /**
     * Handles generating a report
     * @param context
     * @param topic data associated with this topic
     */
    public void generateReport(Context context, TopicView topic);


    /**
     *
     * @param context current application context
     * @param topic view of content with menu open
     * @return string to display; null if new option should not be added
     */
    public String getDisplayString(Context context, TopicView topic);
}
