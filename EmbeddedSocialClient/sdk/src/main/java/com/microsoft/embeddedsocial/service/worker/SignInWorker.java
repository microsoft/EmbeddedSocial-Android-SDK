/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.worker;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.IAccountService;
import com.microsoft.embeddedsocial.server.IAuthenticationService;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.exception.NotFoundException;
import com.microsoft.embeddedsocial.server.model.UserRequest;
import com.microsoft.embeddedsocial.server.model.account.GetMyProfileRequest;
import com.microsoft.embeddedsocial.server.model.account.GetUserAccountRequest;
import com.microsoft.embeddedsocial.server.model.account.GetUserAccountResponse;
import com.microsoft.embeddedsocial.server.model.account.GetUserProfileResponse;
import com.microsoft.embeddedsocial.server.model.auth.AuthenticationResponse;
import com.microsoft.embeddedsocial.server.model.auth.CreateSessionRequest;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.activity.CreateProfileActivity;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Sends sign in requests.  Launches the create user page if necessary.
 */
public class SignInWorker extends Worker {
    public static final String SOCIAL_NETWORK_ACCOUNT = "thirdPartyAccount";
    public static final String TAG = "signInWorker";

    private final Context context;

    private final IAccountService accountService = GlobalObjectRegistry
            .getObject(EmbeddedSocialServiceProvider.class)
            .getAccountService();

    private final IAuthenticationService authenticationService = GlobalObjectRegistry
            .getObject(EmbeddedSocialServiceProvider.class)
            .getAuthenticationService();

    public SignInWorker(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
        this.context = context;
    }

    @Override
    public Result doWork() {
        String serializedNetworkAccount = getInputData().getString(SOCIAL_NETWORK_ACCOUNT);
        if (serializedNetworkAccount == null) {
            return Result.failure();
        }
        InputStream inputStream = new ByteArrayInputStream(
                Base64.decode(serializedNetworkAccount, Base64.DEFAULT));
        try {
            ObjectInputStream objectStream = new ObjectInputStream(inputStream);
            SocialNetworkAccount socialNetworkAccount = (SocialNetworkAccount)objectStream.readObject();
            CreateSessionRequest signInWithThirdPartyRequest = new CreateSessionRequest(
                    socialNetworkAccount.getAccountType(),
                    socialNetworkAccount.getThirdPartyAccessToken(),
                    socialNetworkAccount.getThirdPartyRequestToken());

            try {
                String authorization = signInWithThirdPartyRequest.getAuthorization();
                GetMyProfileRequest getMyProfileRequest = new GetMyProfileRequest(authorization);

                // Determine the user's user handle
                GetUserProfileResponse getUserProfileResponse = getMyProfileRequest.send();

                // set the user handle and attempt sign in
                signInWithThirdPartyRequest.setRequestUserHandle(getUserProfileResponse.getUser().getHandle());
                AuthenticationResponse signInResponse = authenticationService.signInWithThirdParty(signInWithThirdPartyRequest);
                handleSuccessfulResult(signInResponse);
            } catch (NotFoundException e) {
                // User handle not found; create an account
                Intent i = new Intent(context, CreateProfileActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle extras = new Bundle();
                extras.putParcelable(IntentExtras.THIRD_PARTY_ACCOUNT, socialNetworkAccount);
                i.putExtras(extras);
                context.startActivity(i);
            } finally {
                socialNetworkAccount.clearTokens();
            }
        } catch (Exception e) {
            DebugLog.logException(e);
            UserAccount.getInstance().onSignInWithThirdPartyFailed();
            return Result.failure();
        }

        return Result.success();
    }

    private void handleSuccessfulResult(AuthenticationResponse response)
            throws NetworkRequestException {

        String userHandle = response.getUserHandle();
        String sessionToken = UserRequest.createSessionAuthorization(response.getSessionToken());
        GetUserAccountRequest getUserRequest = new GetUserAccountRequest(sessionToken);
        GetUserAccountResponse userAccount = accountService.getUserAccount(getUserRequest);
        AccountData accountData = AccountData.fromServerResponse(userAccount.getUser());
        int messageId = R.string.es_msg_general_signin_success;
        UserAccount.getInstance().onSignedIn(userHandle, sessionToken, accountData, messageId);
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(GetFcmIdWorker.class).build();
        WorkManager.getInstance().enqueue(workRequest);
    }
}
