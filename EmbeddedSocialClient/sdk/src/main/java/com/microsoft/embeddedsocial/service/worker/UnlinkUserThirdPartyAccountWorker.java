/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.worker;

import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.event.LinkUserThirdPartyAccountEvent;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.IAccountService;
import com.microsoft.embeddedsocial.server.exception.StatusException;
import com.microsoft.embeddedsocial.server.model.account.UnlinkUserThirdPartyAccountRequest;

import android.content.Context;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Sends an unlink user third party account request to the server.
 */
public class UnlinkUserThirdPartyAccountWorker extends Worker {
    public static final String IDENTITY_PROVIDER = "identityProvider";

    public UnlinkUserThirdPartyAccountWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public Result doWork() {
        final IdentityProvider accountType =
                IdentityProvider.fromValue(getInputData().getString(IDENTITY_PROVIDER));

        IAccountService accountService = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getAccountService();
        UnlinkUserThirdPartyAccountRequest unlinkUserThirdPartyAccountRequest
                = new UnlinkUserThirdPartyAccountRequest(accountType);

        try {
            accountService.unlinkUserThirdPartyAccount(unlinkUserThirdPartyAccountRequest);
            EventBus.post(LinkUserThirdPartyAccountEvent.createUnlinkEvent(accountType));
            return Result.success();
        } catch (Exception e) {
            DebugLog.logException(e);
            LinkUserThirdPartyAccountEvent event;
            // Notify the handler that the request failed
            if (e instanceof StatusException) {
                event = LinkUserThirdPartyAccountEvent.createUnlinkEvent(accountType, e.getMessage(),
                        ((StatusException) e).getStatusCode());
            } else {
                event = LinkUserThirdPartyAccountEvent.createUnlinkEvent(accountType, e.getMessage());
            }

            EventBus.post(event);
            return Result.failure();
        }
    }
}
