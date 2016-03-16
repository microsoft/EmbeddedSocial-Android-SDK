package com.microsoft.socialplus.server.model.relationship;

import com.microsoft.socialplus.server.model.FeedUserRequest;

public class GetFollowFeedRequest extends FeedUserRequest {
    private final String queryUserHandle;

    public GetFollowFeedRequest(String queryUserHandle) {
        this.queryUserHandle = queryUserHandle;
    }

    public String getQueryUserHandle() {
        return queryUserHandle;
    }
}
