/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.trigger.consistency;

import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.data.storage.trigger.ISqlTrigger;
import com.microsoft.embeddedsocial.data.storage.trigger.TriggerGenerator;

/**
 * Cache consistency triggers for notifications/activities.
 */
public class NotificationTriggers {

	private static final String DELETE_UNREFERENCED_ACTIVITIES_STATEMENT
		= "delete from " + DbSchemas.UserActivity.TABLE_NAME
		+ " where not exists (select 1 from " + DbSchemas.ActivityFeed.TABLE_NAME + " as feeds" +
		" where feeds." + DbSchemas.UserActivity.ACTIVITY_HANDLE + " = "
		+ DbSchemas.UserActivity.ACTIVITY_HANDLE + ")";

	private static final String DELETE_UNREFERENCED_ACTORS_STATEMENT
		= "delete from " + DbSchemas.ActivityActor.TABLE_NAME
		+ " where OLD." + DbSchemas.UserActivity.ACTIVITY_HANDLE + " = "
		+ DbSchemas.ActivityActor.TABLE_NAME + "."  + DbSchemas.UserActivity.ACTIVITY_HANDLE;

	/**
	 * All SQL triggers contained in this class.
	 */
	public static final ISqlTrigger[] TRIGGERS = {
		TriggerGenerator.newOnAfterDeleteTrigger(       // TODO this can be optimized
			"activity_feed_consistency",
			DbSchemas.ActivityFeed.TABLE_NAME,
			DELETE_UNREFERENCED_ACTIVITIES_STATEMENT
		),
		TriggerGenerator.newOnAfterDeleteTrigger(
			"actor_consistency",
			DbSchemas.UserActivity.TABLE_NAME,
			DELETE_UNREFERENCED_ACTORS_STATEMENT
		),
	};
}
