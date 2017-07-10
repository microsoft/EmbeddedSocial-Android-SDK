/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util.menu;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.storage.UserActionProxy;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.ui.activity.EditPostActivity;
import com.microsoft.embeddedsocial.ui.util.ContentUpdateHelper;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.sdk.IReportHandler;
import com.microsoft.embeddedsocial.service.IntentExtras;

/**
 * Menu listener for the topic layout
 */
public class TopicContextMenuClickListener extends ContextMenuClickListener {

	private final TopicView topic;
	private final UserActionProxy userActionProxy;

	public TopicContextMenuClickListener(Context context, TopicView topic) {
		super(context, topic.getUser(), topic.getHandle());
		this.topic = topic;
		userActionProxy = new UserActionProxy(context);
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		if (super.onMenuItemClick(item)) {
			return true;
		}

		int i = item.getItemId();
		if (i == R.id.es_actionReportPost) {
			ContentUpdateHelper.startContentReport(context, contentHandle, ContentType.TOPIC);
			return true;
		} else if (i == R.id.es_actionRemove) {
			ContentUpdateHelper.launchRemoveTopic(context, topic);
			return true;
		} else if (i == R.id.es_actionEdit) {
			Intent intent = new Intent(context, EditPostActivity.class);
			intent.putExtra(IntentExtras.TOPIC_EXTRA, topic);
			context.startActivity(intent);
			return true;
		} else if (i == R.id.es_actionHideTopic) {
			userActionProxy.hideTopic(topic.getHandle());
			return true;
		} else if (i == R.id.es_reportCustom) {
			IReportHandler reportHandler = GlobalObjectRegistry.getObject(IReportHandler.class);
			try {
				reportHandler.generateReport(context, topic);
			} catch (NullPointerException e) {
				DebugLog.logException(e);
			}
			return true;
		} else {
			return false;
		}
	}
}
