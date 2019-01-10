/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.worker;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.autorest.models.ImageType;
import com.microsoft.embeddedsocial.autorest.models.Visibility;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.data.model.AccountDataDifference;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.IAccountService;
import com.microsoft.embeddedsocial.server.ImageUploader;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.account.UpdateUserPhotoRequest;
import com.microsoft.embeddedsocial.server.model.account.UpdateUserPublicAccountInfoRequest;
import com.microsoft.embeddedsocial.server.model.account.UpdateUserVisibilityRequest;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UpdateAccountWorker extends Worker {
    public static final String ACCOUNT_DATA_DIFFERENCE = "accountDataDifference";

    private final Context context;
    private final IAccountService server = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getAccountService();

    public UpdateAccountWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @Override
    public Result doWork() {
        String serializedData = getInputData().getString(ACCOUNT_DATA_DIFFERENCE);
        AccountDataDifference difference = WorkerSerializationHelper.deserialize(serializedData);
        AccountData accountData = UserAccount.getInstance().getAccountDetails();

        try {
            updatePhotoIfNeeded(difference, accountData);
            updatePublicInfoIfNeeded(difference, accountData);
            updatePrivacyIfNeeded(difference, accountData);
        } catch (IOException | NetworkRequestException e) {
            DebugLog.logException(e);
            return Result.failure();
        } finally {
            UserAccount.getInstance().updateAccountDetails(accountData);
        }

        return Result.success();
    }

    private void updatePrivacyIfNeeded(AccountDataDifference difference, AccountData accountData) throws NetworkRequestException {
        if (difference.isPrivacyChanged()) {
            Visibility visibility = difference.isPrivate() ? Visibility.PRIVATE : Visibility.PUBLIC;
            server.updateUserVisibility(new UpdateUserVisibilityRequest(visibility));
            accountData.setIsPrivate(difference.isPrivate());
        }
    }

    private void updatePublicInfoIfNeeded(AccountDataDifference difference, AccountData accountData) throws NetworkRequestException {
        if (difference.isPublicInfoChanged()) {
            server.updateUserPublicAccountInfo(new UpdateUserPublicAccountInfoRequest(
                    difference.getFirstName(),
                    difference.getLastName(),
                    difference.getBio()));
            accountData.setFirstName(difference.getFirstName());
            accountData.setLastName(difference.getLastName());
            accountData.setBio(difference.getBio());
        }
    }

    private void updatePhotoIfNeeded(AccountDataDifference difference, AccountData accountData) throws IOException, NetworkRequestException {
        if (difference.isPhotoUriChanged()) {
            Uri photoUri = difference.getPhotoUri();
            String photoUrl = photoUri != null ? ImageUploader.uploadImage(context, photoUri, ImageType.USERPHOTO) : null;
            server.updateUserPhoto(new UpdateUserPhotoRequest(photoUrl));
            accountData.setUserPhotoUrl(photoUrl);
        }
    }
}
