/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher;

import com.microsoft.embeddedsocial.autorest.models.PublisherType;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.model.CommentFeedType;
import com.microsoft.embeddedsocial.fetcher.base.RequestType;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.content.comments.GetCommentFeedRequest;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicNameRequest;
import com.microsoft.embeddedsocial.server.model.view.TopicView;

public class CommentFeedFetcherFromTopicName extends CommentFeedFetcher {

    private String topicName;
    private PublisherType publisherType;

    public CommentFeedFetcherFromTopicName(CommentFeedType feedType, String topicName, PublisherType publisherType) {
        this.topicName = topicName;
        this.publisherType = publisherType;
        commentFeedRequestExecutor = new BatchDataRequestExecutor<>(
                contentService::getCommentFeed,
                () -> new GetCommentFeedRequestForTopicName(feedType)
        );
    }

    public synchronized String getTopicHandle() {
        if (topicHandle == null) {
            // topic handle is not known -- resolve it from topic name
            try {
                final GetTopicNameRequest request = new GetTopicNameRequest(topicName, publisherType);
                topicHandle = contentService.getTopicName(request);
            } catch (NetworkRequestException e) {
                setErrorCause(e);
                DebugLog.logException(e);
            }
        }
        return topicHandle;
    }

    @Override
    protected TopicView readTopic(RequestType requestType) throws Exception {
        // ensure the topic handle is known
        getTopicHandle();
        return super.readTopic(requestType);
    }


    class GetCommentFeedRequestForTopicName extends GetCommentFeedRequest {
        public GetCommentFeedRequestForTopicName(CommentFeedType commentFeedType) {
            super(commentFeedType, null);
            setTopicHandle(CommentFeedFetcherFromTopicName.this.getTopicHandle());
        }
    }
}
