/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.auth;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

/**
 * Handles authorization responses from Google and retrieves the user access token
 */
public class GoogleResponseHandler {

    private Context context;

    public GoogleResponseHandler(Context context) {
        this.context = context;
    }

    public static PendingIntent createPostAuthorizationIntent(@NonNull Context context,
                                                              @NonNull AuthorizationRequest request) {
        String action = context.getString(R.string.es_google_auth_response);
        Intent intent = new Intent(action);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, request.hashCode(), intent, 0);
    }

    public void handleAuthorizationResponse(Intent intent) {
        AuthorizationResponse resp = AuthorizationResponse.fromIntent(intent);
        AuthorizationException ex = AuthorizationException.fromIntent(intent);
        if (resp != null) {
            getAccessToken(resp);
        } else {
            DebugLog.logException(ex);
            sendAuthFailure();
        }
    }

    private void getAccessToken(AuthorizationResponse authorizationResponse) {
        AuthorizationService service = new AuthorizationService(context);
        service.performTokenRequest(
                authorizationResponse.createTokenExchangeRequest(),
                new AuthorizationService.TokenResponseCallback() {
                    @Override public void onTokenRequestCompleted(
                            TokenResponse resp, AuthorizationException ex) {
                        if (ex == null && resp != null) {
                            SocialNetworkAccount account = new SocialNetworkAccount(
                                    IdentityProvider.GOOGLE, resp.accessToken);
                            sendAuthSuccess(account);
                        } else {
                            DebugLog.logException(ex);
                            sendAuthFailure();
                        }
                    }
                });
        service.dispose();
    }

    private void sendAuthSuccess(SocialNetworkAccount account) {
        Intent intent = new Intent(GoogleNativeAuthenticator.GOOGLE_ACCOUNT_ACTION);
        intent.putExtra(GoogleNativeAuthenticator.GOOGLE_ACCOUNT, account);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void sendAuthFailure() {
        LocalBroadcastManager.getInstance(context).sendBroadcast(
                new Intent(GoogleNativeAuthenticator.GOOGLE_ACCOUNT_ACTION));
    }
}
