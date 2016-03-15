/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model;

public class FeedUserRequest extends UserRequest {

	public static final int DEFAULT_BATCH_SIZE = 20;

	private String cursor;
	private int batchSize = DEFAULT_BATCH_SIZE;

	public String getCursor() {
		return cursor;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public void setCursor(String continuationKey) {
		this.cursor = continuationKey;
	}

}
