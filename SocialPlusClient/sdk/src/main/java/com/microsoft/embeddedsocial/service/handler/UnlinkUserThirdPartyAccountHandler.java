/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.handler;

import android.content.Intent;

import com.microsoft.embeddedsocial.base.service.IServiceIntentHandler;
import com.microsoft.embeddedsocial.event.LinkUserThirdPartyAccountEvent;
import com.microsoft.embeddedsocial.server.exception.StatusException;
import com.microsoft.embeddedsocial.server.model.account.UnlinkUserThirdPartyAccountRequest;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.server.IAccountService;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.service.ServiceAction;

/**
 * Sends a unlink user third party account request to the server.
 */
public class UnlinkUserThirdPartyAccountHandler implements IServiceIntentHandler<ServiceAction> {
	@Override
	public void handleIntent(ServiceAction action, Intent intent) {
		final IdentityProvider accountType = IdentityProvider.fromValue(intent.getExtras().getString(IntentExtras.IDENTITY_PROVIDER));

		IAccountService accountService = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getAccountService();
		UnlinkUserThirdPartyAccountRequest unlinkUserThirdPartyAccountRequest
				= new UnlinkUserThirdPartyAccountRequest(accountType);

		try {
			accountService.unlinkUserThirdPartyAccount(unlinkUserThirdPartyAccountRequest);
			EventBus.post(LinkUserThirdPartyAccountEvent.createUnlinkEvent(accountType));
		} catch (Exception e) {
			DebugLog.logException(e);
			LinkUserThirdPartyAccountEvent event;
			if (e instanceof StatusException) {
				event = LinkUserThirdPartyAccountEvent.createUnlinkEvent(accountType, e.getMessage(),
						((StatusException)e).getStatusCode());
			} else {
				event = LinkUserThirdPartyAccountEvent.createUnlinkEvent(accountType, e.getMessage());
			}
			EventBus.post(event);
		}
	}

	@Override
	public void dispose() {

	}
}
