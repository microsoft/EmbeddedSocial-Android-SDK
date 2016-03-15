/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model;

import com.microsoft.autorest.models.FeedResponseUserCompactView;
import com.microsoft.socialplus.server.model.view.UserCompactView;

import java.util.ArrayList;
import java.util.List;

public class UsersListResponse extends FeedUserResponse implements ListResponse<UserCompactView> {

	private List<UserCompactView> users;

	public UsersListResponse(List<UserCompactView> users) {
		this.users = users;
	}

	public UsersListResponse(FeedResponseUserCompactView feed) {
		users = new ArrayList<>();
		for (com.microsoft.autorest.models.UserCompactView feedView : feed.getData()) {
			users.add(new UserCompactView(feedView));
		}
	}

	@Override
	public List<UserCompactView> getData() {
		return users;
	}
}
