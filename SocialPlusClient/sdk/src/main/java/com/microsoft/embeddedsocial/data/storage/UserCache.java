/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

import android.text.TextUtils;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.storage.model.UserAccountBinding;
import com.microsoft.embeddedsocial.data.storage.model.UserFeedRelation;
import com.microsoft.embeddedsocial.data.storage.model.UserRelationOperation;
import com.microsoft.embeddedsocial.data.storage.syncadapter.FollowUserSyncAdapter;
import com.microsoft.embeddedsocial.data.storage.syncadapter.GeneralUserRelationSyncAdapter;
import com.microsoft.embeddedsocial.data.storage.transaction.DbTransaction;
import com.microsoft.embeddedsocial.server.model.FeedUserRequest;
import com.microsoft.embeddedsocial.server.model.UsersListResponse;
import com.microsoft.embeddedsocial.server.model.like.GetLikeFeedRequest;
import com.microsoft.embeddedsocial.server.model.relationship.GetFollowFeedRequest;
import com.microsoft.embeddedsocial.server.model.view.ThirdPartyAccountView;
import com.microsoft.embeddedsocial.server.model.view.UserAccountView;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.server.model.view.UserProfileView;
import com.microsoft.embeddedsocial.server.sync.ISynchronizable;
import com.microsoft.embeddedsocial.data.storage.exception.FatalDatabaseException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Caches data of app users.
 */
public class UserCache {

	public static final String NO_HANDLE = "";

	private final Dao<UserCompactView, String> userDao;
	private final Dao<UserProfileView, String> userProfileDao;
	private final Dao<UserRelationOperation, Integer> userOperationDao;
	private final Dao<UserAccountView, String> userAccountDao;
	private Dao<UserFeedRelation, Integer> userFeedDao;
	private Dao<UserAccountBinding, Integer> userAccountBindingDao;
	private Dao<ThirdPartyAccountView, String> thirdPartyAccountDao;

	public UserCache() {
		DatabaseHelper helper = GlobalObjectRegistry.getObject(DatabaseHelper.class);
		userDao = helper.getUserDao();
		userProfileDao = helper.getUserProfileDao();
		userOperationDao = helper.getUserOperationDao();
		userAccountDao = helper.getUserAccountDao();
		try {
			userFeedDao = helper.getDao(UserFeedRelation.class);
			userAccountBindingDao = helper.getDao(UserAccountBinding.class);
			thirdPartyAccountDao = helper.getDao(ThirdPartyAccountView.class);
		} catch (SQLException e) {
			DebugLog.logException(e);
			throw new FatalDatabaseException(e);
		}
	}

	public void storeUserFeed(GetFollowFeedRequest request, UserFeedType feedType, UsersListResponse response)
		throws SQLException {

		storeFeed(request, feedType, request.getQueryUserHandle(), response.getData());
	}

	public void storeUserFeed(FeedUserRequest request, UserFeedType feedType, UsersListResponse response)
		throws SQLException {

		storeFeed(request, feedType, NO_HANDLE, response.getData());
	}

	public void storeLikeFeed(GetLikeFeedRequest request, UsersListResponse response) throws SQLException {
		storeFeed(request, UserFeedType.LIKED, request.getContentHandle(), response.getData());
	}

	public UsersListResponse getResponse(UserFeedType feedType) throws SQLException {
		return new UsersListResponse(getFeed(feedType, NO_HANDLE));
	}

	public UsersListResponse getResponse(UserFeedType feedType, String query) throws SQLException {
		return new UsersListResponse(getFeed(feedType, query));
	}

