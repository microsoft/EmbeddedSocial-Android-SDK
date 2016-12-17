/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.microsoft.embeddedsocial.sdk.R;


/**
 * ImageView with set aspect ratio.
 */
public class AspectRatioImageView extends ImageView {

	private float ratio = 1;

	public AspectRatioImageView(Context context) {
		super(context);
	}

	public AspectRatioImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public AspectRatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(attrs);
	}

	private void init(AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.es_AspectRatioImageView);
		ratio = a.getFloat(R.styleable.es_AspectRatioImageView_es_aspectRatio, 1);
		a.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int newWidth = getMeasuredWidth();
		int newHeight = (int) (newWidth * ratio);
		setMeasuredDimension(newWidth, newHeight);
	}

}
