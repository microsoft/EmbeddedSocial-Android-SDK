/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.microsoft.embeddedsocial.sdk.R;

/**
 * Consists of a single web view used for web authentication process.
 */
public class WebAuthenticationActivity extends Activity {

	public static final String EXTRA_AUTH_URL = "authUrl";
	public static final String EXTRA_END_URL = "endUrl";
	public static final String EXTRA_AUTH_MODE = "authMode";
	public static final String EXTRA_RESULT_URL = "resultUrl";

	private String endUrl;
	private AuthMode authMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!getResources().getBoolean(R.bool.es_isTablet)) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		WebView webView = new WebView(this);
		setContentView(webView);
		Intent intent = getIntent();
		int authModeIndex = intent.getIntExtra(EXTRA_AUTH_MODE, 0);
		authMode = AuthMode.values()[authModeIndex];
		if (authMode == AuthMode.WAIT_FOR_END_URL
			|| authMode == AuthMode.WAIT_FOR_END_URL_RETURN_TITLE) {

			endUrl = intent.getStringExtra(EXTRA_END_URL);
		}
		String authUrl = intent.getStringExtra(EXTRA_AUTH_URL);
		webView.setWebViewClient(new WebViewClientImpl());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(authUrl);
	}

	private class WebViewClientImpl extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (authMode == AuthMode.STOP_ON_FIRST_REDIRECT) {
				onAuthCompleted(url);
			} else {
				boolean endUrlReached = url.startsWith(endUrl);
				if (!endUrlReached) {
					view.loadUrl(url);
				} else if (authMode == AuthMode.WAIT_FOR_END_URL) {
					onAuthCompleted(url);
				}
			}

			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if (authMode == AuthMode.WAIT_FOR_END_URL_RETURN_TITLE && url.startsWith(endUrl)) {
				onAuthCompleted(view.getTitle());
			}
		}
	}

	private void onAuthCompleted(String url) {
		setResult(RESULT_OK, new Intent().putExtra(EXTRA_RESULT_URL, url));
		finish();
	}

	/**
	 * Authentication modes supported by this activity.
	 */
	public enum AuthMode {

		/**
		 * Authentication stops on first redirect URL, the URL is returned.
		 */
		STOP_ON_FIRST_REDIRECT,

		/**
		 * Authentication stops when a redirect URL matches the end URL.
		 */
		WAIT_FOR_END_URL,

		/**
		 * Authentication stops when a redirect URL matches the end URL, and then
		 * the window title is returned.
		 */
		WAIT_FOR_END_URL_RETURN_TITLE
	}
}
