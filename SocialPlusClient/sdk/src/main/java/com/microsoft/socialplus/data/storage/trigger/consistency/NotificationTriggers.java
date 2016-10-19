/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.data.storage.trigger.consistency;

import com.microsoft.socialplus.data.storage.DbSchemas.ActivityActor;
import com.microsoft.socialplus.data.storage.DbSchemas.ActivityFeed;
import com.microsoft.socialplus.data.storage.DbSchemas.UserActivity;
import com.microsoft.socialplus.data.storage.trigger.ISqlTrigger;
import com.microsoft.socialplus.data.storage.trigger.TriggerGenerator;

/**
 * Cache consistency triggers for notifications/activities.
 */
public class NotificationTriggers {

	private static final String DELETE_UNREFERENCED_ACTIVITIES_STATEMENT
		= "delete from " + UserActivity.TABLE_NAME
		+ " where not exists (select 1 from " + ActivityFeed.TABLE_NAME + " as feeds" +
		" where feeds." + UserActivity.ACTIVITY_HANDLE + " = "
		+ UserActivity.ACTIVITY_HANDLE + ")";

	private static final String DELETE_UNREFERENCED_ACTORS_STATEMENT
		= "delete from " + ActivityActor.TABLE_NAME
		+ " where OLD." + UserActivity.ACTIVITY_HANDLE + " = "
		+ ActivityActor.TABLE_NAME + "."  + UserActivity.ACTIVITY_HANDLE;

	/**
	 * All SQL triggers contained in this class.
	 */
	public static final ISqlTrigger[] TRIGGERS = {
		TriggerGenerator.newOnAfterDeleteTrigger(       // TODO this can be optimized
			"activity_feed_consistency",
			ActivityFeed.TABLE_NAME,
			DELETE_UNREFERENCED_ACTIVITIES_STATEMENT
		),
		TriggerGenerator.newOnAfterDeleteTrigger(
			"actor_consistency",
			UserActivity.TABLE_NAME,
			DELETE_UNREFERENCED_ACTORS_STATEMENT
		),
	};
}
