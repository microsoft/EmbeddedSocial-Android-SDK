/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.signin;

import com.microsoft.embeddedsocial.base.event.AbstractEvent;
import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * User successfully signed-in.
 */
@HandlingThread(ThreadType.MAIN)
public class UserSignedInEvent extends AbstractEvent {
    private final int messageId;

    public UserSignedInEvent(int messageId) {
        this.messageId = messageId;
    }

    public int getMessageId() {
        return messageId;
    }
}
