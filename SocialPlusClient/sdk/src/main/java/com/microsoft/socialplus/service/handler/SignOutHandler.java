/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.service.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.webkit.CookieManager;

import com.microsoft.socialplus.actions.Action;
import com.microsoft.socialplus.auth.MicrosoftLiveAuthenticator;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.server.IAuthenticationService;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UserRequest;
import com.microsoft.socialplus.server.model.auth.SignOutRequest;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.service.ServiceAction;

/**
 * Performs sign-out.
 */
public class SignOutHandler extends ActionHandler {

	private final Context context;

	public SignOutHandler(Context context) {
		this.context = context;
	}

	@Override
	protected void handleAction(Action action, ServiceAction serviceAction, Intent intent) {
		String authorization = intent.getStringExtra(IntentExtras.AUTHORIZATION);
		IAuthenticationService server = GlobalObjectRegistry.getObject(SocialPlusServiceProvider.class).getAuthenticationService();
		try {
			SignOutRequest request = new SignOutRequest(authorization);
			server.signOut(request);
		} catch (NetworkRequestException e) {
			// ignore server errors
			DebugLog.logException(e);
		}
		MicrosoftLiveAuthenticator.signOut(context);
		clearCookies();
	}

	private void clearCookies() {
		CookieManager cookieManager = CookieManager.getInstance();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			cookieManager.removeAllCookies(null);
		} else {
			//noinspection deprecation
			cookieManager.removeAllCookie();
		}
	}
}
