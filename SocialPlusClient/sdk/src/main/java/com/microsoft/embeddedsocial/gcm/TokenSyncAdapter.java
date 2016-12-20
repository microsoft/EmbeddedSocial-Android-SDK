/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.gcm;

import com.microsoft.embeddedsocial.server.sync.exception.SynchronizationException;
import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.server.INotificationService;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.notification.RegisterPushNotificationRequest;
import com.microsoft.embeddedsocial.server.sync.ISynchronizable;

/**
 * Is used to register GCM token on Embedded Social server.
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
			.getObject(EmbeddedSocialServiceProvider.class)
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
