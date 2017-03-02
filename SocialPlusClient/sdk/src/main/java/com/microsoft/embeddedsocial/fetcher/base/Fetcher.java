/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher.base;

import com.microsoft.embeddedsocial.base.IDisposable;
import com.microsoft.embeddedsocial.base.function.Predicate;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.base.utils.thread.BackgroundThreadFactory;
import com.microsoft.embeddedsocial.base.utils.thread.ThreadUtils;
import com.microsoft.embeddedsocial.server.model.FeedUserRequest;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Pages data by portions from some source.
 *
 * @param <T> data type
 */
public abstract class Fetcher<T> implements IDisposable {

	private static final int MAX_ITEMS_NUMBER = 5000;
	private static final int SYNC_THREAD_PRIORITY = 8;

	private final ExecutorService executor = Executors.newSingleThreadExecutor();
	// used another executor for updates from the cache to minimize delay
	private final ExecutorService syncExecutor = Executors.newSingleThreadExecutor(new BackgroundThreadFactory(SYNC_THREAD_PRIORITY));
	private final CallbackNotifier callbackNotifier = new CallbackNotifier();
	private final DataHolder<T> data = new DataHolder<>(callbackNotifier);

	private FetcherState state = FetcherState.HAS_MORE_DATA;
	private Exception errorCause;
	private DataState currentDataState = createEmptyDataState();

	private int pageSizeFactor = 1;
	private int desiredPageSize = FeedUserRequest.DEFAULT_BATCH_SIZE;

	private volatile boolean dataReplacedFromCache;

	/**
	 * Sets a data loading callback
	 */
	public final void setCallback(Callback callback) {
		callbackNotifier.setCallback(callback);
		callbackNotifier.notifyStateChanged(state);
	}

	/**
	 * Returns whether the data is not marked as ended yet.
	 */
	public final boolean hasMoreData() {
		return state != FetcherState.DATA_ENDED;
	}

	private void setState(FetcherState newState) {
		state = newState;
		callbackNotifier.notifyStateChanged(newState);
	}

	/**
	 * Requests a new data page. it's forbidden to call this method if data is loading now.
	 */
	public final void requestMoreData() {
		if (state == FetcherState.DATA_ENDED) {
			throw new RuntimeException("no more data");
		}
		submitDataRequest(() -> {
			try {
				Exception exception = null;
				try {
					addData(fetchDataPageWithState(currentDataState, RequestType.REGULAR));
				} catch (PartiallyLoadedDataException e) {
					DebugLog.logException(e.getCause());
					addData(e.getLoadedData());
					exception = e;
				}
				if (exception == null) {
					onDataRequestSucceeded();
				} else {
					onDataRequestFailed(exception);
				}
			} catch (Exception e) {
				DebugLog.logException(e);
				onDataRequestFailed(e);
			}
		});
	}

	private void addData(List<T> newData) throws Exception {
		synchronized (this) {
			if (dataReplacedFromCache) {
				// we can't just add the new data, because the data in this object has changed, so we re-read it from the cache
				replaceDataFromCache();
			} else {
				data.add(newData);
			}
		}
	}

	/**
	 * Requests a data refresh (i.e. loading starts from the beginning and data is replaced). it's forbidden to call this method if data is loading now.
	 * If the data request fails the data is not affected.
	 */
	public final void refreshData() {
		submitDataRequest(() -> {
			try {
				DataState dataState = createEmptyDataState(); // create a new data state object to read data from the start
				List<T> newData = fetchDataPageWithState(dataState, RequestType.FORCE_REFRESH);
				synchronized (this) {
					if (dataReplacedFromCache) {
						// we can't just set the new data, because it might be outdated, so we re-read it from the cache
						replaceDataFromCache();
					} else {
						data.replaceData(newData);
					}
				}
				currentDataState = dataState; // replace data state only if an attempt is successful; otherwise object's state isn't changed
				onDataRequestSucceeded();
			} catch (Exception e) {
				DebugLog.logException(e);
				onDataRequestFailed(e);
			}

		});
	}

	/**
	 * Replaces the current data with the data stored in the cache.
	 */
	public final void syncWithCache() {
		syncExecutor.submit(() -> {
			try {
				replaceDataFromCache();
				callbackNotifier.notifyDataUpdated();
			} catch (Exception e) {
				DebugLog.logException(e);
			}
		});
	}

	private void submitDataRequest(Runnable task) {
		if (executor.isShutdown()) {
			DebugLog.d("executor is shutdown, ignore new tasks");
			return;
		}
		if (state == FetcherState.LOADING) {
			throw new IllegalStateException("fetcher is already loading data");
		}
		if (!ThreadUtils.inMainThread()) {
			throw new RuntimeException("this method must be called only from the main thread");
		}
		setState(FetcherState.LOADING);
		executor.submit(() -> {
			dataReplacedFromCache = false;
			task.run();
		});
	}

