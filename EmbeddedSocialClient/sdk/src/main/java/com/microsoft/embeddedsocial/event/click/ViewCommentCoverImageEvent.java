/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.click;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.base.event.ThreadType;
import com.microsoft.embeddedsocial.event.BaseCommonBehaviorEvent;
import com.microsoft.embeddedsocial.server.model.view.CommentView;

import androidx.fragment.app.Fragment;

/**
 * View selected cover image.
 */
@HandlingThread(ThreadType.MAIN)
public class ViewCommentCoverImageEvent extends BaseCommonBehaviorEvent {
    private final CommentView comment;

    public ViewCommentCoverImageEvent(Fragment source, CommentView comment) {
        super(source);
        this.comment = comment;
    }

    public CommentView getComment() {
        return comment;
    }
}
