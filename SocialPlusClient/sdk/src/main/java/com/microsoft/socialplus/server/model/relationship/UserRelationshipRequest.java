/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.relationship;

import com.microsoft.socialplus.server.model.UserRequest;

public class UserRelationshipRequest extends UserRequest {

	protected final String relationshipUserHandle;

	public UserRelationshipRequest(String relationshipUserHandle) {
		this.relationshipUserHandle = relationshipUserHandle;
	}

	public String getRelationshipUserHandle() {
		return relationshipUserHandle;
	}
}
