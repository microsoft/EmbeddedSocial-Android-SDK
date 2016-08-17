/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.service.handler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.actions.Action;
import com.microsoft.socialplus.actions.ActionsLauncher;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.data.model.AccountData;
import com.microsoft.socialplus.data.model.AccountDataDifference;
import com.microsoft.socialplus.data.model.CreateAccountData;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.IAccountService;
import com.microsoft.socialplus.server.IAuthenticationService;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UserRequest;
import com.microsoft.socialplus.server.model.account.CreateUserRequest;
import com.microsoft.socialplus.server.model.account.GetUserAccountRequest;
import com.microsoft.socialplus.server.model.account.GetUserAccountResponse;
import com.microsoft.socialplus.server.model.auth.AuthenticationResponse;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.service.ServiceAction;
import com.microsoft.socialplus.service.WorkerService;

import java.io.IOException;

/**
 * Sends a create account request to the server.
 */
public class CreateAccountHandler extends ActionHandler {

    private final IAccountService accountService = GlobalObjectRegistry
            .getObject(SocialPlusServiceProvider.class)
            .getAccountService();

    private final IAuthenticationService authenticationService = GlobalObjectRegistry
            .getObject(SocialPlusServiceProvider.class)
            .getAuthenticationService();

    private final Context context;

    public CreateAccountHandler(Context context) {
        this.context = context;
    }

    @Override
    protected void handleAction(Action action, ServiceAction serviceAction, Intent intent) {
        Bundle extras = intent.getExtras();
        CreateAccountData createAccountData = extras.getParcelable(IntentExtras.CREATE_ACCOUNT_DATA);

        CreateUserRequest createUserRequest = new CreateUserRequest.Builder()
                .setFirstName(createAccountData.getFirstName())
                .setLastName(createAccountData.getLastName())
                .setBio(createAccountData.getBio())
                .setIdentityProvider(createAccountData.getIdentityProvider())
                .setAccessToken(createAccountData.getThirdPartyAccessToken())
                .build();
        try {
            AuthenticationResponse createUserResponse = accountService.createUser(createUserRequest);
            handleSuccessfulResult(action, createUserResponse);

            uploadPhoto(createAccountData.getPhotoUri());
        } catch (IOException | NetworkRequestException e) {
            DebugLog.logException(e);
            action.fail();
        }
    }

    private void handleSuccessfulResult(Action action, AuthenticationResponse response)
            throws NetworkRequestException {

        String userHandle = response.getUserHandle();
        String sessionToken = UserRequest.createSessionAuthorization(response.getSessionToken());
        GetUserAccountRequest getUserRequest = new GetUserAccountRequest(sessionToken);
        GetUserAccountResponse userAccount = accountService.getUserAccount(getUserRequest);
        AccountData accountData = AccountData.fromServerResponse(userAccount.getUser());
        if (!action.isCompleted()) {
            int messageId = R.string.sp_msg_general_create_user_success;
            UserAccount.getInstance().onSignedIn(userHandle, sessionToken, accountData, messageId);
            WorkerService.getLauncher(context).launchService(ServiceAction.GCM_REGISTER);
        }
    }

    /**
     * Uploads the profile photo
     */
    private void uploadPhoto(Uri photoUri) throws IOException, NetworkRequestException {
        // TODO this is a separate call which could fail and leave the wrong public access
        if (photoUri != null) {
            AccountDataDifference difference = new AccountDataDifference();
            difference.setNewPhoto(photoUri);
            ActionsLauncher.updateAccount(context, difference);
        }
    }

    @Override
    public void dispose() {

    }
}
