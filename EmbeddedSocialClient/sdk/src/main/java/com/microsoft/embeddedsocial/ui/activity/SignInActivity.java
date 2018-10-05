/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import com.microsoft.embeddedsocial.auth.AbstractAuthenticator;
import com.microsoft.embeddedsocial.auth.GoogleAppAuthAuthenticator;
import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.base.utils.thread.ThreadUtils;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.fragment.SignInFragment;

import android.content.Intent;

/**
 * Activity for sign-in.
 */
public class SignInActivity extends BaseActivity {
    public static final String NAME = "SignIn";

    private SignInFragment signInFragment;

    public SignInActivity() {
        super(R.id.es_navigationProfile);
        signInFragment = new SignInFragment();
    }

    @Override
    protected void setupFragments() {
        setActivityContent(signInFragment);
    }

    @Override
    protected boolean isAuthorizationRequired() {
        return false;
    }

    @Override
    protected String getName() {
        return NAME;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // XXX: keyboard is closed in a handler because of bugs on HTC devices
        ThreadUtils.getMainThreadHandler().post(() -> ViewUtils.hideKeyboard(this));
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
                signInFragment.setIsGettingThirdPartyCredentials(true);
                AbstractAuthenticator authenticator = signInFragment.getAuthenticator();
                if (authenticator instanceof GoogleAppAuthAuthenticator) {
                    ((GoogleAppAuthAuthenticator)authenticator).handleAuthorizationResponse(intent);
                }
            }
        }
    }
}
