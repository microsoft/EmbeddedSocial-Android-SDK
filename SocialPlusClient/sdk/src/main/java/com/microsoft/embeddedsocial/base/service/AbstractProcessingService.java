/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.microsoft.embeddedsocial.base.service.IServiceIntentProcessor.IQueueStateListener;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;

/**
 * Base class for intent processing services.
 */
public abstract class AbstractProcessingService extends Service {

	private final IQueueStateListener queueStateListener = new QueueStateListenerDelegate();
	private IServiceIntentProcessor intentProcessor;
	private int lastStartId;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * Is called when an intent processor has to be created.
	 * @return	{@link IServiceIntentProcessor} instance.
	 */
	protected abstract IServiceIntentProcessor createIntentProcessor();
	
	/**
	 * Is used to check if the service has to be automatically stopped when
	 * all pending requests have been processed.
	 * @return	<code>true</code> if the service has to be stopped when it's idle.
	 */
	protected boolean shouldStopWhenIdle() {
		// default implementation is a sticky service
		return false;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		this.intentProcessor = createIntentProcessor();
		intentProcessor.setQueueStateListener(queueStateListener);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.lastStartId = startId;
		if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
			boolean processed = intentProcessor.processIntentAsync(intent);
			if (!processed) {
				DebugLog.e("couldn't find intent handler for action " + intent.getAction());
			}
		} else {
			DebugLog.e("received invalid intent (null intent or null action)");
		}
		
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		intentProcessor.dispose();
		super.onDestroy();
	}

	private void onTaskQueueIsEmpty() {
		if (shouldStopWhenIdle()) {
			boolean willBeStopped = stopSelfResult(lastStartId);
			String serviceName = getClass().getSimpleName();
			
			if (!willBeStopped) {
				DebugLog.w("can't stop " + serviceName + ", request found in the external queue");
			} else {
				DebugLog.i(serviceName + " is idle, stopping");
			}
		}
	}
	
	/**
	 * Internal queue state listener.
	 */
	private class QueueStateListenerDelegate implements IQueueStateListener {
		@Override
		public void onTaskQueueIsEmpty() {
			AbstractProcessingService.this.onTaskQueueIsEmpty();
		}
	}
}
