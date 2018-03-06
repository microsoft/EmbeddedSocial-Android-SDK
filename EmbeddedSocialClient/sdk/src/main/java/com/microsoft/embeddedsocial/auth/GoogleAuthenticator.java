/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.auth;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;

import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;

import android.content.Intent;
import android.support.v4.app.Fragment;

import static com.microsoft.embeddedsocial.auth.AuthUtils.hashString;

/**
 * Implements Google authentication process using GMS sign in API.
 */
public class GoogleAuthenticator extends AbstractAuthenticator {
    public static final int RC_SIGN_IN = 100;
    private final AuthenticationMode authMode;


    private GoogleSignInClient signInClient;

    public GoogleAuthenticator(Fragment fragment, IAuthenticationCallback authCallback,
                               GoogleAuthenticator.AuthenticationMode authMode) {
        super(IdentityProvider.GOOGLE, fragment, authCallback);
        Options options = GlobalObjectRegistry.getObject(Options.class);
        // ensure the client id provided in the config is the web client in the API console
        String clientId = options.getGoogleClientId();
        // TODO use authmode
        this.authMode = authMode;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestIdToken(clientId)
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(fragment.getContext(), gso);


    }

    @Override
    public void onAuthenticationStarted() throws AuthenticationException {
        Intent signInIntent = signInClient.getSignInIntent();
        getFragment().startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GoogleAuthenticator.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            return true;
        }
        return false;
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount gsa = task.getResult(ApiException.class);
            String idToken = gsa.getIdToken();
            if (authMode.canStoreToken()) {
                SocialNetworkTokens.google().storeToken(idToken);
            }
            SocialNetworkAccount account = new SocialNetworkAccount(IdentityProvider.GOOGLE,
                    idToken, gsa.getGivenName(), gsa.getFamilyName());
            account.setEmail(hashString(gsa.getEmail()));

            onAuthenticationSuccess(account);
        } catch (ApiException e) {
            DebugLog.logException(e);
            onAuthenticationError(getFragment().getString(R.string.es_msg_google_signin_failed));
        }
    }

    /**
     * Google authentication mode.
     */
    public enum AuthenticationMode {

        /**
         * Allow sign-in only.
         */
        SIGN_IN_ONLY(false, new Scope(Scopes.PROFILE)),

        /**
         * Allow sign-in and obtaining friend list.
         */
        OBTAIN_FRIENDS(true, new Scope(Scopes.PROFILE), new Scope(Scopes.EMAIL));

        private final Scope[] permissions;
        private final boolean allowStoringToken;

        AuthenticationMode(boolean allowStoringToken, Scope... permissions) {
            this.permissions = permissions;
            this.allowStoringToken = allowStoringToken;
        }

        private Scope[] getPermissions() {
            return permissions;
        }

        private boolean canStoreToken() {
            return allowStoringToken;
        }
    }
}
