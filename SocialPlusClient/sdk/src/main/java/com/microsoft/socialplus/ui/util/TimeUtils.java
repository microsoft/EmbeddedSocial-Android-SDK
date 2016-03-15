/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.util;

import android.content.res.Resources;

import com.microsoft.socialplus.sdk.R;

/**
 * Helper class to convert time to text
 */
public class TimeUtils {
	private static final int MINUTE_IN_SECONDS = 60;
	private static final int HOUR_IN_MINUTES = 60;
	private static final int DAY_IN_HOURS = 24;
	private static final int WEEK_IN_DAYS = 7;

	private TimeUtils() {
	}

	public static String timeToText(Resources resources, long elapsedTime) {
		if (elapsedTime < MINUTE_IN_SECONDS) {
			return resources.getString(R.string.sp_elapsed_time_seconds_pattern, elapsedTime);
		}

		long minutes = elapsedTime / MINUTE_IN_SECONDS;
		if (minutes < HOUR_IN_MINUTES) {
			return resources.getString(R.string.sp_elapsed_time_minutes_pattern, minutes);
		}

		long hours = minutes / HOUR_IN_MINUTES;
		if (hours < DAY_IN_HOURS) {
			return resources.getString(R.string.sp_elapsed_time_hours_pattern, hours);
		}

		long days = hours / DAY_IN_HOURS;
		if (days < WEEK_IN_DAYS) {
			return resources.getString(R.string.sp_elapsed_time_days_pattern, days);
		}

		return resources.getString(R.string.sp_elapsed_time_weeks_pattern, days / WEEK_IN_DAYS);
	}
}
