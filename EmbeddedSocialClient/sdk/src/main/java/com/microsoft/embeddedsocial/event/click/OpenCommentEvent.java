/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.event.click;

import com.microsoft.embeddedsocial.event.BaseCommonBehaviorEvent;
import com.microsoft.embeddedsocial.server.model.view.CommentView;

import androidx.fragment.app.Fragment;

public class OpenCommentEvent extends BaseCommonBehaviorEvent {

    private final CommentView comment;
    private final boolean jumpToEdit;

    public OpenCommentEvent(Fragment source, CommentView comment) {
        this(source, comment, false);
    }

    public OpenCommentEvent(Fragment source, CommentView comment, boolean jumpToEdit) {
        super(source);
        this.comment = comment;
        this.jumpToEdit = jumpToEdit;
    }

    public CommentView getComment() {
        return comment;
    }

    public boolean jumpToEdit() {
        return jumpToEdit;
    }
}
