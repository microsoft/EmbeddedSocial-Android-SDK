/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.storage.exception.FatalDatabaseException;
import com.microsoft.embeddedsocial.data.storage.syncadapter.LatestActivitySyncAdapter;
import com.microsoft.embeddedsocial.data.storage.transaction.DbTransaction;
import com.microsoft.embeddedsocial.server.model.activity.ActivityFeedResponse;
import com.microsoft.embeddedsocial.server.model.notification.GetNotificationFeedResponse;
import com.microsoft.embeddedsocial.server.model.view.ActivityView;
import com.microsoft.embeddedsocial.server.model.view.AppCompactView;
import com.microsoft.embeddedsocial.server.sync.ISynchronizable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Caches user activity feeds.
 */
public class ActivityCache {

	private final MetadataStorage metadataStorage;
	private final Dao<UserCompactView, String> userDao;
	private final Dao<AppCompactView, String> appDao;
	private Dao<ActivityView, String> activityDao;
	private Dao<ActivityActor, Integer> activityActorDao;
	private Dao<ActivityFeed, Integer> activityFeedDao;

	/**
	 * Creates a new instance.
	 * @param context   valid context
	 */
	public ActivityCache(Context context) {
		metadataStorage = new MetadataStorage(context);
		DatabaseHelper helper = GlobalObjectRegistry.getObject(DatabaseHelper.class);
		userDao = helper.getUserDao();
		appDao = helper.getAppDao();
		try {
			activityDao = helper.getDao(ActivityView.class);
			activityActorDao = helper.getDao(ActivityActor.class);
			activityFeedDao = helper.getDao(ActivityFeed.class);
		} catch (SQLException e) {
			DebugLog.logException(e);
			throw new FatalDatabaseException(e);
		}
	}

	/**
	 * Checks whether the specified activity is unread.
	 * @param activityHandle    handle of activity to check
	 * @return  true if the activity is unread.
	 */
	boolean isActivityUnread(String activityHandle) {
		return metadataStorage.isActivityUnread(activityHandle);
	}

	/**
	 * Stores the last seen activity handle.
	 * @param lastActivityHandle    last seen activity handle.
	 * @return  true if the activity handle is newer than the previously stored one and was
	 * stored successfully.
	 */
	boolean storeLastActivityHandle(String lastActivityHandle) {
		return metadataStorage.storeLastActivityHandle(lastActivityHandle);
	}

