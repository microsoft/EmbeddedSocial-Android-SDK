/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.util.menu;

import android.content.Context;
import android.view.MenuItem;

import com.microsoft.socialplus.autorest.models.ContentType;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.model.view.ReplyView;
import com.microsoft.socialplus.ui.util.ContentUpdateHelper;

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
		if (i == R.id.sp_actionReportReply) {
			ContentUpdateHelper.startContentReport(context, contentHandle, ContentType.REPLY);
			return true;
		} else if (i == R.id.sp_actionRemove) {
			ContentUpdateHelper.launchRemoveReply(context, replyView);
			return true;
		} else {
			return false;
		}
	}
}
