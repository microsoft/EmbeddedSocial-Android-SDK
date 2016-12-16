/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher.base;

import java.util.LinkedList;
import java.util.List;

/**
 * Composite callback.
 */
class CompositeCallback extends Callback {

	private final List<Callback> callbacks = new LinkedList<>();

	void addCallback(Callback callback) {
		callbacks.add(callback);
	}

	@Override
	public void onDataRemoved() {
		for (Callback callback : callbacks) {
			callback.onDataRemoved();
		}
	}

	@Override
	public void onDataRequestSucceeded() {
		for (Callback callback : callbacks) {
			callback.onDataRequestSucceeded();
		}
	}

	@Override
	public void onDataRequestFailed(Exception e) {
		for (Callback callback : callbacks) {
			callback.onDataRequestFailed(e);
		}
	}

	@Override
	public void onStateChanged(FetcherState newState) {
		for (Callback callback : callbacks) {
			callback.onStateChanged(newState);
		}
	}

	@Override
	public void onDataUpdated() {
		for (Callback callback : callbacks) {
			callback.onDataUpdated();
		}
	}
}
