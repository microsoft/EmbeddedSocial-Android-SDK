/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.handler;

import android.content.Intent;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.actions.Action;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.server.IAccountService;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.account.DeleteUserRequest;
import com.microsoft.embeddedsocial.service.ServiceAction;

/**
 * Sends a delete account request to the server.
 */
public class DeleteAccountHandler extends ActionHandler {
	@Override
	protected void handleAction(Action action, ServiceAction serviceAction, Intent intent) {
		IAccountService server = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getAccountService();
		try {
			server.deleteUser(new DeleteUserRequest());
			UserAccount.getInstance().signOut();
			action.complete();
		} catch (NetworkRequestException e) {
			DebugLog.logException(e);
			action.fail();
		}
	}
}
