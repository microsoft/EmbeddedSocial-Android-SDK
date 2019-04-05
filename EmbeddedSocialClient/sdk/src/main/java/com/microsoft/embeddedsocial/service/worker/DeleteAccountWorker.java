/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.worker;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.IAccountService;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.account.DeleteUserRequest;

import android.content.Context;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Sends a delete account request to the server
 */
public class DeleteAccountWorker extends Worker {
    public static String TAG = "deleteAccountWorker";

    public DeleteAccountWorker(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
    }

    @Override
    public Result doWork() {
        IAccountService server = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getAccountService();
        try {
            server.deleteUser(new DeleteUserRequest());
            UserAccount.getInstance().signOutOfDevice();
        } catch (NetworkRequestException e) {
            DebugLog.logException(e);
            return Result.failure();
        }

        return Result.success();
    }
}
