/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.service.handler;

import android.content.Intent;

import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.event.EventBus;
import com.microsoft.socialplus.base.service.IServiceIntentHandler;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.event.LinkUserThirdPartyAccountEvent;
import com.microsoft.socialplus.server.IAccountService;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.account.LinkThirdPartyRequest;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.service.ServiceAction;
import com.microsoft.socialplus.ui.util.SocialNetworkAccount;

/**
 * Sends a link user third party account request to the server.
 */
public class LinkUserThirdPartyAccountHandler implements IServiceIntentHandler<ServiceAction> {

	@Override
	public void handleIntent(ServiceAction action, Intent intent) {
		final SocialNetworkAccount account = intent.getExtras().getParcelable(IntentExtras.SOCIAL_NETWORK_ACCOUNT);

		IAccountService service = GlobalObjectRegistry.getObject(SocialPlusServiceProvider.class).getAccountService();
		LinkThirdPartyRequest linkUserThirdPartyAccountRequest = new LinkThirdPartyRequest(
				account.getAccountType(),
				account.getThirdPartyAccessToken());

		try {
			service.linkUserThirdPartyAccount(linkUserThirdPartyAccountRequest);
			EventBus.post(LinkUserThirdPartyAccountEvent.createLinkEvent(account));
		} catch (NetworkRequestException e) {
			DebugLog.logException(e);
			EventBus.post(LinkUserThirdPartyAccountEvent.createLinkEvent(account, e.getCause().getMessage()));
		}
	}

	@Override
	public void dispose() {

	}
}
