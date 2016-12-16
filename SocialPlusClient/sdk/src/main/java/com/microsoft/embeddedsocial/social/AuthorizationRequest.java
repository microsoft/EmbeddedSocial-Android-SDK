/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.social;

import com.microsoft.embeddedsocial.social.exception.SocialNetworkException;

/**
 * A synchronous request to authorize in a social network.
 */
public interface AuthorizationRequest {
	void call() throws SocialNetworkException;
}
