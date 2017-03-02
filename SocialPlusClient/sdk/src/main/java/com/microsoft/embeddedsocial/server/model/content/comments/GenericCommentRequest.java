/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.content.comments;

import com.microsoft.embeddedsocial.server.model.UserRequest;

public class GenericCommentRequest extends UserRequest {

    protected final String commentHandle;

    public GenericCommentRequest(String commentHandle) {
        this.commentHandle = commentHandle;
    }

    public String getCommentHandle() {
        return commentHandle;
    }
}
