/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.worker;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.data.model.AccountDataDifference;
import com.microsoft.embeddedsocial.data.model.CreateAccountData;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.IAccountService;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.UserRequest;
import com.microsoft.embeddedsocial.server.model.account.CreateUserRequest;
import com.microsoft.embeddedsocial.server.model.account.GetUserAccountRequest;
import com.microsoft.embeddedsocial.server.model.account.GetUserAccountResponse;
import com.microsoft.embeddedsocial.server.model.auth.AuthenticationResponse;

import android.content.Context;
import android.net.Uri;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class CreateAccountWorker extends Worker {
    public static final String CREATE_ACCOUNT_DATA = "createAccountData";

    private final Context context;

    private final IAccountService accountService = GlobalObjectRegistry
            .getObject(EmbeddedSocialServiceProvider.class)
            .getAccountService();

    public CreateAccountWorker(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
        this.context = context;
    }

    @Override
    public Result doWork() {
        String serializedAccountData = getInputData().getString(CREATE_ACCOUNT_DATA);
        CreateAccountData createAccountData = WorkerSerializationHelper.deserialize(serializedAccountData);

        if (serializedAccountData == null) {
            UserAccount.getInstance().onCreateUserFailed();
            return Result.failure();
        }

        try {
            CreateUserRequest createUserRequest = new CreateUserRequest.Builder()
                    .setFirstName(createAccountData.getFirstName())
                    .setLastName(createAccountData.getLastName())
                    .setBio(createAccountData.getBio())
                    .setIdentityProvider(createAccountData.getIdentityProvider())
                    .setAccessToken(createAccountData.getThirdPartyAccessToken())
                    .setRequestToken(createAccountData.getThirdPartyRequestToken())
                    .build();

            AuthenticationResponse createUserResponse = accountService.createUser(createUserRequest);
            handleSuccessfulResult(createUserResponse);
            uploadPhoto(createAccountData.getPhotoUri());
        } catch (NetworkRequestException e) {
            DebugLog.logException(e);
            UserAccount.getInstance().onCreateUserFailed();
            return Result.failure();
        }

        return Result.success();
    }

    private void handleSuccessfulResult(AuthenticationResponse response) throws NetworkRequestException {
        String userHandle = response.getUserHandle();
        String sessionToken = UserRequest.createSessionAuthorization(response.getSessionToken());
        GetUserAccountRequest getUserRequest = new GetUserAccountRequest(sessionToken);
        GetUserAccountResponse userAccount = accountService.getUserAccount(getUserRequest);
        AccountData accountData = AccountData.fromServerResponse(userAccount.getUser());
        int messageId = R.string.es_msg_general_create_user_success;
        UserAccount.getInstance().onSignedIn(userHandle, sessionToken, accountData, messageId);
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(GetFcmIdWorker.class).build();
        WorkManager.getInstance().enqueue(workRequest);
    }

    /**
     * Uploads the profile photo
     */
    private void uploadPhoto(Uri photoUri) {
        // TODO this is a separate call which could fail and leave the wrong public access
        if (photoUri != null) {
            AccountDataDifference difference = new AccountDataDifference();
            difference.setNewPhoto(photoUri);

            Data inputData = new Data.Builder()
                    .putString(UpdateAccountWorker.ACCOUNT_DATA_DIFFERENCE,
                            WorkerSerializationHelper.serialize(difference)).build();
            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(UpdateAccountWorker.class)
                    .setInputData(inputData).build();
            WorkManager.getInstance().enqueue(workRequest);
        }
    }
}
