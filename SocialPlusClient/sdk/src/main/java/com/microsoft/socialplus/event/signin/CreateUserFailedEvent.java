/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.event.signin;

import com.microsoft.socialplus.base.event.AbstractEvent;
import com.microsoft.socialplus.base.event.HandlingThread;
import com.microsoft.socialplus.base.event.ThreadType;
import com.microsoft.socialplus.ui.util.SocialNetworkAccount;

/**
 * Error during user creation.
 */
@HandlingThread(ThreadType.MAIN)
public class CreateUserFailedEvent extends AbstractEvent {
    public CreateUserFailedEvent() {

    }
}
