/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.service.handler;

import android.content.Intent;

import com.microsoft.socialplus.autorest.models.IdentityProvider;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.event.EventBus;
import com.microsoft.socialplus.base.service.IServiceIntentHandler;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.event.LinkUserThirdPartyAccountEvent;
import com.microsoft.socialplus.server.IAccountService;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.model.account.UnlinkUserThirdPartyAccountRequest;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.service.ServiceAction;

/**
 * Sends a unlink user third party account request to the server.
 */
public class UnlinkUserThirdPartyAccountHandler implements IServiceIntentHandler<ServiceAction> {
	@Override
	public void handleIntent(ServiceAction action, Intent intent) {
		final IdentityProvider accountType = IdentityProvider.fromValue(intent.getExtras().getString(IntentExtras.IDENTITY_PROVIDER));

		IAccountService accountService = GlobalObjectRegistry.getObject(SocialPlusServiceProvider.class).getAccountService();
		UnlinkUserThirdPartyAccountRequest unlinkUserThirdPartyAccountRequest
				= new UnlinkUserThirdPartyAccountRequest(accountType);

		try {
			accountService.unlinkUserThirdPartyAccount(unlinkUserThirdPartyAccountRequest);
			EventBus.post(LinkUserThirdPartyAccountEvent.createUnlinkEvent(accountType));
		} catch (Exception e) {
			DebugLog.logException(e);
			EventBus.post(LinkUserThirdPartyAccountEvent.createUnlinkEvent(accountType, e.getCause().getMessage()));
		}
	}

	@Override
	public void dispose() {

	}
}
