/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.util.menu;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import com.microsoft.socialplus.autorest.models.ContentType;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.data.storage.UserActionProxy;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.sdk.IReportHandler;
import com.microsoft.socialplus.server.model.view.TopicView;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.ui.activity.EditPostActivity;
import com.microsoft.socialplus.ui.util.ContentUpdateHelper;

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
		if (i == R.id.sp_actionReportPost) {
			ContentUpdateHelper.startContentReport(context, contentHandle, ContentType.TOPIC);
			return true;
		} else if (i == R.id.sp_actionRemove) {
			ContentUpdateHelper.launchRemoveTopic(context, topic);
			return true;
		} else if (i == R.id.sp_actionEdit) {
			Intent intent = new Intent(context, EditPostActivity.class);
			intent.putExtra(IntentExtras.TOPIC_EXTRA, topic);
			context.startActivity(intent);
			return true;
		} else if (i == R.id.sp_actionHideTopic) {
			userActionProxy.hideTopic(topic.getHandle());
			return true;
		} else if (i == R.id.sp_reportCustom) {
			IReportHandler reportHandler = GlobalObjectRegistry.getObject(IReportHandler.class);
			try {
				reportHandler.generateReport(context, topic.getFriendlyName());
			} catch (NullPointerException e) {
				DebugLog.logException(e);
			}
			return true;
		} else {
			return false;
		}
	}
}
