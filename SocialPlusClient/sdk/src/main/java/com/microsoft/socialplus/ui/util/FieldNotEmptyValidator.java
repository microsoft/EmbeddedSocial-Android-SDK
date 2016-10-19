/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.util;

import android.content.Context;

import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.view.TextInput;

/**
 * Checks that the field is not empty.
 */
public class FieldNotEmptyValidator extends TextInput.Validator {

	public FieldNotEmptyValidator(Context context) {
		super(context.getString(R.string.sp_message_field_cant_be_empty));
	}

	@Override
	protected boolean isValid(String text) {
		return !TextHelper.isEmpty(text);
	}
}
