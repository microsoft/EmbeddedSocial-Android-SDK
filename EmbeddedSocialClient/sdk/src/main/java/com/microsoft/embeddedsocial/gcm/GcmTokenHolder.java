/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.gcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.microsoft.embeddedsocial.server.sync.ISynchronizable;

import java.util.Collections;
import java.util.List;

/**
 * Holds GCM token along with its state (such as synced/unsynced, etc.)
 */
public class GcmTokenHolder {

	private static final String PREFERENCES_NAME = "gcm_token";
	private static final String DATA_KEY = "token_state";

	private final SharedPreferences dataStorage;

	private GcmTokenHolder(Context context) {
		dataStorage = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
	}

	/**
	 * Creates a new instance of token holder attached to the given context.
	 * @param   context   valid context
	 * @return  {@linkplain GcmTokenHolder} instance.
	 */
	public static GcmTokenHolder create(Context context) {
		return new GcmTokenHolder(context);
	}

	/**
	 * Stores GCM token.
	 * @param token the token to store
	 */
	public void storeToken(String token) {
		TokenState state = new TokenState();
		state.token = token;
		state.valid = true;
		state.timestamp = System.currentTimeMillis();
		storeTokenState(state);
	}

	/**
	 * Gets the timestamp of the moment when the current token was obtained.
	 * @return  token timestamp.
	 */
	public long getTokenTimestamp() {
		return readTokenState().timestamp;
	}

	/**
	 * Checks if the token is synchronized.
	 * @return  true if the token is synchronized.
	 */
	public boolean isTokenSynchronized() {
		return readTokenState().synced;
	}

	/**
	 * Gets token synchronization operations if any are pending. If there's no valid token
	 * or the token is already synchronized, returns empty list.
	 * @return  list of sync operations.
	 */
	public List<ISynchronizable> getTokenSyncOperations() {
		List<ISynchronizable> result;
		TokenState tokenState = readTokenState();

		if (tokenState.valid && !tokenState.synced) {
			result = Collections.singletonList(new TokenSyncAdapter(this));
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * Marks GCM token as synchronized.
	 */
	public void markTokenSynchronized() {
		TokenState state = readTokenState();
		if (state.valid) {
			state.synced = true;
		}
		storeTokenState(state);
	}

	/**
	 * Checks if current token is valid.
	 * @return  true if current token is valid.
	 */
	public boolean hasValidToken() {
		return readTokenState().valid;
	}

	/**
	 * Gets GCM token.
	 * @return  GCM token or null if it wasn't stored previously.
	 */
	public String getToken() {
		return readTokenState().token;
	}

	/**
	 * Resets current GCM token.
	 */
	public void resetToken() {
		storeTokenState(new TokenState());
	}

	private TokenState readTokenState() {
		String serializedState = dataStorage.getString(DATA_KEY, "");
		TokenState result;

		if (TextUtils.isEmpty(serializedState)) {
			result = new TokenState();
		} else {
			result = new Gson().fromJson(serializedState, TokenState.class);
		}

		return result;
	}

	private void storeTokenState(TokenState state) {
		dataStorage.edit()
			.putString(DATA_KEY, new Gson().toJson(state))
			.apply();
	}

	/**
	 * Persistent token state.
	 */
	private class TokenState {
		String token;
		boolean valid;
		boolean synced;
		long timestamp;
	}
}
