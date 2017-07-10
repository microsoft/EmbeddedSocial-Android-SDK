/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server;

/**
 * Enum holds server configuration values
 */
public enum ServerConfig {

	STAGING("http://sp-prod.cloudapp.net/api");

	private final String server;

	ServerConfig(String server) {
		this.server = server;
	}

	public String getServer() {
		return server;
	}
}
