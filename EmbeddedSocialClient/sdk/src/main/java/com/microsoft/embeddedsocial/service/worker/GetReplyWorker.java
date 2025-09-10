/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.worker;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.event.content.GetReplyEvent;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.IContentService;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyRequest;
import com.microsoft.embeddedsocial.server.model.content.replies.GetReplyResponse;

import android.content.Context;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Fetches a reply from the server
 */
public class GetReplyWorker extends Worker {
    public static final String REPLY_HANDLE = "replyHandle";

    public GetReplyWorker(Context context, WorkerParameters workerParameters) {
        super(context, workerParameters);
    }

    @Override
    public Result doWork() {
        IContentService contentService
                = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getContentService();

        final String replyHandle = getInputData().getString(REPLY_HANDLE);

        try {
            final GetReplyRequest request = new GetReplyRequest(replyHandle);
            GetReplyResponse response = contentService.getReply(request);
            EventBus.post(new GetReplyEvent(response.getReply(), response.getReply() != null));
        } catch (NetworkRequestException e) {
            DebugLog.logException(e);
            EventBus.post(new GetReplyEvent(null, false));
            return Result.failure();
        }

        return Result.success();
    }
}
