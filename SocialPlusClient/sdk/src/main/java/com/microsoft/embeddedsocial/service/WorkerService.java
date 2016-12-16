/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service;

import android.content.Context;

import com.microsoft.embeddedsocial.base.service.AbstractProcessingService;
import com.microsoft.embeddedsocial.base.service.IServiceIntentProcessor;
import com.microsoft.embeddedsocial.base.service.ServiceLauncher;

/**
 * Worker service.
 */
public class WorkerService extends AbstractProcessingService {

	IntentProcessorFactory factory = new IntentProcessorFactory(this);

	@Override
	protected IServiceIntentProcessor createIntentProcessor() {
		return factory.createIntentProcessor();
	}

	public static ServiceLauncher<ServiceAction> getLauncher(Context context) {
		return new ServiceLauncher<>(context, WorkerService.class);
	}
}
