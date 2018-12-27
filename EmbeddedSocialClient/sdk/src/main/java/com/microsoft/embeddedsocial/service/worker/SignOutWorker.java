/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.worker;

import com.microsoft.embeddedsocial.auth.MicrosoftLiveAuthenticator;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.IAuthenticationService;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.auth.SignOutRequest;

import android.content.Context;
import android.os.Build;
import android.webkit.CookieManager;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Performs the sign out operation
 */
public class SignOutWorker extends Worker {
    public static final String AUTHORIZATION = "authorization";

    Context context;

    public SignOutWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @Override
    public Result doWork() {
        String authorization = getInputData().getString(AUTHORIZATION);
        IAuthenticationService server = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getAuthenticationService();
        try {
            SignOutRequest request = new SignOutRequest(authorization);
            server.signOut(request);
        } catch (NetworkRequestException e) {
            // ignore server errors
            DebugLog.logException(e);
        }
        MicrosoftLiveAuthenticator.signOut(context);
        clearCookies();
        return Result.success();
    }

    private void clearCookies() {
        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
        } else {
            //noinspection deprecation
            cookieManager.removeAllCookie();
        }
    }
}
