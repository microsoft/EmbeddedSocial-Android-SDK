/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.event.data;

import com.microsoft.socialplus.base.event.HandlingThread;
import com.microsoft.socialplus.base.event.ThreadType;
import com.microsoft.socialplus.data.model.AccountData;
import com.microsoft.socialplus.event.BaseUserEvent;


/**
 * Profile data was loaded event.
 */
@HandlingThread(ThreadType.MAIN)
public class ProfileDataUpdatedEvent extends BaseUserEvent {

	private final AccountData accountData;

	public ProfileDataUpdatedEvent(String userHandle, AccountData accountData) {
		super(userHandle);
		this.accountData = accountData;
	}

	public AccountData getAccountData() {
		return accountData;
	}
}
