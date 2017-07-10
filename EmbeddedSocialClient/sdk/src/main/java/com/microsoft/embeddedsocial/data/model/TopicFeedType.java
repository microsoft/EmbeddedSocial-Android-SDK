/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.model;

/**
 * Type of topics feed.
 */
public enum TopicFeedType {
	USER_RECENT,                 // shown on user's profile page (including the current user's profile page)
	USER_POPULAR,                // shown on user's profile page (including the current user's profile page)
	FOLLOWING_RECENT,            // shown on Home page
	EVERYONE_RECENT,             // not used
	EVERYONE_POPULAR_ALL_TIME,   // shown on Popular page
	EVERYONE_POPULAR_THIS_MONTH, // shown on Popular page
	EVERYONE_POPULAR_THIS_WEEK,  // shown on Popular page
	EVERYONE_POPULAR_TODAY,      // shown on Popular page
	MY_RECENT,
	MY_POPULAR,
	MY_LIKED,
	FEATURED
}
