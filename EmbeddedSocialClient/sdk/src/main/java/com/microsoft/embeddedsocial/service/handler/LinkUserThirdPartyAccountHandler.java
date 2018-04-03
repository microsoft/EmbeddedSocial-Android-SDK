/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.handler;

import android.content.Intent;
import android.os.Bundle;

import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.base.service.IServiceIntentHandler;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.event.LinkUserThirdPartyAccountEvent;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.server.IAccountService;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.exception.StatusException;
import com.microsoft.embeddedsocial.server.model.account.LinkThirdPartyRequest;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.service.ServiceAction;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;

/**
 * Sends a link user third party account request to the server.
 */
public class LinkUserThirdPartyAccountHandler implements IServiceIntentHandler<ServiceAction> {

	@Override
	public void handleIntent(ServiceAction action, Intent intent) {
		final SocialNetworkAccount account = intent.getExtras().getParcelable(IntentExtras.SOCIAL_NETWORK_ACCOUNT);

		IAccountService service = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getAccountService();
		LinkThirdPartyRequest linkUserThirdPartyAccountRequest = new LinkThirdPartyRequest(
				account.getAccountType(),
				account.getThirdPartyAccessToken());

		intent.removeExtra(IntentExtras.SOCIAL_NETWORK_ACCOUNT);
		account.clearTokens();
		try {
			service.linkUserThirdPartyAccount(linkUserThirdPartyAccountRequest);
			EventBus.post(LinkUserThirdPartyAccountEvent.createLinkEvent(account));
		} catch (NetworkRequestException e) {
			DebugLog.logException(e);
			LinkUserThirdPartyAccountEvent event;
			if (e instanceof StatusException) {
				event = LinkUserThirdPartyAccountEvent.createLinkEvent(account, e.getMessage(),
						((StatusException)e).getStatusCode());
			} else {
				event = LinkUserThirdPartyAccountEvent.createLinkEvent(account, e.getMessage());
			}
			EventBus.post(event);
		}
	}

	@Override
	public void dispose() {

	}
}
