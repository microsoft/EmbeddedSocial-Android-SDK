/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event;

import com.microsoft.embeddedsocial.account.AuthorizationCause;
import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.base.event.ThreadType;

import android.support.v4.app.Fragment;

/**
 * Event indicated that sign-in is required.
 */
@HandlingThread(ThreadType.CALLING_MAIN)
public class RequestSignInEvent extends BaseCommonBehaviorEvent {

    private final AuthorizationCause authorizationCause;

    public RequestSignInEvent(Fragment source, AuthorizationCause authorizationCause) {
        super(source);
        this.authorizationCause = authorizationCause;
    }

    public AuthorizationCause getAuthorizationCause() {
        return authorizationCause;
    }
}
