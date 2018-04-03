/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.pin;

import com.microsoft.embeddedsocial.server.model.UserRequest;

public class GenericPinRequest extends UserRequest {
	protected final String topicHandle;

	public GenericPinRequest(String topicHandle) {
		this.topicHandle = topicHandle;
	}
}
