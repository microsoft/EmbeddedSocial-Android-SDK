/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.sdk;

/**
 * Control drawer layout state
 */
public interface IDrawerState {
	void openDrawer();
	void closeDrawer();
	boolean isDrawerOpen();
}
