/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util.menu;

import android.content.Context;
import android.view.MenuItem;

import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;
import com.microsoft.embeddedsocial.ui.util.ContentUpdateHelper;
import com.microsoft.embeddedsocial.autorest.models.ContentType;

/**
 * Menu listener for the reply layout
 */
public class ReplyContextMenuClickListener extends ContextMenuClickListener {

	private ReplyView replyView;

	public ReplyContextMenuClickListener(Context context, ReplyView replyView) {
		super(context, replyView.getUser(), replyView.getHandle());
		this.replyView = replyView;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		if (super.onMenuItemClick(item)) {
			return true;
		}

		int i = item.getItemId();
		if (i == R.id.es_actionReportReply) {
			ContentUpdateHelper.startContentReport(context, contentHandle, ContentType.REPLY);
			return true;
		} else if (i == R.id.es_actionRemove) {
			ContentUpdateHelper.launchRemoveReply(context, replyView);
			return true;
		} else {
			return false;
		}
	}
}
