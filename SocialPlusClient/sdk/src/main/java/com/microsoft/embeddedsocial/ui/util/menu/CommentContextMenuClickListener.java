/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util.menu;

import android.content.Context;
import android.view.MenuItem;

import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.ui.util.ContentUpdateHelper;
import com.microsoft.embeddedsocial.autorest.models.ContentType;

/**
 * Menu listener for the comment layout
 */
public class CommentContextMenuClickListener extends ContextMenuClickListener {

	private final CommentView comment;

	public CommentContextMenuClickListener(Context context, CommentView comment) {
		super(context, comment.getUser(), comment.getHandle());
		this.comment = comment;
	}

	@Override
	public boolean onMenuItemClick(MenuItem item) {
		if (super.onMenuItemClick(item)) {
			return true;
		}

		int i = item.getItemId();
		if (i == R.id.es_actionReportComment) {
			ContentUpdateHelper.startContentReport(context, contentHandle, ContentType.COMMENT);
			return true;
		} else if (i == R.id.es_actionRemove) {
			ContentUpdateHelper.launchRemoveComment(context, comment);
			return true;
		} else {
			return false;
		}
	}
}
