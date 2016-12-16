/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service;

/**
 * Service actions.
 */
public enum ServiceAction {

	BACKGROUND_INIT,

	DELETE_ACCOUNT,

	DELETE_SEARCH_HISTORY,

	GCM_REGISTER,

	GET_COMMENT,

	GET_REPLY,

	SIGN_IN,

	SIGN_OUT,

	SYNC_DATA,

	CREATE_ACCOUNT,

	UPDATE_ACCOUNT,

	UPDATE_NOTIFICATION_COUNT,

	LINK_USER_THIRD_PARTY_ACCOUNT,

	UNLINK_USER_THIRD_PARTY_ACCOUNT,

	REMOVE_FOLLOWER,
}
