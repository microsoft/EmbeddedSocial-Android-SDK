/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.trigger;

/**
 * Represents SQL trigger.
 */
public interface ISqlTrigger {

	/**
	 * Gets SQL create statement for the trigger
	 * @return  SQL statement.
	 */
	String toSqlCreateStatement();
}
