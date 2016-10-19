/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.social;

import com.microsoft.socialplus.social.exception.SocialNetworkException;

/**
 * A synchronous request to authorize in a social network.
 */
public interface AuthorizationRequest {
	void call() throws SocialNetworkException;
}
