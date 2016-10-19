/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.event.click;

import com.microsoft.socialplus.base.event.AbstractEvent;
import com.microsoft.socialplus.base.event.HandlingThread;
import com.microsoft.socialplus.base.event.ThreadType;

/**
 * Feed display method changed event (i.e. user clicked on list or gallery button).
 */
@HandlingThread(ThreadType.CALLING_MAIN)
public class DisplayMethodChangedEvent extends AbstractEvent {
}
