/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.microsoft.embeddedsocial.auth.GoogleNativeAuthenticator;
import com.microsoft.embeddedsocial.auth.SocialNetworkTokens;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;

import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.TokenResponse;

public class GoogleCallbackActivity extends SignInActivity {

    public static PendingIntent createPostAuthorizationIntent(@NonNull Context context,
                                                              @NonNull AuthorizationRequest request) {
        String action = context.getString(R.string.es_google_auth_response);
        Intent intent = new Intent(action);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, request.hashCode(), intent, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        checkIntent(intent);
    }

    private void checkIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null && action.equals(getString(R.string.es_google_auth_response))) {
                handleAuthorizationResponse(intent);
            } else {
                finish();
            }
        } else {
            finish();
        }
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

    public void getAccessToken(AuthorizationResponse authorizationResponse) {
        AuthorizationService service = new AuthorizationService(this);
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
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendAuthFailure() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(
                new Intent(GoogleNativeAuthenticator.GOOGLE_ACCOUNT_ACTION));
    }
}
