/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.worker;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.IRelationshipService;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.relationship.RemoveFollowerRequest;

import android.content.Context;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Sends remove follower requests to the server
 */
public class RemoveFollowerWorker extends Worker {
    public static final String USER_HANDLE = "userHandle";

    private final IRelationshipService relationshipService = GlobalObjectRegistry
            .getObject(EmbeddedSocialServiceProvider.class)
            .getRelationshipService();

    public RemoveFollowerWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {
        String userHandle = getInputData().getString(USER_HANDLE);

        RemoveFollowerRequest removeFollowerRequest = new RemoveFollowerRequest(userHandle);
        try {
            relationshipService.removeFollower(removeFollowerRequest);
        } catch (NetworkRequestException e) {
            DebugLog.logException(e);
            return Result.failure();
        }
        return Result.success();
    }
}
