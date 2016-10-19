/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.event;

import com.microsoft.socialplus.account.AuthorizationCause;
import com.microsoft.socialplus.base.event.AbstractEvent;
import com.microsoft.socialplus.base.event.HandlingThread;
import com.microsoft.socialplus.base.event.ThreadType;

/**
 * Event indicated that sign-in is required.
 */
@HandlingThread(ThreadType.CALLING_MAIN)
public class RequestSignInEvent extends AbstractEvent {

	private final AuthorizationCause authorizationCause;

	public RequestSignInEvent(AuthorizationCause authorizationCause) {
		this.authorizationCause = authorizationCause;
	}

	public AuthorizationCause getAuthorizationCause() {
		return authorizationCause;
	}
}
