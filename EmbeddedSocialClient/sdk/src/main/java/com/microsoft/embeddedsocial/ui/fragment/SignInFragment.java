/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.auth.AbstractAuthenticator;
import com.microsoft.embeddedsocial.auth.GoogleNativeAuthenticator;
import com.microsoft.embeddedsocial.auth.MicrosoftLiveAuthenticator;
import com.microsoft.embeddedsocial.auth.TwitterWebAuthenticator;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.event.signin.SignInWithThirdPartyFailedEvent;
import com.microsoft.embeddedsocial.event.signin.UserSignedInEvent;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.fragment.module.SlowConnectionMessageModule;
import com.microsoft.embeddedsocial.actions.Action;
import com.microsoft.embeddedsocial.auth.FacebookAuthenticator;
import com.microsoft.embeddedsocial.auth.IAuthenticationCallback;
import com.microsoft.embeddedsocial.base.utils.thread.ThreadUtils;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragment;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;
import com.microsoft.embeddedsocial.ui.util.WebPageHelper;
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
		R.string.es_cancel,
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
		return R.layout.es_fragment_signin;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		progressView = view.findViewById(R.id.es_progress);
		buttonsView = view.findViewById(R.id.es_buttons);
		Options options = GlobalObjectRegistry.getObject(Options.class);
		setupSignInButton(view, R.id.es_signInFacebook, v -> signInWithFacebook(), options.isFacebookLoginEnabled());
		setupSignInButton(view, R.id.es_signInMicrosoft, v -> signInWithMicrosoft(), options.isMicrosoftLoginEnabled());
		setupSignInButton(view, R.id.es_signInGoogle, v -> signInWithGoogle(), options.isGoogleLoginEnabled());
		setupSignInButton(view, R.id.es_signInTwitter, v -> signInWithTwitter(), options.isTwitterLoginEnabled());

		Resources res = getResources();
		String privacyPolicy = res.getString(R.string.es_privacy_statement);
		String termsOfUse = res.getString(R.string.es_terms_of_use);
		String termsText = res.getString(R.string.es_terms, privacyPolicy, termsOfUse);

		int privacyIndexStart = termsText.indexOf(privacyPolicy);
		int privacyIndexEnd = privacyIndexStart + privacyPolicy.length();
		int termsIndexStart = termsText.indexOf(termsOfUse);
		int termsIndexEnd = termsIndexStart + termsOfUse.length();

		SpannableString spannableText = new SpannableString(termsText);

		spannableText.setSpan(new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				WebPageHelper.openPrivacyPolicy(getContext());
			}
		}, privacyIndexStart, privacyIndexEnd, 0);

		spannableText.setSpan(new ClickableSpan() {
			@Override
			public void onClick(View widget) {
				WebPageHelper.openTermsAndConditions(getContext());
			}
		}, termsIndexStart, termsIndexEnd, 0);

		TextView termsView = (TextView)view.findViewById(R.id.es_policyText);
		termsView.setText(spannableText);
		termsView.setMovementMethod(LinkMovementMethod.getInstance());
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
		Toast.makeText(getActivity(), R.string.es_msg_general_signin_error, Toast.LENGTH_LONG).show();
	}

	private void signInWithFacebook() {
		startAuthentication(new FacebookAuthenticator(this, this,
				FacebookAuthenticator.AuthenticationMode.SIGN_IN_ONLY));
	}

	private void signInWithGoogle() {
		startAuthentication(new GoogleNativeAuthenticator(this, this,
				GoogleNativeAuthenticator.AuthenticationMode.SIGN_IN_ONLY));
	}

	private void signInWithTwitter() {
		startAuthentication(new TwitterWebAuthenticator(this, this));
	}

	private void signInWithMicrosoft() {
		startAuthentication(new MicrosoftLiveAuthenticator(this, this,
				MicrosoftLiveAuthenticator.AuthenticationMode.SIGN_IN_ONLY));
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
