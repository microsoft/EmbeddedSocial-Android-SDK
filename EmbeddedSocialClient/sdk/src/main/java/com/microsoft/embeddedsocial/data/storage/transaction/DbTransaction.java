/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.transaction;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Helps to perform DB transactions.
 */
public class DbTransaction {

	/**
	 * Performs a transaction on the specified dao.
	 * @param dao           the dao to perform the transaction on
	 * @param transaction   the transaction to perform
	 * @throws SQLException if any exception happens during the transaction.
	 */
	public static synchronized void performTransaction(Dao<?, ?> dao, ISqlTransaction transaction)
		throws SQLException {

		try {
			dao.callBatchTasks(() -> {
				transaction.performTransaction();
				return null;
			});
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new SQLException(e);
		}
	}
}
