/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Toast;

import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.actions.Action;
import com.microsoft.socialplus.auth.AbstractAuthenticator;
import com.microsoft.socialplus.auth.FacebookAuthenticator;
import com.microsoft.socialplus.auth.GoogleWebAuthenticator;
import com.microsoft.socialplus.auth.IAuthenticationCallback;
import com.microsoft.socialplus.auth.MicrosoftLiveAuthenticator;
import com.microsoft.socialplus.auth.TwitterWebAuthenticator;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.base.utils.thread.ThreadUtils;
import com.microsoft.socialplus.event.signin.SignInWithThirdPartyFailedEvent;
import com.microsoft.socialplus.event.signin.UserSignedInEvent;
import com.microsoft.socialplus.sdk.Options;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.fragment.base.BaseFragment;
import com.microsoft.socialplus.ui.fragment.module.SlowConnectionMessageModule;
import com.microsoft.socialplus.ui.util.SocialNetworkAccount;
import com.squareup.otto.Subscribe;

/**
 * Sign-in fragment.
 */
public class SignInFragment extends BaseFragment implements IAuthenticationCallback {

	private static final long SIGN_IN_TIMEOUT = 15 * DateUtils.SECOND_IN_MILLIS;

	private View progressView;
	private View buttonsView;
	private AbstractAuthenticator authenticator;

	private Action signInAction;
	private final SlowConnectionMessageModule slowConnectionMessageModule = new SlowConnectionMessageModule(
		this,
		R.string.sp_cancel,
		SIGN_IN_TIMEOUT,
		() -> {
			if (signInAction != null) {
				signInAction.fail();
				setProgressVisible(false);
			}
		}
	);

	public SignInFragment() {
		addModule(slowConnectionMessageModule);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.sp_fragment_signin;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		progressView = view.findViewById(R.id.sp_progress);
		buttonsView = view.findViewById(R.id.sp_buttons);
		Options options = GlobalObjectRegistry.getObject(Options.class);
		setupSignInButton(view, R.id.sp_signInFacebook, v -> signInWithFacebook(), options.isFacebookLoginEnabled());
		setupSignInButton(view, R.id.sp_signInMicrosoft, v -> signInWithMicrosoft(), options.isMicrosoftLoginEnabled());
		setupSignInButton(view, R.id.sp_signInGoogle, v -> signInWithGoogle(), options.isGoogleLoginEnabled());
		setupSignInButton(view, R.id.sp_signInTwitter, v -> signInWithTwitter(), options.isTwitterLoginEnabled());


	}

	private void setupSignInButton(View root, @IdRes int viewId, View.OnClickListener onClickListener, boolean visible) {
		View view = root.findViewById(viewId);
		if (visible) {
			view.setOnClickListener(onClickListener);
		} else {
			view.setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (UserAccount.getInstance().isSignedIn()) {
			onSignedIn();
		} else {
			setProgressVisible(UserAccount.getInstance().isSigningIn());
		}
	}

	@SuppressWarnings("unused")
	@Subscribe
	public void onSignedIn(UserSignedInEvent event) {
		Toast.makeText(getActivity(), event.getMessageId(), Toast.LENGTH_LONG).show();
		onSignedIn();
	}

	private void onSignedIn() {
		Activity activity = getActivity();
		Intent data = new Intent();
		data.putExtras(activity.getIntent());
		activity.setResult(Activity.RESULT_OK, data);
		finishActivity();
	}

	@Subscribe
	public void onSignInWithThirdPartyFailed(SignInWithThirdPartyFailedEvent event) {
		onSignInFinished();
		Toast.makeText(getActivity(), R.string.sp_msg_general_signin_error, Toast.LENGTH_LONG).show();
	}

	private void signInWithFacebook() {
		startAuthentication(new FacebookAuthenticator(this, this,
			FacebookAuthenticator.AuthenticationMode.SIGN_IN_ONLY));
	}

	private void signInWithGoogle() {
		startAuthentication(new GoogleWebAuthenticator(this, this));
	}

	private void signInWithTwitter() {
		startAuthentication(new TwitterWebAuthenticator(this, this));
	}

	private void signInWithMicrosoft() {
		startAuthentication(new MicrosoftLiveAuthenticator(this, this));
	}

	private void startAuthentication(AbstractAuthenticator authenticator) {
		this.authenticator = authenticator;
		setProgressVisible(true);
		authenticator.startAuthenticationAsync();
	}

	private void setProgressVisible(boolean progressVisible) {
		if (progressVisible) {
			hideKeyboard();
		}
		progressView.setVisibility(progressVisible ? View.VISIBLE : View.INVISIBLE);
		buttonsView.setVisibility(progressVisible ? View.INVISIBLE : View.VISIBLE);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		progressView = null;
		buttonsView = null;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (authenticator != null) {
			authenticator.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void onSignInStarted() {
		slowConnectionMessageModule.onOperationStarted();
		setProgressVisible(true);
	}

	private void onSignInFinished() {
		setProgressVisible(false);
		slowConnectionMessageModule.onOperationFinished();
	}

	private void clearAuthenticator() {
		if (authenticator != null) {
			authenticator.dispose();
		}
		authenticator = null;
	}

	@Override
	public void onAuthenticationSuccess(SocialNetworkAccount account) {
		clearAuthenticator();
		ThreadUtils.runOnMainThread(() -> {
			onSignInStarted();
			signInAction = UserAccount.getInstance().signInUsingThirdParty(account);
		});
	}

	@Override
	public void onAuthenticationError(String errorMessage) {
		clearAuthenticator();
		DebugLog.i(errorMessage);
		ThreadUtils.runOnMainThread(() -> {
			onSignInFinished();
			Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
		});
	}
}
