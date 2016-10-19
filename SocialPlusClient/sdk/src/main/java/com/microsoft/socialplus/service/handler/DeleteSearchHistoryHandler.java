/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.service.handler;

import android.content.Intent;

import com.microsoft.socialplus.base.service.IServiceIntentHandler;
import com.microsoft.socialplus.data.storage.SearchHistory;
import com.microsoft.socialplus.service.ServiceAction;

/**
 * Deletes the search history.
 */
public class DeleteSearchHistoryHandler implements IServiceIntentHandler<ServiceAction> {
	@Override
	public void handleIntent(ServiceAction action, Intent intent) {
		new SearchHistory().clear();
	}

	@Override
	public void dispose() {

	}
}
