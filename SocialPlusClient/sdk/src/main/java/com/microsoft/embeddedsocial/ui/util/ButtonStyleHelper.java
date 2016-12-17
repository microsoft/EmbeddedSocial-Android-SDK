/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.sdk.R;

/**
 * Helps to apply styles to buttons.
 */
public class ButtonStyleHelper {

	private final int completedColor;
	private final int accentColor;
	private final ColorStateList greenColor;
	private final ColorStateList grayColor;
	private final ColorStateList redColor;

	public ButtonStyleHelper(Context context) {
		Resources res = context.getResources();
		completedColor = res.getColor(R.color.es_button_completed_text);
		greenColor = res.getColorStateList(R.color.es_button_text_green);
		grayColor = res.getColorStateList(R.color.es_button_text_gray);
		redColor = res.getColorStateList(R.color.es_pink_500);
		accentColor = GlobalObjectRegistry.getObject(Options.class).getAccentColor();
	}

	public void applyGreenCompletedStyle(TextView view) {
		view.setTextColor(completedColor);
		view.setBackgroundResource(R.drawable.es_button_green_completed);
	}

	public void applyGreenStyle(TextView view) {
		view.setTextColor(greenColor);
		view.setBackgroundResource(R.drawable.es_button_green);
	}

	public void applyGrayStyle(TextView view) {
		view.setTextColor(grayColor);
		view.setBackgroundResource(R.drawable.es_button_gray);
	}

	public void applyRedStyle(TextView view) {
		view.setTextColor(redColor);
		view.setBackgroundResource(R.drawable.es_button_red);
	}

	public void applyRedCompletedStyle(TextView view) {
		view.setTextColor(completedColor);
		view.setBackgroundResource(R.drawable.es_button_red_completed);
	}

	public void applyAccentColor(ImageView imageView, boolean isApply) {
		if (isApply) {
			imageView.setColorFilter(accentColor);
		} else {
			imageView.setColorFilter(null);
		}
	}

	public void applyAccentColor(TextView textView) {
		textView.setTextColor(accentColor);
		final Drawable leftDrawable = textView.getCompoundDrawables()[0];
		if (leftDrawable != null) {
			leftDrawable.setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);
		}
	}
}
