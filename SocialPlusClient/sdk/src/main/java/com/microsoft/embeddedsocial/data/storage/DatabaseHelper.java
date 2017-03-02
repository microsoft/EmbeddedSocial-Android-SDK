/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.model.AddPostData;
import com.microsoft.embeddedsocial.data.storage.model.UserRelationOperation;
import com.microsoft.embeddedsocial.data.storage.trigger.ISqlTrigger;
import com.microsoft.embeddedsocial.server.model.view.AppCompactView;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.server.model.view.UserAccountView;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.data.storage.exception.FatalDatabaseException;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;
import com.microsoft.embeddedsocial.server.model.view.UserProfileView;

import java.sql.SQLException;
import java.util.Set;

/**
 * ORMLite-based database helper managing DB table creation and upgrade.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final int DB_VERSION = 17;
	private static final String DB_NAME = "local_content";

	private Dao<TopicView, String> topicDao;
	private Dao<CommentView, String> commentDao;
	private Dao<ReplyView, String> replyDao;
	private Dao<UserCompactView, String> userDao;
	private Dao<UserProfileView, String> userProfileDao;
	private Dao<AddPostData, Integer> postDao;
	private Dao<AppCompactView, String> appDao;
	private Dao<UserRelationOperation, Integer> userOperationDao;
	private Dao<UserAccountView, String> userAccountDao;

	/**
	 * Used by ORMLite reflection.
	 */
	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		try {
			this.topicDao = getDao(TopicView.class);
			this.commentDao = getDao(CommentView.class);
			this.replyDao = getDao(ReplyView.class);
			this.userDao = getDao(UserCompactView.class);
			this.userProfileDao = getDao(UserProfileView.class);
			this.postDao = getDao(AddPostData.class);
			this.appDao = getDao(AppCompactView.class);
			this.userOperationDao = getDao(UserRelationOperation.class);
			this.userAccountDao = getDao(UserAccountView.class);
		} catch (SQLException e) {
			DebugLog.logException(e);
			throw new FatalDatabaseException(e);
		}
	}

	public Dao<UserAccountView, String> getUserAccountDao() {
		return userAccountDao;
	}

	public Dao<UserCompactView, String> getUserDao() {
		return userDao;
	}

	public Dao<ReplyView, String> getReplyDao() {
		return replyDao;
	}

	public Dao<TopicView, String> getTopicDao() {
		return topicDao;
	}

	public Dao<CommentView, String> getCommentDao() {
		return commentDao;
	}

	public Dao<AddPostData, Integer> getPostDao() {
		return postDao;
	}

	public Dao<AppCompactView, String> getAppDao() {
		return appDao;
	}

	public Dao<UserRelationOperation, Integer> getUserOperationDao() {
		return userOperationDao;
	}

	public Dao<UserProfileView, String> getUserProfileDao() {
		return userProfileDao;
	}

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		try {
			createAllTables(connectionSource);
			createAllTriggers(database);
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	private void createAllTriggers(SQLiteDatabase database) {
		for (ISqlTrigger trigger : DbModelRegistry.getRegisteredTriggers()) {
			database.execSQL(trigger.toSqlCreateStatement());
		}
	}

	/**
	 * Deletes all data from the DB.
	 */
	public void clearData() {
		Set<Class<?>> registeredModels = DbModelRegistry.getRegisteredModels();
		for (Class<?> registeredModel : registeredModels) {
			try {
				TableUtils.clearTable(connectionSource, registeredModel);
			} catch (SQLException e) {
				DebugLog.logException(e);
			}
		}
	}

	private void createAllTables(ConnectionSource connectionSource) throws SQLException {
		Set<Class<?>> registeredModels = DbModelRegistry.getRegisteredModels();
		for (Class<?> registeredModel : registeredModels) {
			TableUtils.createTableIfNotExists(connectionSource, registeredModel);
		}
	}

	private void dropAllTables() throws SQLException {
		Set<Class<?>> registeredModels = DbModelRegistry.getRegisteredModels();
		for (Class<?> registeredModel : registeredModels) {
			TableUtils.dropTable(getConnectionSource(), registeredModel, true);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			dropAllTables();
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
		onCreate(database, connectionSource);
	}
}
