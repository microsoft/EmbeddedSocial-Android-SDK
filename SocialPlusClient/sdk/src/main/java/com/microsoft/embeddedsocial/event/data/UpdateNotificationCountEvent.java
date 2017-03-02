/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.data;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.base.event.AbstractEvent;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * Notifies that notification count was updated.
 */
@HandlingThread(ThreadType.MAIN)
public class UpdateNotificationCountEvent extends AbstractEvent {
}
