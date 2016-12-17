/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.module;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.text.format.DateUtils;

import com.microsoft.embeddedsocial.server.NetworkAvailability;
import com.microsoft.embeddedsocial.ui.fragment.base.Module;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragment;

/**
 * Display message about slow connection.
 */
public class SlowConnectionMessageModule extends Module {

	private static final long DEFAULT_ACTION_TIMEOUT = 10 * DateUtils.SECOND_IN_MILLIS;

	private final long actionTimeout;

	@StringRes
	private final int actionTextId;
	private final Runnable actionCallback;

	private Handler handler = new Handler(Looper.getMainLooper());
	private long operationStartedTime = -1;
	private Snackbar snackbar;

	public SlowConnectionMessageModule(BaseFragment owner, int actionTextId, Runnable actionCallback) {
		this(owner, actionTextId, DEFAULT_ACTION_TIMEOUT, actionCallback);
	}

	public SlowConnectionMessageModule(BaseFragment owner, int actionTextId, long actionTimeout, Runnable actionCallback) {
		super(owner);
		this.actionTextId = actionTextId;
		this.actionCallback = actionCallback;
		this.actionTimeout = actionTimeout;
	}

	public void onOperationStarted() {
		cancelPreviousMessage();
		operationStartedTime = SystemClock.elapsedRealtime();
		if (getOwner().isVisible()) {
			handler.postDelayed(this::showSlowConnectionMessage, actionTimeout);
		}
	}

	public void onOperationFinished() {
		cancelPreviousMessage();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (operationStartedTime != -1) {
			long timeSinceLoadingStarted = SystemClock.elapsedRealtime() - operationStartedTime;
			if (timeSinceLoadingStarted >= actionTimeout) {
				showSlowConnectionMessage();
			} else {
				handler.postDelayed(this::showSlowConnectionMessage, actionTimeout - timeSinceLoadingStarted);
			}
		}
	}

	@Override
	public void onPause() {
		handler.removeCallbacksAndMessages(null);
		super.onPause();
	}


	private void cancelPreviousMessage() {
		dismissMessage();
		snackbar = null;
		operationStartedTime = -1;
		handler.removeCallbacksAndMessages(null);
	}

	private void dismissMessage() {
		if (snackbar != null) {
			snackbar.dismiss();
		}
	}

	private void showSlowConnectionMessage() {
		if (isNetworkAvailable()) {
			//noinspection ConstantConditions
			snackbar = Snackbar.make(getOwner().getView(), R.string.es_message_slow_connection, Snackbar.LENGTH_INDEFINITE);
			snackbar.setAction(actionTextId, v -> {
				dismissMessage();
				if (actionCallback != null) {
					actionCallback.run();
				}
			});
			snackbar.show();
		}
	}

	private boolean isNetworkAvailable() {
		return GlobalObjectRegistry.getObject(NetworkAvailability.class).isNetworkAvailable();
	}

}
