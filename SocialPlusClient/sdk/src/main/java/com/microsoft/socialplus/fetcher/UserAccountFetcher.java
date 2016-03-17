/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.fetcher;

import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.data.Preferences;
import com.microsoft.socialplus.fetcher.base.DataState;
import com.microsoft.socialplus.fetcher.base.Fetcher;
import com.microsoft.socialplus.fetcher.base.RequestType;
import com.microsoft.socialplus.server.IAccountService;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.model.account.GetUserAccountRequest;
import com.microsoft.socialplus.server.model.account.GetUserAccountResponse;
import com.microsoft.socialplus.server.model.account.GetUserRequest;
import com.microsoft.socialplus.server.model.view.UserAccountView;

import java.util.Collections;
import java.util.List;

/**
 * Fetches the user account info.
 */
public class UserAccountFetcher extends Fetcher<UserAccountView> {
	@Override
	protected List<UserAccountView> fetchDataPage(DataState dataState, RequestType requestType, int pageSize) throws Exception {
		UserAccountView userAccount;
		IAccountService accountService = GlobalObjectRegistry.getObject(SocialPlusServiceProvider.class).getAccountService();
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
