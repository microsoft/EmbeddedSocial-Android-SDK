/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.service.handler;

import android.content.Context;
import android.content.Intent;

import com.microsoft.socialplus.base.service.IServiceIntentHandler;
import com.microsoft.socialplus.service.ServiceAction;
import com.microsoft.socialplus.service.WorkerService;

public class BackgroundInitializationHandler implements IServiceIntentHandler<ServiceAction> {

	private final Context context;

	public BackgroundInitializationHandler(Context context) {
		this.context = context;
	}

	@Override
	public void handleIntent(ServiceAction action, Intent intent) {
		WorkerService.getLauncher(context).launchService(ServiceAction.GCM_REGISTER);
	}

	@Override
	public void dispose() {  }
}
