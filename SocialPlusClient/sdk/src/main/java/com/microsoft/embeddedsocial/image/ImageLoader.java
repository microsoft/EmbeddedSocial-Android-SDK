/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.ConditionVariable;
import android.widget.ImageView;

import com.microsoft.embeddedsocial.sdk.BuildConfig;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Helper class for image loading.
 */
public final class ImageLoader {
	/**
	 * Toggle this boolean to get logs and stack traces from Picasso in debug builds
	 */
	private static final boolean LOG_PICASSO = false;

	private static final int THREADS_COUNT = 4;

	private static volatile Picasso picasso;

	private static ConditionVariable initCondition = new ConditionVariable();

	private static ExecutorService executor = Executors.newFixedThreadPool(THREADS_COUNT);

	private ImageLoader() {
	}

	public static void init(Context context) {
		// the initialization includes cache allocation so it's moved to the background thread
		executor.submit(() -> {
			Picasso.Builder builder = new Picasso.Builder(context);
			builder.executor(executor);
			if (BuildConfig.DEBUG && LOG_PICASSO) {
				builder.loggingEnabled(true);
				builder.listener(new Picasso.Listener() {
					@Override
					public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
						exception.printStackTrace();
					}
				});
			}
			picasso = builder.build();
			initCondition.open();
		});

	}

	public static void load(ImageView imageView, String uri) {
		performAction(() -> picasso.load(uri).into(imageView));
	}


	/**
	 * Loads an image into the target. You must keep a reference to the target, otherwise the image can be not loaded.
	 */
	public static void load(Target target, String uri, int width) {
		performAction(() -> picasso.load(uri).resize(width, 0).onlyScaleDown().into(target));
	}

	/**
	 * Loads an image into the target. You must keep a reference to the target, otherwise the image can be not loaded.
	 */
	public static void load(Target target, String uri) {
		performAction(() -> picasso.load(uri).into(target));
	}


	private static void performAction(Runnable task) {
		if (picasso == null) { // not initialized yet
			executor.submit(() -> {
				initCondition.block(); // wait for initialization
				task.run();
			});
		} else {
			task.run(); // just post a request in a caller thread
		}
	}

	public static void cancel(ImageView imageView) {
		performAction(() -> picasso.cancelRequest(imageView));
	}

	public static void cancel(Target target) {
		performAction(() -> picasso.cancelRequest(target));
	}

	public static Bitmap loadSync(String uri, Point requiredSize) throws IOException {
		if (picasso == null) {
			initCondition.block();
		}
		return picasso.load(uri).resize(requiredSize.x, requiredSize.y).get();
	}

}
