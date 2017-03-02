/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.fetcher.base.DataState;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.fetcher.base.RequestType;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.account.GetUserAccountRequest;
import com.microsoft.embeddedsocial.server.model.account.GetUserAccountResponse;
import com.microsoft.embeddedsocial.server.model.account.GetUserProfileRequest;
import com.microsoft.embeddedsocial.server.model.view.UserAccountView;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.server.IAccountService;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.model.view.UserProfileView;

import java.util.Collections;
import java.util.List;

/**
 * Fetches the account info.
 */
class ProfileFetcher extends Fetcher<AccountData> {

	private final String userHandle;

	public ProfileFetcher(String userHandle) {
		this.userHandle = userHandle;
	}

	@Override
	protected List<AccountData> fetchDataPage(DataState dataState, RequestType requestType, int pageSize) throws Exception {
		AccountData profile;
		if (requestType == RequestType.SYNC_WITH_CACHE && UserAccount.getInstance().isCurrentUser(userHandle)) {
			profile = UserAccount.getInstance().getAccountDetails();
		} else {
			profile = readProfile(requestType);
		}
		dataState.markDataEnded();
		return Collections.singletonList(profile);
	}

	private AccountData readProfile(RequestType requestType) throws NetworkRequestException {
		try {
			IAccountService accountService = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getAccountService();
			UserProfileView userProfile = getUserProfile(accountService, requestType);
			AccountData accountData = new AccountData(userProfile);
			if (UserAccount.getInstance().isCurrentUser(userHandle)) {
				// TODO: check if these data are still needed
				UserAccountView user = getUserAccount(accountService, requestType);
				accountData.setAccountTypeFromThirdPartyAccounts(user.getThirdPartyAccounts());
				UserAccount.getInstance().updateAccountDetails(accountData);
			}
			return accountData;
		} catch (NetworkRequestException e) {
			DebugLog.logException(e);
			if (UserAccount.getInstance().isCurrentUser(userHandle)) {
				return UserAccount.getInstance().getAccountDetails();
			} else {
				throw e;
			}
		}
	}

	private UserAccountView getUserAccount(IAccountService accountService, RequestType requestType) throws NetworkRequestException {
		GetUserAccountRequest userRequest = new GetUserAccountRequest();
		if (requestType == RequestType.SYNC_WITH_CACHE) {
			userRequest.forceCacheUsage();
		}
		GetUserAccountResponse userAccount = accountService.getUserAccount(userRequest);
		return userAccount.getUser();
	}

	private UserProfileView getUserProfile(IAccountService accountService, RequestType requestType) throws NetworkRequestException {
		GetUserProfileRequest profileRequest = new GetUserProfileRequest(userHandle);
		if (requestType == RequestType.SYNC_WITH_CACHE) {
			profileRequest.forceCacheUsage();
		}
		return accountService.getUserProfile(profileRequest).getUser();
	}
}
