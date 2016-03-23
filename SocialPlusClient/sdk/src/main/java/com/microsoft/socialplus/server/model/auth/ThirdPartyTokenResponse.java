/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.auth;

import com.microsoft.socialplus.autorest.models.GetRequestTokenResponse;

public class ThirdPartyTokenResponse {

	private String requestToken;

	public ThirdPartyTokenResponse(GetRequestTokenResponse response) {
		requestToken = response.getRequestToken();
	}

	public String getRequestToken() {
		return requestToken;
	}
}
