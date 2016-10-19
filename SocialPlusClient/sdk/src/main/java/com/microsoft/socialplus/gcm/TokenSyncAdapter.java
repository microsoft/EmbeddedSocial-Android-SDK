/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.gcm;

import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.server.INotificationService;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.notification.RegisterPushNotificationRequest;
import com.microsoft.socialplus.server.sync.ISynchronizable;
import com.microsoft.socialplus.server.sync.exception.SynchronizationException;

/**
 * Is used to register GCM token on Social Plus server.
 */
class TokenSyncAdapter implements ISynchronizable {

	private final GcmTokenHolder tokenHolder;
	private boolean launched;

	TokenSyncAdapter(GcmTokenHolder tokenHolder) {
		this.tokenHolder = tokenHolder;
	}

	@Override
	public void synchronize() throws SynchronizationException {
		if (!validatePreconditions()) {
			return;
		}
		INotificationService notificationService = GlobalObjectRegistry
			.getObject(SocialPlusServiceProvider.class)
			.getNotificationService();

		RegisterPushNotificationRequest request = new RegisterPushNotificationRequest(
			tokenHolder.getToken(),
			tokenHolder.getTokenTimestamp()
		);
		try {
			notificationService.registerPushNotification(request);
			launched = true;
		} catch (NetworkRequestException e) {
			throw new SynchronizationException(e);
		}
	}

	private boolean validatePreconditions() throws SynchronizationException {
		boolean result = true;

		if (tokenHolder.isTokenSynchronized()) {
			DebugLog.w("GCM token has already been synchronized");
			result = false;
		} else if (!tokenHolder.hasValidToken()) {
			DebugLog.w("GCM token is not valid");
			result = false;
		} else if (!UserAccount.getInstance().isSignedIn()) {
			throw new SynchronizationException("User is not signed in");
		}

		return result;
	}

	@Override
	public void onSynchronizationSuccess() {
		if (launched) {
			tokenHolder.markTokenSynchronized();
		}
	}
}
