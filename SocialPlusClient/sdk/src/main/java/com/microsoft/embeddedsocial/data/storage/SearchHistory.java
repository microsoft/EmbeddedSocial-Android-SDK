/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.expression.Template;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.model.SearchType;
import com.microsoft.embeddedsocial.data.storage.transaction.DbTransaction;
import com.microsoft.embeddedsocial.data.storage.exception.FatalDatabaseException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores search history for the current user.
 */
public class SearchHistory {

	private static final long HISTORY_SIZE = 10;

	private static final String DELETE_OLDEST_ENTRY_STATEMENT
		= new Template("delete from ${history} where ${id} = " +
		"(select min(${id}) from ${history} where ${type} = ?)")
		.var("history", DbSchemas.SearchHistory.TABLE_NAME)
		.var("id", DbSchemas.SearchHistory.ID)
		.var("type", DbSchemas.SearchHistory.SEARCH_TYPE)
		.render();

	private Dao<HistoryEntry, Integer> historyDao;
	private Dao<StoredHashtag, Integer> hashtagDao;

	/**
	 * Creates an instance.
	 */
	public SearchHistory() {
		DatabaseHelper helper = GlobalObjectRegistry.getObject(DatabaseHelper.class);
		try {
			historyDao = helper.getDao(HistoryEntry.class);
			hashtagDao = helper.getDao(StoredHashtag.class);
		} catch (SQLException e) {
			DebugLog.logException(e);
			throw new FatalDatabaseException(e);
		}
	}

	/**
	 * Adds search history entry.
	 * @param request   user request
	 */
	public void addEntry(String request, SearchType searchType) {
		try {
			deleteDuplicates(request, searchType);
			historyDao.create(new HistoryEntry(request, searchType));
			enforceHistorySizeLimit();
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	private void deleteDuplicates(String request, SearchType searchType) throws SQLException {
		DeleteBuilder<HistoryEntry, Integer> deleteBuilder = historyDao.deleteBuilder();
		deleteBuilder.where().eq(DbSchemas.SearchHistory.QUERY_TEXT, request).and().eq(DbSchemas.SearchHistory.SEARCH_TYPE, searchType);
		deleteBuilder.delete();
	}

	private void enforceHistorySizeLimit() throws SQLException {
		for (SearchType searchType : SearchType.values()) {
			QueryBuilder<HistoryEntry, Integer> query = historyDao.queryBuilder();
			query.where().eq(DbSchemas.SearchHistory.SEARCH_TYPE, searchType);
			query.setCountOf(true);
			DbTransaction.performTransaction(historyDao, () -> {
				if (historyDao.countOf(query.prepare()) > HISTORY_SIZE) {
					historyDao.executeRaw(DELETE_OLDEST_ENTRY_STATEMENT, searchType.name());
				}
			});
		}
	}

	/**
	 * Gets the list of all stored user search requests.
	 * @return  list of search requests.
	 */
	public List<String> getSearchRequests(SearchType searchType) {
		List<String> result = new ArrayList<>();
		try {
			List<HistoryEntry> query = historyDao.queryBuilder()
				.orderBy(DbSchemas.SearchHistory.ID, false)
				.where().eq(DbSchemas.SearchHistory.SEARCH_TYPE, searchType)
				.query();
			for (HistoryEntry historyEntry : query) {
				result.add(historyEntry.text);
			}
		} catch (SQLException e) {
			DebugLog.logException(e);
		}

		return result;
	}

	/**
	 * Clears search history.
	 */
	public void clear() {
		try {
			historyDao.deleteBuilder().delete();
		} catch (SQLException e) {
			DebugLog.logException(e);
		}
	}

	/**
	 * Gets stored trending hashtags.
	 * @return  trending hashtags.
	 * @throws SQLException if the database fails
	 */
	List<String> getTrendingHashtags() throws SQLException {
		List<StoredHashtag> storedHashtags = hashtagDao.queryForAll();
		List<String> result = new ArrayList<>();

		for (StoredHashtag storedHashtag : storedHashtags) {
			result.add(storedHashtag.text);
		}

		return result;
	}

	/**
	 * Stores trending hashtags.
	 * @param hashtags          hashtags to store
	 * @throws SQLException     if the database fails
	 */
	void storeTrendingHashtags(List<String> hashtags) throws SQLException {
		List<StoredHashtag> storedHashtags = new ArrayList<>();
		for (String hashtagText : hashtags) {
			storedHashtags.add(new StoredHashtag(hashtagText));
		}
		DbTransaction.performTransaction(
			hashtagDao,
			() -> {
				hashtagDao.deleteBuilder().delete();
				for (StoredHashtag storedHashtag : storedHashtags) {
					hashtagDao.create(storedHashtag);
				}
			}
		);
	}

	@DatabaseTable(tableName = DbSchemas.SearchHistory.TABLE_NAME)
	static class HistoryEntry {

		@DatabaseField(generatedId = true, columnName = DbSchemas.SearchHistory.ID)
		private int id;

		@DatabaseField(columnName = DbSchemas.SearchHistory.SEARCH_TYPE)
		private SearchType searchType;

		@DatabaseField(columnName = DbSchemas.SearchHistory.QUERY_TEXT)
		private String text;

		HistoryEntry() {  }

		HistoryEntry(String text, SearchType searchType) {
			this.text = text;
			this.searchType = searchType;
		}
	}

	@DatabaseTable
	static class StoredHashtag {

		@DatabaseField(generatedId = true)
		private int id;

		@DatabaseField
		private String text;

		StoredHashtag() {  }

		StoredHashtag(String text) {
			this.text = text;
		}
	}
}
