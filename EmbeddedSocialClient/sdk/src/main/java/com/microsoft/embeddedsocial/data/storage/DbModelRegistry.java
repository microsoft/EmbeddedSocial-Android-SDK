/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

import com.microsoft.embeddedsocial.data.model.AddPostData;
import com.microsoft.embeddedsocial.data.model.DiscussionItem;
import com.microsoft.embeddedsocial.data.storage.model.CommentFeedRelation;
import com.microsoft.embeddedsocial.data.storage.model.EditedTopic;
import com.microsoft.embeddedsocial.data.storage.model.ReportContentOperation;
import com.microsoft.embeddedsocial.data.storage.model.TopicFeedRelation;
import com.microsoft.embeddedsocial.data.storage.model.UserAccountBinding;
import com.microsoft.embeddedsocial.data.storage.model.UserFeedRelation;
import com.microsoft.embeddedsocial.data.storage.model.UserRelationOperation;
import com.microsoft.embeddedsocial.data.storage.trigger.ISqlTrigger;
import com.microsoft.embeddedsocial.data.storage.trigger.consistency.ContentTriggers;
import com.microsoft.embeddedsocial.data.storage.trigger.consistency.NotificationTriggers;
import com.microsoft.embeddedsocial.data.storage.trigger.consistency.UserActionTriggers;
import com.microsoft.embeddedsocial.data.storage.trigger.consistency.UserRelationTriggers;
import com.microsoft.embeddedsocial.server.model.view.ActivityView;
import com.microsoft.embeddedsocial.server.model.view.AppCompactView;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;
import com.microsoft.embeddedsocial.server.model.view.ThirdPartyAccountView;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.server.model.view.UserAccountView;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.server.model.view.UserProfileView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Holds the list of DB model classes.
 */
public final class DbModelRegistry {

	private static final Set<Class<?>> MODEL_CLASSES = new HashSet<>();
	private static final List<ISqlTrigger> TRIGGERS = new ArrayList<>();

	private DbModelRegistry() {  }

	static {
		registerDbModel(AddPostData.class);
		registerDbModel(UserCompactView.class);
		registerDbModel(UserProfileView.class);
		registerDbModel(AppCompactView.class);
		registerDbModel(TopicView.class);
		registerDbModel(TopicFeedRelation.class);
		registerDbModel(CommentFeedRelation.class);
		registerDbModel(UserActionCache.LikeChangedAction.class);
		registerDbModel(UserActionCache.PinChangedAction.class);
		registerDbModel(UserActionCache.ContentRemovedAction.class);
		registerDbModel(UserActionCache.HideTopicAction.class);
		registerDbModel(UserFeedRelation.class);
		registerDbModel(UserRelationOperation.class);
		registerDbModel(CommentView.class);
		registerDbModel(ReplyView.class);
		registerDbModel(SearchHistory.HistoryEntry.class);
		registerDbModel(SearchHistory.StoredHashtag.class);
		registerDbModel(ActivityView.class);
		registerDbModel(ActivityCache.ActivityActor.class);
		registerDbModel(ActivityCache.ActivityFeed.class);
		registerDbModel(UserAccountView.class);
		registerDbModel(ThirdPartyAccountView.class);
		registerDbModel(UserAccountBinding.class);
		registerDbModel(ReportContentOperation.class);
		registerDbModel(DiscussionItem.class);
		registerDbModel(EditedTopic.class);
	}

	static {
		registerTriggers(ContentTriggers.TRIGGERS);
		registerTriggers(UserActionTriggers.TRIGGERS);
		registerTriggers(NotificationTriggers.TRIGGERS);
		registerTriggers(UserRelationTriggers.OPERATION_CLEANUP_TRIGGERS);
		registerTriggers(UserRelationTriggers.CONSISTENCY_TRIGGERS);
	}

	/**
	 * Registers a DB model class.
	 * @param modelClass    DB model class
	 */
	public static void registerDbModel(Class<?> modelClass) {
		MODEL_CLASSES.add(modelClass);
	}

	/**
	 * Gets all registered DB model classes.
	 * @return  set of registered DB model classes.
	 */
	public static Set<Class<?>> getRegisteredModels() {
		return Collections.unmodifiableSet(MODEL_CLASSES);
	}

	/**
	 * Registers DB triggers.
	 * @param triggers  the triggers to register
	 */
	public static void registerTriggers(ISqlTrigger... triggers) {
		TRIGGERS.addAll(Arrays.asList(triggers));
	}

	/**
	 * Gets all registered DB triggers.
	 * @return  DB triggers.
	 */
	public static List<ISqlTrigger> getRegisteredTriggers() {
		return Collections.unmodifiableList(TRIGGERS);
	}
}
