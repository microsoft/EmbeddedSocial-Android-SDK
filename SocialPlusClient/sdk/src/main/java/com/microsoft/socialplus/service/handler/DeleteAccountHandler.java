/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.service.handler;

import android.content.Intent;

import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.actions.Action;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.server.IAccountService;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UserRequest;
import com.microsoft.socialplus.server.model.account.DeleteUserRequest;
import com.microsoft.socialplus.service.ServiceAction;

/**
 * Sends a delete account request to the server.
 */
public class DeleteAccountHandler extends ActionHandler {
	@Override
	protected void handleAction(Action action, ServiceAction serviceAction, Intent intent) {
		IAccountService server = GlobalObjectRegistry.getObject(SocialPlusServiceProvider.class).getAccountService();
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
