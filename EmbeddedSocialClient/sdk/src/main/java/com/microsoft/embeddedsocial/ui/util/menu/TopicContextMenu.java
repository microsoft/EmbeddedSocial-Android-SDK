/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util.menu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.Menu;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.autorest.models.PublisherType;
import com.microsoft.embeddedsocial.sdk.IReportHandler;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.TopicRenderOptions;

/**
 * Contains common methods for topic context menus.
 */
public class TopicContextMenu {

	/**
	 * Inflate context menu to topic properties.
	 *
	 * @param topic the topic to generate context menu for
	 */
	public static void inflateContextMenu(@NonNull Context context, @NonNull PopupMenu menu, TopicView topic, TopicRenderOptions options) {
		if (topic.isLocal()) {
			menu.inflate(R.menu.es_topic_pending);
		} else {
			if (isOwnTopic(topic)) {
				menu.inflate(R.menu.es_topic_own);
			} else {
				if (topic.getPublisherType() != PublisherType.APP &&
						GlobalObjectRegistry.getObject(Options.class).userRelationsEnabled()) {
					// Inflate the relation options if this is a user other than the signed in user and user relations are enabled
					UserContextMenuHelper.inflateUserRelationshipContextMenu(menu, topic.getUser().getFollowerStatus());
				}
				menu.inflate(R.menu.es_topic);
			}
			if (UserAccount.getInstance().isSignedIn() && options.shouldShowHideTopicItem()) {
				menu.inflate(R.menu.es_topic_hide);
			}
			addCustomReportHandler(context, menu, topic);
        }
		menu.setOnMenuItemClickListener(new TopicContextMenuClickListener(context, topic));
	}

	private static boolean isOwnTopic(TopicView topic) {
		if (topic.getPublisherType() == PublisherType.APP) {
			return false;
		}
		return UserAccount.getInstance().isCurrentUser(topic.getUser().getHandle());
	}

    /**
     * Adds a custom report handler if one was provided
     */
	private static void addCustomReportHandler(Context context, @NonNull PopupMenu menu, TopicView topic) {
		IReportHandler reportHandler = GlobalObjectRegistry.getObject(IReportHandler.class);
		if (reportHandler != null) {
			String displayString = reportHandler.getDisplayString(context, topic);
			if (!TextUtils.isEmpty(displayString)) {
                // create an item with the a known ID and the provided title
                menu.getMenu().add(Menu.NONE, R.id.es_reportCustom, Menu.NONE, displayString);
            }
        }
	}
}
