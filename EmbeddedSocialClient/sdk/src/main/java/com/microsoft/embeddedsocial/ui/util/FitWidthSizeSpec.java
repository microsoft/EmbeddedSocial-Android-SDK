/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util;

import android.app.Activity;
import android.graphics.Point;

import com.microsoft.embeddedsocial.base.utils.BitmapUtils;
import com.microsoft.embeddedsocial.base.utils.ViewUtils;

/**
 * Image transformation to fit all available width.
 */
public class FitWidthSizeSpec implements BitmapUtils.SizeSpec {

	private static final int MAX_IMAGE_SIZE = 1024;

	private final int availableWidth;

	public FitWidthSizeSpec(Activity activity) {
		this(ViewUtils.getDisplayWidth(activity));
	}

	public FitWidthSizeSpec(int availableWidth) {
		this.availableWidth = availableWidth;
	}

	@Override
	public Point resolveRequiredSize(Point originalSize) {
		double bitmapRatio = (double) originalSize.y / originalSize.x;
		Point availableSpace = new Point(availableWidth, (int) (bitmapRatio * availableWidth));
		int maxDimension = Math.max(availableSpace.x, availableSpace.y);
		if (maxDimension > MAX_IMAGE_SIZE) {
			double scale = (double) MAX_IMAGE_SIZE / maxDimension;
			availableSpace.set((int) (scale * availableSpace.x), (int) (scale * availableSpace.y));
		}
		return availableSpace;
	}

}
