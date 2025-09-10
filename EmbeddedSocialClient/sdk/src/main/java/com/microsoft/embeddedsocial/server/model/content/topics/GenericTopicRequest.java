/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.content.topics;

import com.microsoft.embeddedsocial.server.model.UserRequest;

public class GenericTopicRequest extends UserRequest {

    protected final String topicHandle;

    public GenericTopicRequest(String topicHandle) {
        this.topicHandle = topicHandle;
    }

    public String getTopicHandle() {
        return topicHandle;
    }
}
