/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * This activity is invisible for users. It always must be the first launched sdk's activity (so we can close all sdk's activities).
 */
public class GateActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		finish();
	}
}
