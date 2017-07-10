/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model;

import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.autorest.models.FeedResponseUserCompactView;
import com.microsoft.embeddedsocial.autorest.models.FeedResponseUserProfileView;
import com.microsoft.embeddedsocial.autorest.models.UserProfileView;

import java.util.ArrayList;
import java.util.List;

public class UsersListResponse extends FeedUserResponse implements ListResponse<UserCompactView> {

	private List<UserCompactView> users;

	public UsersListResponse(List<UserCompactView> users) {
		this.users = users;
	}

	public UsersListResponse(FeedResponseUserCompactView feed) {
		users = new ArrayList<>();
		for (com.microsoft.embeddedsocial.autorest.models.UserCompactView feedView : feed.getData()) {
			users.add(new UserCompactView(feedView));
		}
	}

	public UsersListResponse(FeedResponseUserProfileView feed) {
		users = new ArrayList<>();
		for (UserProfileView feedView : feed.getData()) {
			users.add(new UserCompactView(feedView));
		}
	}

	@Override
	public List<UserCompactView> getData() {
		return users;
	}
}
