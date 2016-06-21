/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.service.handler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.actions.Action;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.data.model.AccountData;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.IAccountService;
import com.microsoft.socialplus.server.IAuthenticationService;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.exception.NotFoundException;
import com.microsoft.socialplus.server.model.account.GetUserAccountRequest;
import com.microsoft.socialplus.server.model.account.GetUserAccountResponse;
import com.microsoft.socialplus.server.model.auth.AuthenticationResponse;
import com.microsoft.socialplus.server.model.auth.SignInWithThirdPartyRequest;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.service.ServiceAction;
import com.microsoft.socialplus.service.WorkerService;
import com.microsoft.socialplus.ui.activity.CreateProfileActivity;
import com.microsoft.socialplus.ui.util.SocialNetworkAccount;

/**
 * Sends sign-in requests.
 */
public class SignInHandler extends ActionHandler {

	private final IAccountService accountService = GlobalObjectRegistry
			.getObject(SocialPlusServiceProvider.class)
			.getAccountService();

	private final IAuthenticationService authenticationService = GlobalObjectRegistry
			.getObject(SocialPlusServiceProvider.class)
			.getAuthenticationService();

	private final Context context;

	public SignInHandler(Context context) {
		this.context = context;
	}

	@Override
	protected void handleAction(Action action, ServiceAction serviceAction, Intent intent) {
		signinWithThirdParty(action, intent.getParcelableExtra(IntentExtras.THIRD_PARTY_ACCOUNT));
	}

	private void signinWithThirdParty(Action action, SocialNetworkAccount thirdPartyAccount) {
		SignInWithThirdPartyRequest signInWithThirdPartyRequest = new SignInWithThirdPartyRequest(
				thirdPartyAccount.getAccountType(),
				thirdPartyAccount.getThirdPartyAccessToken());
		try {
			AuthenticationResponse signInResponse = authenticationService.signInWithThirdParty(signInWithThirdPartyRequest);
			handleSuccessfulResult(action, signInResponse);
		} catch (NotFoundException e) {
			Intent i = new Intent(context, CreateProfileActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Bundle extras = new Bundle();
			extras.putParcelable(IntentExtras.THIRD_PARTY_ACCOUNT, thirdPartyAccount);
			i.putExtras(extras);
			context.startActivity(i);
		} catch (Exception e) {
			DebugLog.logException(e);
			UserAccount.getInstance().onSignInWithThirdPartyFailed(thirdPartyAccount);
		}
	}

	private void handleSuccessfulResult(Action action, AuthenticationResponse response)
			throws NetworkRequestException {

		String userHandle = response.getUserHandle();
		String sessionToken = "Bearer " + response.getSessionToken();
		GetUserAccountRequest getUserRequest = new GetUserAccountRequest(sessionToken);
		GetUserAccountResponse userAccount = accountService.getUserAccount(getUserRequest);
		AccountData accountData = AccountData.fromServerResponse(userAccount.getUser());
		if (!action.isCompleted()) {
			int messageId = R.string.sp_msg_general_signin_success;
			UserAccount.getInstance().onSignedIn(userHandle, sessionToken, accountData, messageId);
			WorkerService.getLauncher(context).launchService(ServiceAction.GCM_REGISTER);
		}
	}

	@Override
	public void dispose() {

	}
}
