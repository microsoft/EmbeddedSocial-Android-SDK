/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.fetcher.base.DataState;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.fetcher.base.RequestType;
import com.microsoft.embeddedsocial.server.IAccountService;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.model.account.GetUserAccountRequest;
import com.microsoft.embeddedsocial.server.model.account.GetUserAccountResponse;
import com.microsoft.embeddedsocial.server.model.view.UserAccountView;

import java.util.Collections;
import java.util.List;

/**
 * Fetches the user account info.
 */
public class UserAccountFetcher extends Fetcher<UserAccountView> {
	@Override
	protected List<UserAccountView> fetchDataPage(DataState dataState, RequestType requestType, int pageSize) throws Exception {
		UserAccountView userAccount;
		IAccountService accountService = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getAccountService();
		GetUserAccountRequest userRequest = new GetUserAccountRequest();
		if (requestType == RequestType.SYNC_WITH_CACHE) {
			userRequest.forceCacheUsage();
		}
		GetUserAccountResponse userAccountResponse = accountService.getUserAccount(userRequest);
		userAccount = userAccountResponse.getUser();

		dataState.markDataEnded();
		return Collections.singletonList(userAccount);
	}
}
