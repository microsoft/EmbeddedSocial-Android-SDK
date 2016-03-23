/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.content.topics;

import com.microsoft.socialplus.autorest.models.FeedResponseTopicView;
import com.microsoft.socialplus.server.model.FeedUserResponse;
import com.microsoft.socialplus.server.model.ListResponse;
import com.microsoft.socialplus.server.model.view.TopicView;

import java.util.ArrayList;
import java.util.List;

public class TopicsListResponse extends FeedUserResponse implements ListResponse<TopicView> {

	private List<TopicView> topics;

	public TopicsListResponse(List<TopicView> topics) {
		this.topics = topics;
	}

	public TopicsListResponse(FeedResponseTopicView response) {
		topics = new ArrayList<>();
		for (com.microsoft.socialplus.autorest.models.TopicView topic : response.getData()) {
			topics.add(new TopicView(topic));
		}
	}

	@Override
	public List<TopicView> getData() {
		return topics;
	}
}