	public void deleteOperation(UserRelationOperation operation) {
		try {
			DbTransaction.performTransaction(
				userOperationDao,
				() -> userOperationDao.delete(operation)
			);
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	private void deleteFeed(UserFeedType feedType, String queriedUserHandle) throws SQLException {
		DbTransaction.performTransaction(userFeedDao, () -> {
			String query = queriedUserHandle != null ? queriedUserHandle : NO_HANDLE;
			DeleteBuilder<UserFeedRelation, Integer> deleteBuilder = userFeedDao.deleteBuilder();
			deleteBuilder.where()
				.eq(DbSchemas.UserFeeds.FEED_TYPE, feedType)
				.and().eq(DbSchemas.UserFeeds.QUERIED_USER_HANDLE, query);
			deleteBuilder.delete();
		});
	}

	private List<UserCompactView> getFeed(UserFeedType feedType, String queriedUserHandle)
		throws SQLException {

		if (queriedUserHandle == null) {
			queriedUserHandle = NO_HANDLE;
		}
		List<UserCompactView> result = new ArrayList<>();

		List<UserFeedRelation> feed = userFeedDao.queryBuilder()
				.where().eq(DbSchemas.UserFeeds.FEED_TYPE, feedType)
				.and().eq(DbSchemas.UserFeeds.QUERIED_USER_HANDLE, queriedUserHandle)
				.query();

		for (UserFeedRelation userReference : feed) {
			result.add(userDao.queryForId(userReference.getUserHandle()));
		}

		return result;
	}

	private void storeFeed(FeedUserRequest request, UserFeedType feedType,
	                       String queriedHandle, List<UserCompactView> users) throws SQLException {

		DbTransaction.performTransaction(userDao, () -> {
			if (TextUtils.isEmpty(request.getCursor())) {
				deleteFeed(feedType, queriedHandle);
			}

			for (UserCompactView user : users) {
				userDao.createOrUpdate(user);
				UserFeedRelation relation = new UserFeedRelation(feedType, user.getHandle(),
					queriedHandle != null ? queriedHandle : NO_HANDLE);
				userFeedDao.create(relation);
			}
		});
	}

	public List<ISynchronizable> getPendingUserRelationOperations() {
		List<ISynchronizable> result = new ArrayList<>();

		List<UserRelationOperation> operations = getUserRelationOperations();
		for (UserRelationOperation operation : operations) {
			ISynchronizable adapter;
			if (operation.getAction() == UserRelationAction.FOLLOW) {
				adapter = new FollowUserSyncAdapter(operation, this);
			} else {
				adapter = new GeneralUserRelationSyncAdapter(operation, this);
			}
			result.add(adapter);
		}

		return result;
	}

	List<UserRelationOperation> getUserRelationOperations() {
		try {
			return userOperationDao.queryForAll();
		} catch (SQLException e) {
			DebugLog.logException(e);
			return Collections.emptyList();
		}
	}

	void followUser(String userHandle) {
		storeUserOperation(new UserRelationOperation(UserRelationAction.FOLLOW, userHandle));
	}

	void unfollowUser(String userHandle) {
		storeUserOperation(new UserRelationOperation(UserRelationAction.UNFOLLOW, userHandle));
	}

	void blockUser(String userHandle) {
		storeUserOperation(new UserRelationOperation(UserRelationAction.BLOCK, userHandle));
	}

	void unblockUser(String userHandle) {
		storeUserOperation(new UserRelationOperation(UserRelationAction.UNBLOCK, userHandle));
	}

	void acceptUser(String userHandle) {
		storeUserOperation(new UserRelationOperation(UserRelationAction.ACCEPT, userHandle));
	}

	void rejectUser(String userHandle) {
		storeUserOperation(new UserRelationOperation(UserRelationAction.REJECT, userHandle));
	}

	private void storeUserOperation(UserRelationOperation operation) {
		try {
			DbTransaction.performTransaction(userOperationDao,
				() -> userOperationDao.create(operation));
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	public void storeUser(UserCompactView user) {
		try {
			DbTransaction.performTransaction(
				userDao,
				() -> userDao.createOrUpdate(user)
			);
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	public void storeUserProfile(UserProfileView profile) {
		try {
			DbTransaction.performTransaction(
				userProfileDao,
				() -> userProfileDao.createOrUpdate(profile)
			);
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	public UserAccountView getUserAccount(String userHandle) throws SQLException {
		UserAccountView account = userAccountDao.queryForId(userHandle);
		List<UserAccountBinding> accountBindings = userAccountBindingDao.queryForEq(
			DbSchemas.UserFeeds.USER_HANDLE,
			userHandle
		);
		List<ThirdPartyAccountView> thirdPartyAccounts = new ArrayList<>();
		for (UserAccountBinding binding : accountBindings) {
			thirdPartyAccounts.add(thirdPartyAccountDao.queryForId(binding.getAccountHandle()));
		}
		account.setThirdPartyAccounts(thirdPartyAccounts);

		return account;
	}

	public void storeUserAccount(UserAccountView account) throws SQLException {
		DbTransaction.performTransaction(userAccountDao, () -> {

			userAccountDao.createOrUpdate(account);

			List<ThirdPartyAccountView> thirdPartyAccounts = account.getThirdPartyAccounts();
			for (ThirdPartyAccountView thirdPartyAccount : thirdPartyAccounts) {
				if (thirdPartyAccount.hasAccountHandle()) {
					thirdPartyAccountDao.createOrUpdate(thirdPartyAccount);
				}
			}

			DeleteBuilder<UserAccountBinding, Integer> deleteBuilder = userAccountBindingDao
				.deleteBuilder();
			deleteBuilder.where().eq(DbSchemas.UserFeeds.USER_HANDLE, account.getHandle());
			deleteBuilder.delete();

			for (ThirdPartyAccountView thirdPartyAccount : thirdPartyAccounts) {
				if (thirdPartyAccount.hasAccountHandle()) {
					UserAccountBinding binding = new UserAccountBinding(
						account.getHandle(),
						thirdPartyAccount.getAccountHandle()
					);
					userAccountBindingDao.createOrUpdate(binding);
				}
			}
		});
	}

	public UserProfileView getUserProfile(String userHandle) throws SQLException {
		UserProfileView profile = userProfileDao.queryForId(userHandle);

		if (profile == null) {
			throw new SQLException("user profile was not found in cache");
		}

		return profile;
	}

	public UserCompactView getUserByHandle(String userHandle) throws SQLException {
		UserCompactView user = userDao.queryForId(userHandle);

		if (user == null) {
			throw new SQLException("user " + userHandle + " was not found in cache");
		}

		return user;
	}

	/**
	 * Describes possible actions that can be done for a user.
	 */
	public enum UserRelationAction {
		FOLLOW,
		UNFOLLOW,
		BLOCK,
		UNBLOCK,
		ACCEPT,
		REJECT
	}

	/**
	 * Describes types of user feeds.
	 */
	public enum UserFeedType {

		/**
		 * Blocked users.
		 */
		BLOCKED,

		/**
		 * Users that we follow.
		 */
		FOLLOWING,

		/**
		 * Our followers.
		 */
		FOLLOWER,

		/**
		 * Friendship requests not yet approved.
		 */
		PENDING,

		/**
		 * Users that liked a certain content piece.
		 */
		LIKED
	}
}
