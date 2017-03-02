/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.data.storage.model.ReportContentOperation;
import com.microsoft.embeddedsocial.data.storage.syncadapter.RemoveContentActionSyncAdapter;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.autorest.models.Reason;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.storage.exception.FatalDatabaseException;
import com.microsoft.embeddedsocial.data.storage.syncadapter.HideTopicSyncAdapter;
import com.microsoft.embeddedsocial.data.storage.syncadapter.LikeStatusSyncAdapter;
import com.microsoft.embeddedsocial.data.storage.syncadapter.PinStatusSyncAdapter;
import com.microsoft.embeddedsocial.data.storage.syncadapter.ReportContentSyncAdapter;
import com.microsoft.embeddedsocial.data.storage.transaction.DbTransaction;
import com.microsoft.embeddedsocial.server.sync.ISynchronizable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores user actions such as likes/pins.
 */
public class UserActionCache {

	private Dao<PinChangedAction, Integer> pinDao;
	private Dao<LikeChangedAction, Integer> likeDao;
	private Dao<ContentRemovedAction, Integer> removeActionDao;
	private Dao<ReportContentOperation, Integer> reportContentDao;
	private Dao<HideTopicAction, Integer> hideTopicDao;

	/**
	 * Creates an instance.
	 */
	public UserActionCache() {
		DatabaseHelper helper = GlobalObjectRegistry.getObject(DatabaseHelper.class);
		try {
			pinDao = helper.getDao(PinChangedAction.class);
			likeDao = helper.getDao(LikeChangedAction.class);
			removeActionDao = helper.getDao(ContentRemovedAction.class);
			reportContentDao = helper.getDao(ReportContentOperation.class);
			hideTopicDao = helper.getDao(HideTopicAction.class);
		} catch (SQLException e) {
			DebugLog.logException(e);
			throw new FatalDatabaseException(e);
		}
	}

