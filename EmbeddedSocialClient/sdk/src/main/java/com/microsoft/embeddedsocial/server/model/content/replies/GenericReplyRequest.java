/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.content.replies;

import com.microsoft.embeddedsocial.server.model.UserRequest;

public class GenericReplyRequest extends UserRequest {
    protected final String replyHandle;

    public GenericReplyRequest(String replyHandle) {
        this.replyHandle = replyHandle;
    }

    public String getReplyHandle() {
        return replyHandle;
    }
}
