/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.transaction;

import java.sql.SQLException;

/**
 * SQL transaction operation.
 */
public interface ISqlTransaction {

	/**
	 * Performs the transaction.
	 * @throws SQLException if something goes wrong.
	 */
	void performTransaction() throws SQLException;
}
