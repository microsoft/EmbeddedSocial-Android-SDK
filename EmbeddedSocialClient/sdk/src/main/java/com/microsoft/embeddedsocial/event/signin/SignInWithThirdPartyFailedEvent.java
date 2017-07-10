/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.signin;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.base.event.AbstractEvent;
import com.microsoft.embeddedsocial.base.event.ThreadType;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;

/**
 * Error during sign-in with third party account.
 */
@HandlingThread(ThreadType.MAIN)
public class SignInWithThirdPartyFailedEvent extends AbstractEvent {

}