	/**
	 * Acts as a sync producer by providing a list of sync actions for the last seen activity handle.
	 * Returns an empty list if there are no pending sync actions.
	 * @return  list of {@linkplain ISynchronizable} entities.
	 */
	public List<ISynchronizable> getActivityHandleSyncActions() {
		List<ISynchronizable> result;

		if (metadataStorage.isActivityHandleSyncRequired()) {
			result = Collections.singletonList(new LatestActivitySyncAdapter(metadataStorage));
		} else {
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * Gets cached response for notification feed.
	 * @return  notification feed response.
	 * @throws SQLException if the database fails
	 */
	GetNotificationFeedResponse getNotificationFeedResponse() throws SQLException {
		return new GetNotificationFeedResponse(getActivities(ActivityFeedType.NOTIFICATIONS));
	}

	/**
	 * Gets cached activity feed response for a specific feed type.
	 * @param feedType  feed type
	 * @return  activity feed response
	 * @throws SQLException if the database fails
	 */
	ActivityFeedResponse getActivityFeedResponse(ActivityFeedType feedType) throws SQLException {
		return new ActivityFeedResponse(getActivities(feedType));
	}

	private List<ActivityView> getActivities(ActivityFeedType feedType) throws SQLException {
		List<ActivityFeed> feed = activityFeedDao.queryBuilder()
			.where().eq(DbSchemas.ActivityFeed.FEED_TYPE, feedType)
			.query();

		List<ActivityView> activities = new ArrayList<>();
		for (ActivityFeed feedEntry : feed) {
			ActivityView activity = activityDao.queryForId(feedEntry.activityHandle);
			activityDao.refresh(activity);
			activity.setUnread(false);  // cached activities are always read
			activity.setActorUsers(getActivityActorUsers(activity));
			activities.add(activity);
		}

		return activities;
	}

	private List<UserCompactView> getActivityActorUsers(ActivityView activity) throws SQLException {
		List<ActivityActor> actors = activityActorDao.queryBuilder()
			.where().eq(DbSchemas.UserActivity.ACTIVITY_HANDLE, activity.getHandle())
			.query();

		List<UserCompactView> users = new ArrayList<>();

		for (ActivityActor actor : actors) {
			users.add(userDao.queryForId(actor.userHandle));
		}

		return users;
	}

	/**
	 * Stores activity feed in cache.
	 * @param feedType      activity feed type
	 * @param activities    activities to store
	 * @param deleteExisting    specifies whether to delete contents of the feed that were stored
	 *                          before this call
	 */
	void storeActivityFeed(ActivityFeedType feedType, List<ActivityView> activities,
		boolean deleteExisting) throws SQLException {

		if (deleteExisting) {
			deleteFeed(feedType);
		}
		DbTransaction.performTransaction(activityDao, () -> {
			for (ActivityView activity : activities) {
				storeActivity(activity);
				activityFeedDao.createOrUpdate(new ActivityFeed(activity.getHandle(), feedType));
			}
		});
	}

	private void deleteFeed(ActivityFeedType feedType) throws SQLException {
		DeleteBuilder<ActivityFeed, Integer> feedDeleteBuilder = activityFeedDao.deleteBuilder();
		feedDeleteBuilder.where().eq(DbSchemas.ActivityFeed.FEED_TYPE, feedType);
		feedDeleteBuilder.delete();
	}

	private void storeActivity(ActivityView activity) throws SQLException {
		deleteActivityActors(activity);
		activityDao.createOrUpdate(activity);
		appDao.createOrUpdate(activity.getApp());
		List<UserCompactView> actorUsers = activity.getActorUsers();
		if (actorUsers != null) {
			for (UserCompactView actorUser : actorUsers) {
				userDao.createOrUpdate(actorUser);
				activityActorDao.createOrUpdate(new ActivityActor(activity.getHandle(), actorUser.getHandle()));
			}
		}
	}

	private void deleteActivityActors(ActivityView activity) throws SQLException {
		DeleteBuilder<ActivityActor, Integer> deleteBuilder = activityActorDao.deleteBuilder();
		deleteBuilder.where().eq(DbSchemas.UserActivity.ACTIVITY_HANDLE, activity.getHandle());
		deleteBuilder.delete();
	}

	@DatabaseTable(tableName = DbSchemas.ActivityActor.TABLE_NAME)
	@SuppressWarnings("unused")
	static class ActivityActor {

		@DatabaseField(generatedId = true)
		private int id;

		@DatabaseField(columnName = DbSchemas.UserActivity.ACTIVITY_HANDLE)
		private String activityHandle;

		@DatabaseField(columnName = DbSchemas.UserFeeds.USER_HANDLE)
		private String userHandle;

		ActivityActor(String activityHandle, String userHandle) {
			this.activityHandle = activityHandle;
			this.userHandle = userHandle;
		}

		/**
		 * For ORM.
		 */
		ActivityActor() {  }
	}

	@DatabaseTable(tableName = DbSchemas.ActivityFeed.TABLE_NAME)
	@SuppressWarnings("unused")
	static class ActivityFeed {

		@DatabaseField(generatedId = true)
		private int id;

		@DatabaseField(columnName = DbSchemas.ActivityFeed.FEED_TYPE)
		private ActivityFeedType feedType;

		@DatabaseField(columnName = DbSchemas.UserActivity.ACTIVITY_HANDLE)
		private String activityHandle;

		/**
		 * For ORM.
		 */
		ActivityFeed() {  }

		ActivityFeed(String activityHandle, ActivityFeedType feedType) {
			this.activityHandle = activityHandle;
			this.feedType = feedType;
		}
	}

	/**
	 * Defines possible types of activity feeds.
	 */
	public enum ActivityFeedType {
		FOLLOWING_ACTIVITY,
		NOTIFICATIONS
	}

	/**
	 * Stores metadata describing user activity.
	 */
	public static class MetadataStorage {

		private static final String SHARED_PREFERENCES_NAME = "activity_handle";
		private static final String KEY_LAST_HANDLE = "activityHandle";
		private static final String KEY_SYNCED = "synced";

		private final SharedPreferences dataStorage;

		private MetadataStorage(Context context) {
			dataStorage = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		}

		/**
		 * Gets the handle of last stored activity.
		 * @return  activity handle or an empty string.
		 */
		public String getLastActivityHandle() {
			return dataStorage.getString(KEY_LAST_HANDLE, "");
		}

		/**
		 * Checks whether the specified activity is unread.
		 * @param activityHandle    handle of activity to check
		 * @return  true if the activity is unread.
		 */
		public boolean isActivityUnread(String activityHandle) {
			String lastActivityHandle = getLastActivityHandle();
			return TextUtils.isEmpty(lastActivityHandle)
				|| activityHandle.compareTo(lastActivityHandle) < 0;
		}

		/**
		 * Stores the last seen activity handle.
		 * @param   newActivityHandle    last seen activity handle.
		 * @return  true if the activity handle is newer than the previously stored one and was
		 * stored successfully.
		 */
		public boolean storeLastActivityHandle(String newActivityHandle) {
			boolean result = false;

			if (!TextUtils.isEmpty(newActivityHandle) && isActivityUnread(newActivityHandle)) {
				dataStorage.edit()
					.putString(KEY_LAST_HANDLE, newActivityHandle)
					.putBoolean(KEY_SYNCED, false)
					.apply();
				result = true;
			}

			return result;
		}

		/**
		 * Marks last activity handle as synchronized.
		 */
		public void markActivityHandleSynchronized() {
			dataStorage.edit()
				.putBoolean(KEY_SYNCED, true)
				.apply();
		}

		/**
		 * Clears last activity handle.
		 */
		public void clearLastActivityHandle() {
			dataStorage.edit()
				.remove(KEY_LAST_HANDLE)
				.putBoolean(KEY_SYNCED, false)
				.apply();
		}

		/**
		 * Checks whether last activity handle should be synchronized.
		 * @return  true if last activity handle has to be synchronized.
		 */
		boolean isActivityHandleSyncRequired() {
			return !TextUtils.isEmpty(getLastActivityHandle()) && !dataStorage.getBoolean(KEY_SYNCED, false);
		}
	}
}
