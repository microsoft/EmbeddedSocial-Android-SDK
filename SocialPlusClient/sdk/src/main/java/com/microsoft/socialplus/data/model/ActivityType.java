/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.data.model;

/**
 * Defines possible activities type (see Activity Feed page).
 */
public enum ActivityType {
	LIKE,
	CHILD,
	CHILD_PEER,
	MENTION,
	FOLLOWING,
	FOLLOW_ACCEPT,
	FOLLOW_REQUEST,
	BLOCKED, // currently not supported
	CONTENT, // currently not supported
	PIN      // currently not supported
}
