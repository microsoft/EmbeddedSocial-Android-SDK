/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.view;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.autorest.models.ActivityType;
import com.microsoft.embeddedsocial.autorest.models.BlobType;
import com.microsoft.embeddedsocial.autorest.models.ContentCompactView;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.server.model.TimedItem;
import com.microsoft.embeddedsocial.server.model.UniqueItem;
import com.microsoft.embeddedsocial.ui.util.TimeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@DatabaseTable(tableName = DbSchemas.UserActivity.TABLE_NAME)
public class ActivityView implements UniqueItem, TimedItem {

	@DatabaseField(columnName = DbSchemas.UserActivity.ACTIVITY_HANDLE, id = true)
	private String activityHandle;

	@DatabaseField
	private String activityType;

	private List<UserCompactView> actorUsers;

	@DatabaseField
	private int count;

	@DatabaseField
	private String actedOnContentType;

	@DatabaseField
	private String actedOnContentHandle;

	@DatabaseField
	private String actedOnContentText;

	@DatabaseField
	private String actedOnContentBlobType;

	@DatabaseField
	private String actedOnContentBlobUrl;

	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private UserCompactView actedOnUser;

	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private AppCompactView app;

	@DatabaseField
	private long createdTime;

	@DatabaseField
	private boolean unread;

	/**
	 * Is used by ORM.
	 */
	ActivityView() {  }

	public ActivityView(com.microsoft.embeddedsocial.autorest.models.ActivityView view) {
		activityHandle = view.getActivityHandle();
		activityType = view.getActivityType().toValue();
		actorUsers = loadActorUsers(view.getActorUsers());
		count = view.getTotalActions();
		ContentCompactView actedOnContent = view.getActedOnContent();
		if (actedOnContent != null) {
			actedOnContentType = actedOnContent.getContentType().toValue();
			actedOnContentHandle = actedOnContent.getContentHandle();
			actedOnContentText = actedOnContent.getText();
			actedOnContentBlobType = actedOnContent.getBlobType().toValue();
			actedOnContentBlobUrl = actedOnContent.getBlobUrl();
		}
		actedOnUser = new UserCompactView(view.getActedOnUser());
		app = new AppCompactView(view.getApp());
		createdTime = view.getCreatedTime().getMillis();
		unread = view.getUnread();
	}

	private List<UserCompactView> loadActorUsers(List<com.microsoft.embeddedsocial.autorest.models.UserCompactView> autorestUsers) {
		ArrayList<UserCompactView> users = new ArrayList<>();
		for (com.microsoft.embeddedsocial.autorest.models.UserCompactView user : autorestUsers) {
			users.add(new UserCompactView(user));
		}
		return users;
	}

	public boolean isUnread() {
		return unread;
	}

	public void setUnread(boolean unread) {
		this.unread = unread;
	}

	@Override
	public String getHandle() {
		return activityHandle;
	}

	public ActivityType getActivityType() {
		return ActivityType.fromValue(activityType);
	}

	public List<UserCompactView> getActorUsers() {
		return actorUsers;
	}

	public int getCount() {
		return Math.max(count, actorUsers == null ? 0 : actorUsers.size());
	}

	public ContentType getActedOnContentType() {
		return ContentType.fromValue(actedOnContentType);
	}

	public String getActedOnContentHandle() {
		return actedOnContentHandle;
	}

	public String getActedOnContentText() {
		return actedOnContentText;
	}

	public BlobType getActedOnContentBlobType() {
		return BlobType.fromValue(actedOnContentBlobType);
	}

	public String getActedOnContentBlobUrl() {
		return actedOnContentBlobUrl;
	}

	public UserCompactView getActedOnUser() {
		return actedOnUser;
	}

	public AppCompactView getApp() {
		return app;
	}

	@Override
	public long getElapsedSeconds() {
		return TimeUtils.elapsedSeconds(createdTime);
	}

	public void setActorUsers(List<UserCompactView> actors) {
		this.actorUsers = actors;
	}
}
