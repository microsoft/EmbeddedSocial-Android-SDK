/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.signin;

import com.microsoft.embeddedsocial.base.event.AbstractEvent;
import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * Error during user creation.
 */
@HandlingThread(ThreadType.MAIN)
public class CreateUserFailedEvent extends AbstractEvent {
    public CreateUserFailedEvent() {

    }
}
