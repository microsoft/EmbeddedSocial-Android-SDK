/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.worker;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.event.content.GetCommentEvent;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.IContentService;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentRequest;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentResponse;

import android.content.Context;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Fetches a comment from the server
 */
public class GetCommentWorker extends Worker {
    public static final String COMMENT_HANDLE = "commentHandle";

    public GetCommentWorker(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
    }

    @Override
    public Result doWork() {
        IContentService contentService
                = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getContentService();

        final String commentHandle = getInputData().getString(COMMENT_HANDLE);

        try {
            final GetCommentRequest request = new GetCommentRequest(commentHandle);
            GetCommentResponse response = contentService.getComment(request);
            EventBus.post(new GetCommentEvent(response.getComment(), response.getComment() != null));
        } catch (NetworkRequestException e) {
            DebugLog.logException(e);
            EventBus.post(new GetCommentEvent(null, false));
            return Result.failure();
        }

        return Result.success();
    }
}
