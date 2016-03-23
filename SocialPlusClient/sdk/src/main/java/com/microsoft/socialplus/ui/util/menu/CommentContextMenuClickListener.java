/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.util.menu;

import android.content.Context;
import android.view.MenuItem;

import com.microsoft.socialplus.autorest.models.ContentType;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.model.view.CommentView;
import com.microsoft.socialplus.ui.util.ContentUpdateHelper;

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
		if (i == R.id.sp_actionReportComment) {
			ContentUpdateHelper.startContentReport(context, contentHandle, ContentType.COMMENT);
			return true;
		} else if (i == R.id.sp_actionRemove) {
			ContentUpdateHelper.launchRemoveComment(context, comment);
			return true;
		} else {
			return false;
		}
	}
}
