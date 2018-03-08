/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.auth;

import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;

import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.TokenResponse;

import org.json.JSONObject;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.microsoft.embeddedsocial.auth.AuthUtils.hashString;

/**
 * Implements Google authentication process using AppAuth.
 */
public class GoogleAppAuthAuthenticator extends AbstractAuthenticator {
	private static final Uri ISSUER_URI = Uri.parse("https://accounts.google.com");

	private final AuthenticationMode authMode;
	private final Options options;
	private AuthorizationService service;
	private Context context;

	public GoogleAppAuthAuthenticator(Fragment fragment, IAuthenticationCallback authCallback,
									  AuthenticationMode authMode) {
		super(IdentityProvider.GOOGLE, fragment, authCallback);

		context = getFragment().getContext();
		service = new AuthorizationService(context);
		options = GlobalObjectRegistry.getObject(Options.class);
		this.authMode = authMode;
	}

	@Override
	protected void onAuthenticationStarted() throws AuthenticationException {
		AuthorizationServiceConfiguration.fetchFromIssuer(
				ISSUER_URI,
				(@Nullable AuthorizationServiceConfiguration serviceConfiguration,
							@Nullable AuthorizationException ex) -> {
						if (ex != null) {
							DebugLog.logException(ex);
							service.dispose();
							onAuthenticationError(getFragment().getString(R.string.es_msg_google_signin_failed));
						} else {
							// service configuration retrieved, proceed to authorization...'
							sendAuthRequest(serviceConfiguration);
						}
				});
	}

	private void sendAuthRequest(AuthorizationServiceConfiguration serviceConfiguration) {
		// ensure the client id provided in the config is the android client in the API console
		String clientId = options.getGoogleClientId();
		String authRedirect = String.format("%s:/oauth2redirect", context.getPackageName());

		Uri redirectUri = Uri.parse(authRedirect);

		AuthorizationRequest request = new AuthorizationRequest.Builder(
				serviceConfiguration,
				clientId,
				ResponseTypeValues.CODE,
				redirectUri)
				.setScopes(authMode.getPermissions())
				.build();

		PendingIntent pendingIntent = createPostAuthorizationIntent(context, request);
		service.performAuthorizationRequest(request, pendingIntent);
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
			onAuthenticationError(getFragment().getString(R.string.es_msg_google_signin_failed));
		}
	}

	private void getAccessToken(AuthorizationResponse authorizationResponse) {
		service.performTokenRequest(
				authorizationResponse.createTokenExchangeRequest(),
				new AuthorizationService.TokenResponseCallback() {
					@Override public void onTokenRequestCompleted(
							TokenResponse resp, AuthorizationException ex) {
						if (ex == null && resp != null) {
							AuthState authState = new AuthState(authorizationResponse, resp, ex);
							onTokenRequestSuccess(authState);
						} else {
							DebugLog.logException(ex);
							onAuthenticationError(getFragment().getString(R.string.es_msg_google_signin_failed));
						}
					}
				});
	}

	/**
	 * Fetch user information using the provided tokens
	 * @param authState Current auth state after successful authentication
	 */
	public void onTokenRequestSuccess(AuthState authState) {
		authState.performActionWithFreshTokens(service, new AuthState.AuthStateAction() {
			@Override
			public void execute(@Nullable String accessToken, @Nullable String idToken, @Nullable AuthorizationException exception) {
				new AsyncTask<String, Void, JSONObject>() {
					@Override
					protected JSONObject doInBackground(String... tokens) {
						OkHttpClient client = new OkHttpClient();
						Request request = new Request.Builder()
								.url(String.format("https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=%s", tokens[0]))
								.addHeader("id_token", String.format("Bearer %s", tokens[1]))
								.build();

						try {
							Response response = client.newCall(request).execute();
							String jsonBody = response.body().string();
							return new JSONObject(jsonBody);
						} catch (Exception e) {
							DebugLog.logException(e);
						}
						return null;
					}

					@Override
					protected void onPostExecute(JSONObject userInfo) {
						String givenName = null;
						String familyName = null;
						String email = null;
						if (userInfo != null) {
							givenName = userInfo.optString("given_name", null);
							familyName = userInfo.optString("family_name", null);
							email = userInfo.optString("email", null);
						}

						SocialNetworkAccount account = new SocialNetworkAccount(
								IdentityProvider.GOOGLE, authState.getAccessToken(), givenName, familyName);
						if (options.checkDeviceAccounts()) {
							account.setHashedEmail(hashString(email));
						}
						notifySuccess(account);
					}
				}.execute(idToken, accessToken);
			}
		});
	}

	/**
	 * Complete the authorization process and notify success
	 * @param account SocialNetworkAccount containing user information for server authentication
	 */
	private void notifySuccess(SocialNetworkAccount account) {
		if (authMode.canStoreToken()) {
			SocialNetworkTokens.google().storeToken(account.getThirdPartyAccessToken());
		}

		onAuthenticationSuccess(account);
	}

	@Override
	public void dispose() {
		service.dispose();
	}


	/**
	 * Google authentication mode.
	 */
	public enum AuthenticationMode {

		/**
		 * Allow sign-in only.
		 */
		SIGN_IN_ONLY(false, GlobalObjectRegistry.getObject(Options.class).checkDeviceAccounts()
				? new String[] {"profile", "email"}
				: new String[] {"profile"}),

		/**
		 * Allow sign-in and obtaining friend list.
		 */
		OBTAIN_FRIENDS(true, "profile", "email");

		private final List<String> permissions;
		private final boolean allowStoringToken;

		AuthenticationMode(boolean allowStoringToken, String... permissions) {
			this.permissions = Arrays.asList(permissions);
			this.allowStoringToken = allowStoringToken;
		}

		private List<String> getPermissions() {
			return permissions;
		}

		private boolean canStoreToken() {
			return allowStoringToken;
		}
	}
}