	private void onDataRequestSucceeded() {
		onDataLoadingCompleted(false, null);
	}

	private void onDataRequestFailed(Exception e) {
		onDataLoadingCompleted(true, e);
	}

	private void onDataLoadingCompleted(boolean error, Exception exception) {
		if (data.size() >= MAX_ITEMS_NUMBER) {
			currentDataState.markDataEnded();
		}
		if (currentDataState.isDataEnded()) {
			setState(FetcherState.DATA_ENDED);
		} else {
			setState(error ? FetcherState.LAST_ATTEMPT_FAILED : FetcherState.HAS_MORE_DATA);
		}

		if (error) {
			callbackNotifier.notifyDataRequestFailed(exception);
			if (errorCause == null) {
				// save the root cause
				setErrorCause(exception);
			}
		} else {
			callbackNotifier.notifyDataRequestSucceeded();
		}
	}

	private void replaceDataFromCache() throws Exception {
		synchronized (this) {
			DataState dataState = createEmptyDataState();
			List<T> newData = fetchDataPage(dataState, RequestType.SYNC_WITH_CACHE, -1);
			if (newData == null) {
				newData = Collections.emptyList();
			}
			data.replaceData(newData);
			dataReplacedFromCache = true;
		}
	}

	private List<T> fetchDataPageWithState(DataState dataState, RequestType refreshState) throws Exception {
		if (dataState.isDataEnded()) {
			throw new RuntimeException("requesting more data, but data is ended");
		}
		int batchSize = getBatchSize();
		List<T> newData = fetchDataPage(dataState, refreshState, batchSize);
		if (newData == null) {
			newData = Collections.emptyList();
		}
		if (newData.isEmpty()) {
			dataState.markDataEnded();
		}
		return newData;
	}

	/**
	 * <p>Reads a next data page. Use <code>dataState</code> to save any state in this method.</p>
	 * <p>If data is ended, call {@link DataState#markDataEnded()} method on <code>dataState</code>.
	 * This method will be called automatically if the result size is less then <code>pageSize</code>.</p>
	 * <p>To save a continuation key call {@link DataState#setContinuationKey(String)} method (if a continuation key is <code>null</code>,
	 * {@link DataState#markDataEnded()} will be called automatically). The saved continuation key can be obtained from <code>dataState</code>
	 * on next method call.</p>
	 * <p>If there was an error during fetching the data, but you have some data to store, wrap it with {@link PartiallyLoadedDataException}.</p>
	 */
	protected abstract List<T> fetchDataPage(DataState dataState, RequestType requestType, int pageSize) throws Exception;

	/**
	 * Returns whether the data is being loading now.
	 */
	public final boolean isLoading() {
		return state == FetcherState.LOADING;
	}

	/**
	 * returns the state.
	 */
	public final FetcherState getState() {
		return state;
	}

	protected void setErrorCause(Exception e) {
		errorCause = e;
	}

	public final Exception getErrorCause() {
		return errorCause;
	}

	/**
	 * Returns an unmodifiable link to the data.
	 */
	public final List<T> getAllData() {
		return data.getAll();
	}

	protected DataState createEmptyDataState() {
		return new DataState();
	}

	/**
	 * Returns whether the data is empty.
	 */
	public final boolean isEmpty() {
		return data.isEmpty();
	}

	/**
	 * Sets the desired page size.
	 */
	public void setDesiredPageSize(int desiredPageSize) {
		this.desiredPageSize = desiredPageSize;
	}

	/**
	 * Deletes all data items satisfying the predicate.
	 * @return <code>true</code> if any item was deleted, <code>false</code> otherwise
	 */
	public boolean removeAllMatches(Predicate<? super T> predicate) {
		synchronized (this) {
			return data.removeAllMatches(predicate);
		}
	}

	/**
	 * Deletes the first data item satisfying the predicate.
	 * @return the position of deleted item or -1 if no item deleted
	 */
	public int removeFirstMatch(Predicate<? super T> predicate) {
		synchronized (this) {
			return data.removeFirstMatch(predicate);
		}
	}

	/**
	 * Inserts a data item at position
	 */
	public void insertItem(T item, int position) {
		synchronized (this) {
			data.insertItem(item, position);
		}
	}

	/**
	 * Returns the batch size for data request. You can set a desired size for this value by calling {@link #setDesiredPageSize(int)}.
	 */
	final int getBatchSize() {
		// considering the number of items in grid row to avoid gaps
		if (desiredPageSize % pageSizeFactor == 0) {
			return desiredPageSize;
		} else {
			return (desiredPageSize / pageSizeFactor + 1) * pageSizeFactor;
		}
	}

	final void setPageSizeFactor(int pageSizeFactor) {
		this.pageSizeFactor = pageSizeFactor;
	}

	final int getPageSizeFactor() {
		return pageSizeFactor;
	}

	@Override
	public void dispose() {
		setCallback(null);
		executor.shutdown();
	}

}
