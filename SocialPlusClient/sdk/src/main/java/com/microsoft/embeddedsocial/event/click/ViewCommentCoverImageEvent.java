/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.click;

import com.microsoft.embeddedsocial.base.event.HandlingThread;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.base.event.AbstractEvent;
import com.microsoft.embeddedsocial.base.event.ThreadType;

/**
 * View selected cover image.
 */
@HandlingThread(ThreadType.MAIN)
public class ViewCommentCoverImageEvent extends AbstractEvent {
    private final CommentView comment;

    public ViewCommentCoverImageEvent(CommentView comment) {
        this.comment = comment;
    }

    public CommentView getComment() {
        return comment;
    }
}
