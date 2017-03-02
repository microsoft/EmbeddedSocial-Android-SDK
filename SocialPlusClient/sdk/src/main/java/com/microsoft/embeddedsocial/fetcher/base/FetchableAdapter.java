/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;

import com.microsoft.embeddedsocial.base.function.Predicate;
import com.microsoft.embeddedsocial.sdk.R;

/**
 * Base adapter for {@link FetchableRecyclerView}. It's an adapter responsible for its data loading itself.
 *
 * @param <T> data type
 * @param <V> view holder type
 */
public abstract class FetchableAdapter<T, V extends ViewHolder> extends Adapter<V> {

	private final Fetcher<T> fetcher;
	private final boolean shouldAskBeforeLoad; // whether the adapter doesn't load more data automatically
	private ViewState viewState;
	private ViewStateListener viewStateListener;
	private boolean refreshPostponed = false;
	private CompositeCallback fetcherCallback = new CompositeCallback();

	private final Callback innerCallback = new Callback() {

		@Override
		public void onDataUpdated() {
			handleLoadingFinished(viewState == ViewState.ERROR ? ViewState.ERROR : ViewState.EMPTY);
		}

		@Override
		public void onDataRequestSucceeded() {
			handleLoadingFinished(ViewState.EMPTY);
		}

		@Override
		public void onDataRequestFailed(Exception e) {
			handleLoadingFinished(ViewState.ERROR);
		}

		private void handleLoadingFinished(ViewState emptyDataState) {
			notifyDataSetChanged();
			if (refreshPostponed) {
				refreshPostponed = false;
				fetcher.refreshData();
			} else {
				ViewState newViewState = fetcher.isEmpty() ? emptyDataState : ViewState.DATA;
				setViewState(newViewState);
			}
		}
	};

	protected FetchableAdapter(Fetcher<T> fetcher) {
		this(fetcher, false);
	}

	protected FetchableAdapter(Fetcher<T> fetcher, boolean shouldAskBeforeLoad) {
		this.fetcher = fetcher;
		this.shouldAskBeforeLoad = shouldAskBeforeLoad;
		initFetcher();
	}

	/**
	 * Adds a callback for data loading.
	 */
	public void addFetcherCallback(Callback externalCallback) {
		fetcherCallback.addCallback(externalCallback);
	}

	/**
	 * Launches a data refresh.
	 */
	public void refreshData() {
		setViewState(ViewState.REFRESHING);
		// if data is loading we postpone the refresh until the loading is completed; the UI state is updated immediately
		if (fetcher.isLoading()) {
			refreshPostponed = true;
		} else {
			fetcher.refreshData();
		}
	}

	/**
	 * Removes the first item satisfying the <code>predicate</code> from the data.
	 */
	public void removeFirstMatch(Predicate<? super T> predicate) {
		int dataItemPosition = fetcher.removeFirstMatch(predicate);
		if (dataItemPosition != -1) {
			int position = getViewPosition(dataItemPosition);
			notifyItemRemoved(position);
			updateStateIfEmpty();
		}
	}

	/**
	 * Removes all items satisfying the <code>predicate</code> from the data.
	 */
	public void removeAllMatches(Predicate<? super T> predicate) {
		boolean dataChanged = fetcher.removeAllMatches(predicate);
		if (dataChanged) {
			notifyDataSetChanged();
			updateStateIfEmpty();
		}
	}

	private void updateStateIfEmpty() {
		if (viewState == ViewState.DATA && fetcher.isEmpty()) {
			setViewState(ViewState.EMPTY);
		}
	}

	/**
	 * Returns the position of the view corresponding to the data item.
	 * Override this method in subclasses if you add some non-data views (title, divider etc.).
	 *
	 * @param dataItemPosition position of an item in the fetcher
	 */
	protected int getViewPosition(int dataItemPosition) {
		return dataItemPosition;
	}

	private void initFetcher() {
		// fetcher can be used previously in another adapter, so we should restore UI state from it
		FetcherState fetcherState = fetcher.getState();
		if (fetcher.isEmpty()) {
			switch (fetcherState) {
				case LOADING:
					setViewState(ViewState.LOADING);
					break;
				case DATA_ENDED:
					setViewState(ViewState.EMPTY);
					break;
				case LAST_ATTEMPT_FAILED:
					setViewState(ViewState.ERROR);
					break;
				case HAS_MORE_DATA:
					fetcher.requestMoreData();
					setViewState(ViewState.LOADING);
					break;
			}
		} else {
			setViewState(ViewState.DATA);
		}
		fetcherCallback.addCallback(innerCallback);
		fetcher.setCallback(fetcherCallback);
	}

	private void setViewState(ViewState newViewState) {
		viewState = newViewState;
		notifyViewStateChanged();
	}

	/**
	 * Sets a listener for view state changes.
	 */
	public void setViewStateListener(ViewStateListener viewStateListener) {
		this.viewStateListener = viewStateListener;
		notifyViewStateChanged();
	}

	private void notifyViewStateChanged() {
		if (viewStateListener != null) {
			viewStateListener.onViewStateChanged(viewState, fetcher.getErrorCause());
		}
	}

	/**
	 * Launches a new data loading if it is allowed by fetcher.
	 */
	void requestMoreData() {
		boolean loading = fetcher.isLoading();
		boolean hasMoreData = fetcher.hasMoreData();
		if (!loading && hasMoreData) {
			fetcher.requestMoreData();
		}
	}

	/**
	 * Launches a new data loading if it is allowed by fetcher and we shouldn't ask user before data loading.
	 */
	void notifyMoreDataRequired() {
		if (!shouldAskBeforeLoad) {
			requestMoreData();
		}
	}

	boolean shouldAskBeforeLoad() {
		return shouldAskBeforeLoad;
	}

	/**
	 * Returns whether the last data loading attempt failed.
	 */
	public boolean lastLoadAttemptFailed() {
		return fetcher.getState() == FetcherState.LAST_ATTEMPT_FAILED;
	}

	public Fetcher<T> getFetcher() {
		return fetcher;
	}

	/**
	 * Returns size of the data (don't mix with the number of adapter items).
	 */
	public final int getDataSize() {
		return fetcher.getAllData().size();
	}

	/**
	 * Returns a data item at position.
	 * @param position data item position (don't mix with adapter item position)
	 */
	public T getItem(int position) {
		return fetcher.getAllData().get(position);
	}

	/**
	 * Returns the current view state.
	 */
	public ViewState getViewState() {
		return viewState;
	}

	/**
	 * Returns a string to be used on the suggestion to load more data. Override this method in your adapter to override the default suggestion message.
	 */
	protected String getLoadMoreSuggestion(Context context) {
		return context.getString(R.string.es_load_more_suggestion);
	}

	/**
	 * Returns the position where the service views (i.e. loading indicator, error message etc.) should be shown. By default they are shown at the end.
	 */
	protected int getPositionForWrapperViews() {
		return getItemCount();
	}

	/**
	 * Return whether an item at the position should take all row in grid layout
	 * @param position position of item view in adapter
	 */
	protected boolean shouldItemTakeAllRow(int position) {
		return false;
	}
}
