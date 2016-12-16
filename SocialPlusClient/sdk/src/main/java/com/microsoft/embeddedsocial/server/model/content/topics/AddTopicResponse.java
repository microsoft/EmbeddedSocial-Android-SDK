/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.content.topics;

/**
 *
 */
public class AddTopicResponse {

	private String topicHandle;

	public AddTopicResponse(String topicHandle) {
		this.topicHandle = topicHandle;
	}

	public String getTopicHandle() {
		return topicHandle;
	}
}
