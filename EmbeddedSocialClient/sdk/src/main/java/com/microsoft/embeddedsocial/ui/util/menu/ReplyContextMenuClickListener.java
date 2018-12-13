/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util.menu;

import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;
import com.microsoft.embeddedsocial.ui.util.ContentUpdateHelper;

import android.view.MenuItem;

import androidx.fragment.app.Fragment;

/**
 * Menu listener for the reply layout
 */
public class ReplyContextMenuClickListener extends ContextMenuClickListener {

    private ReplyView replyView;

    public ReplyContextMenuClickListener(Fragment fragment, ReplyView replyView) {
        super(fragment, replyView.getUser(), replyView.getHandle());
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
