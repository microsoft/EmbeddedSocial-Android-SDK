/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher.base;

import com.microsoft.embeddedsocial.base.utils.thread.ThreadUtils;

/**
 * Helper class for calling callback methods. It moves all calls to the main thread and checks that the callback is not null.
 */
class CallbackNotifier {

	private Callback fetcherCallback;

	void setCallback(Callback callback) {
		this.fetcherCallback = callback;
	}

	void notifyStateChanged(FetcherState state) {
		postCallback(callback -> callback.onStateChanged(state));
	}

	void notifyDataRemoved() {
		postCallback(Callback::onDataRemoved);
	}

	void notifyDataRequestSucceeded() {
		postCallback(Callback::onDataRequestSucceeded);
	}

	void notifyDataRequestFailed(Exception e) {
		postCallback(callback -> callback.onDataRequestFailed(e));
	}

	void notifyDataUpdated() {
		postCallback(Callback::onDataUpdated);
	}

	private void postCallback(CallbackMethod method) {
		ThreadUtils.runOnMainThread(() -> {
			if (fetcherCallback != null) {
				method.call(fetcherCallback);
			}
		});
	}

	/**
	 * Callback method.
	 */
	private interface CallbackMethod {
		void call(Callback callback);
	}

}
