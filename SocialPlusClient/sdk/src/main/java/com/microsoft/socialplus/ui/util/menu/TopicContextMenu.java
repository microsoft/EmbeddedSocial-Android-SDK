/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.util.menu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.Menu;

import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.sdk.ReportHandler;
import com.microsoft.socialplus.server.model.view.TopicView;
import com.microsoft.socialplus.ui.adapter.viewholder.TopicRenderOptions;

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
			menu.inflate(R.menu.sp_topic_pending);
		} else {
			if (isOwnTopic(topic)) {
				menu.inflate(R.menu.sp_topic_own);
			} else {
				UserContextMenuHelper.inflateUserRelationshipContextMenu(menu, topic.getUser().getFollowerStatus());
				menu.inflate(R.menu.sp_topic);
			}
			if (UserAccount.getInstance().isSignedIn() && options.shouldShowHideTopicItem()) {
				menu.inflate(R.menu.sp_topic_hide);
			}
			addCustomReportHandler(menu);
        }
		menu.setOnMenuItemClickListener(new TopicContextMenuClickListener(context, topic));
	}

	private static boolean isOwnTopic(TopicView topic) {
		return UserAccount.getInstance().isCurrentUser(topic.getUser().getHandle());
	}

    /**
     * Adds a custom report handler if one was provided
     */
	private static void addCustomReportHandler(@NonNull PopupMenu menu) {
		ReportHandler reportHandler = GlobalObjectRegistry.getObject(ReportHandler.class);
		if (reportHandler != null) {
			String displayString = reportHandler.getDisplayString();
			if (!TextUtils.isEmpty(displayString)) {
                // create an item with the a known ID and the provided title
                menu.getMenu().add(Menu.NONE, R.id.sp_reportCustom, Menu.NONE, displayString);
            }
        }
	}
}