	/**
	 * Sets 'pinned' status for a topic.
	 * @param topicHandle   topic handle
	 * @param liked         pinned status (true if pinned)
	 */
	void setPinStatus(String topicHandle, boolean liked) {
		try {
			pinDao.create(new PinChangedAction(topicHandle, liked));
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	/**
	 * Gets synchronizables for pending pin actions.
	 * @return  list of {@linkplain ISynchronizable}
	 */
	public List<ISynchronizable> getPendingPinActions() {
		List<ISynchronizable> result = new ArrayList<>();
		List<PinChangedAction> pinActions = queryForAllItems(pinDao);

		for (PinChangedAction pinAction : pinActions) {
			result.add(new PinStatusSyncAdapter(pinDao, pinAction));
		}

		return result;
	}

	/**
	 * Gets synchronizables for pending like actions.
	 * @return  list of {@linkplain ISynchronizable}
	 */
	public List<ISynchronizable> getPendingLikeActions() {
		List<ISynchronizable> result = new ArrayList<>();
		List<LikeChangedAction> likeActions = queryForAllItems(likeDao);

		for (LikeChangedAction likeAction : likeActions) {
			result.add(new LikeStatusSyncAdapter(likeDao, likeAction));
		}

		return result;
	}

	/**
	 * Gets synchronizables for pending content (topics, comments, replies) removal actions.
	 * @return  list of {@linkplain ISynchronizable}
	 */
	public List<ISynchronizable> getPendingContentRemovalActions() {
		List<ContentRemovedAction> actions = queryForAllItems(removeActionDao);
		List<ISynchronizable> result = new ArrayList<>();

		for (ContentRemovedAction action : actions) {
			result.add(RemoveContentActionSyncAdapter.createAdapter(removeActionDao, action));
		}

		return result;
	}

	/**
	 * Gets synchronizables for pending content reporting actions.
	 * @return  list of {@linkplain ISynchronizable}.
	 */
	public List<ISynchronizable> getPendingReportContentActions() {
		List<ReportContentOperation> operations = queryForAllItems(reportContentDao);
		List<ISynchronizable> result = new ArrayList<>();

		for (ReportContentOperation operation : operations) {
			result.add(new ReportContentSyncAdapter(operation, reportContentDao));
		}

		return result;
	}

	/**
	 * Gets synchronizables for pending hide topic actions.
	 * @return  list of {@linkplain ISynchronizable}.
	 */
	public List<ISynchronizable> getPendingHideTopicActions() {
		List<HideTopicAction> actions = queryForAllItems(hideTopicDao);
		List<ISynchronizable> result = new ArrayList<>();

		for (HideTopicAction action : actions) {
			result.add(new HideTopicSyncAdapter(action, hideTopicDao));
		}

		return result;
	}

	/**
	 * Sets 'liked' status for a topic.
	 * @param contentHandle liked content handle
	 * @param contentType   content type
	 * @param liked         liked status
	 */
	void setLikeStatus(String contentHandle, ContentType contentType, boolean liked) {
		try {
			likeDao.create(new LikeChangedAction(contentHandle, contentType, liked));
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	/**
	 * Adds an action to remove some content.
	 * @param contentHandle content handle
	 * @param contentType   content type
	 */
	void addContentRemovalAction(String contentHandle, ContentType contentType) {
		try {
			removeActionDao.create(new ContentRemovedAction(contentHandle, contentType));
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	/**
	 * Reports a user.
	 * @param userHandle    user handle
	 * @param reason        reporting reason
	 */
	void reportUser(String userHandle, Reason reason) {
		try {
			DbTransaction.performTransaction(
				reportContentDao,
				() -> reportContentDao.create(ReportContentOperation.forUser(userHandle, reason))
			);
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	/**
	 * Reports a piece of content.
	 * @param contentHandle handle of content piece
	 * @param contentType   type of reported content
	 * @param reason        reporting reason
	 */
	void reportContent(String contentHandle, ContentType contentType, Reason reason) {
		try {
			DbTransaction.performTransaction(
				reportContentDao,
				() -> reportContentDao.create(ReportContentOperation.forContent(
					contentHandle, contentType, reason))
			);
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	/**
	 * Hides a topic.
	 * @param topicHandle   handle of the topic to hide
	 */
	void hideTopic(String topicHandle) {
		try {
			DbTransaction.performTransaction(
				hideTopicDao,
				() -> hideTopicDao.create(new HideTopicAction(topicHandle))
			);
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	private <T> List<T> queryForAllItems(Dao<T, Integer> dao) {
		List<T> result;

		try {
			result = dao.queryForAll();
		} catch (SQLException e) {
			result = Collections.emptyList();
		}

		return result;
	}

	/**
	 * Is used to store 'pin' actions.
	 */
	@DatabaseTable(tableName = DbSchemas.PinStatus.TABLE_NAME)
	@SuppressWarnings("unused")
	public static class PinChangedAction {

		@DatabaseField(generatedId = true)
		private int id;

		@DatabaseField(unique = true, columnName = DbSchemas.PinStatus.TOPIC_HANDLE)
		private String topicHandle;

		@DatabaseField(columnName = DbSchemas.PinStatus.PIN_STATUS)
		private boolean status;

		/**
		 * For ORM.
		 */
		PinChangedAction() {  }

		PinChangedAction(String topicHandle, boolean status) {
			this.topicHandle = topicHandle;
			this.status = status;
		}

		public boolean getStatus() {
			return status;
		}

		public String getTopicHandle() {
			return topicHandle;
		}
	}

	/**
	 * Is used to store 'like' actions.
	 */
	@DatabaseTable(tableName = DbSchemas.LikeStatus.TABLE_NAME)
	@SuppressWarnings("unused")
	public static class LikeChangedAction {

		@DatabaseField(generatedId = true)
		private int id;

		@DatabaseField(unique = true, columnName = DbSchemas.LikeStatus.CONTENT_HANDLE)
		private String contentHandle;

		@DatabaseField(columnName = DbSchemas.LikeStatus.CONTENT_TYPE)
		private ContentType contentType;

		@DatabaseField(columnName = DbSchemas.LikeStatus.STATUS)
		private boolean status;

		/**
		 * For ORM.
		 */
		LikeChangedAction() {  }

		LikeChangedAction(String contentHandle, ContentType contentType, boolean status) {
			this.contentHandle = contentHandle;
			this.contentType = contentType;
			this.status = status;
		}

		public String getContentHandle() {
			return contentHandle;
		}

		public ContentType getContentType() {
			return contentType;
		}

		public boolean getStatus() {
			return status;
		}
	}

	@DatabaseTable
	@SuppressWarnings("unused")
	public static class ContentRemovedAction {

		@DatabaseField(generatedId = true)
		private int id;

		@DatabaseField(columnName = DbSchemas.RemoveActions.CONTENT_TYPE)
		private ContentType contentType;

		@DatabaseField(columnName = DbSchemas.RemoveActions.CONTENT_HANDLE)
		private String contentHandle;

		/**
		 * Used by ORM.
		 */
		ContentRemovedAction() {  }

		ContentRemovedAction(String contentHandle, ContentType contentType) {
			this.contentHandle = contentHandle;
			this.contentType = contentType;
		}

		public ContentType getContentType() {
			return contentType;
		}

		public String getContentHandle() {
			return contentHandle;
		}
	}

	@SuppressWarnings("unused")
	@DatabaseTable(tableName = DbSchemas.HideTopicAction.TABLE_NAME)
	public static class HideTopicAction {

		@DatabaseField(generatedId = true)
		private int id;

		@DatabaseField(columnName = DbSchemas.Topics.TOPIC_HANDLE)
		private String topicHandle;

		HideTopicAction() {  }

		private HideTopicAction(String topicHandle) {
			this.topicHandle = topicHandle;
		}

		public String getTopicHandle() {
			return topicHandle;
		}
	}
}
