/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util;

import com.microsoft.embeddedsocial.data.display.DisplayMethod;

/**
 * Items displaying params.
 */
public class DisplayParams {

	public final DisplayMethod method;
	public final int spanCount;

	public DisplayParams(DisplayMethod method, int spanCount) {
		this.method = method;
		this.spanCount = spanCount;
	}
}
