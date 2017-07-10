/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher.base;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.GridLayoutManager.SpanSizeLookup;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;

import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.base.utils.thread.ThreadUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Extension of {@link RecyclerView} loading more data when it is needed.
 */
public class FetchableRecyclerView extends RecyclerView {

	private static final double LOAD_MORE_GAP_COEF = 0.2;
	private static final int LOAD_MORE_MIN_GAP = 5;

	private FetchableAdapter<?, ?> adapter;
	private List<View> footers = new LinkedList<>();

	private final Callback fetcherCallback = new Callback() {
		@Override
		public void onDataRemoved() {
			ThreadUtils.runOnMainThread(FetchableRecyclerView.this::requestMoreDataIfNeeded);
		}

		@Override
		public void onStateChanged(FetcherState newState) {
			if (newState == FetcherState.HAS_MORE_DATA) {
				// check if downloaded data is not enough
				ThreadUtils.getMainThreadHandler().post(FetchableRecyclerView.this::requestMoreDataIfNeeded);
			}
		}
	};

	public FetchableRecyclerView(Context context) {
		super(context);
	}

	public FetchableRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FetchableRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setAdapter(@SuppressWarnings("rawtypes") Adapter adapter) {
		throw new RuntimeException("Please use setFetchableAdapter instead");
	}

	/**
	 * Sets an adapter.
	 */
	public void setFetchableAdapter(FetchableAdapter<?, ?> newAdapter) {
		if (newAdapter != null) {
			// the adapter is wrapped to add service views
			Adapter<?> wrappedAdapter = wrapAdapter(newAdapter);
			super.setAdapter(wrappedAdapter);
			requestMoreDataIfNeeded();
		} else {
			super.setAdapter(null);
		}
	}

	private Adapter<?> wrapAdapter(FetchableAdapter<?, ?> newAdapter) {
		adapter = newAdapter;
		LayoutManager layoutManager = getLayoutManager();
		if (layoutManager != null) {
			adapter.getFetcher().setPageSizeFactor(resolveSpanCount(layoutManager));
		}
		adapter.addFetcherCallback(fetcherCallback);
		return new AdapterWrapper<>(adapter, footers);
	}

	@Override
	public void onScrolled(int dx, int dy) {
		super.onScrolled(dx, dy);
		if (dy > 0) {
			requestMoreDataIfNeeded();
		}
	}

	private void requestMoreDataIfNeeded() {
		if (adapter != null && adapter.lastLoadAttemptFailed()) {
			LayoutManager layoutManager = getLayoutManager();
			int totalItemCount = layoutManager.getItemCount();
			int lastVisibleItemPosition = getLastVisibleItemPosition(layoutManager);
			if (lastVisibleItemPosition == NO_POSITION) {
				return;
			}
			Fetcher<?> fetcher = adapter.getFetcher();
			int gap = Math.max((int) (fetcher.getBatchSize() * fetcher.getPageSizeFactor() * LOAD_MORE_GAP_COEF), LOAD_MORE_MIN_GAP);
			if (totalItemCount - lastVisibleItemPosition < gap) {
				adapter.notifyMoreDataRequired();
			}
		}
	}

	private int getLastVisibleItemPosition(LayoutManager layoutManager) {
		try {
			if (layoutManager instanceof LinearLayoutManager) {
				return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
			} else {
				StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
				int[] lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
				staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
				int lastPosition = lastPositions[0];
				for (int i = 1; i < lastPositions.length; i++) {
					if (lastPositions[i] > lastPosition) {
						lastPosition = lastPositions[i];
					}
				}
				return lastPosition;
			}
		} catch (Exception e) {
			DebugLog.logException(e);
			return NO_POSITION;
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (h > 0) {
			requestMoreDataIfNeeded();
		}
	}

	@Override
	public void setLayoutManager(LayoutManager layout) {
		super.setLayoutManager(layout);
		if (adapter != null) {
			adapter.getFetcher().setPageSizeFactor(resolveSpanCount(layout));
		}
	}

	private int resolveSpanCount(LayoutManager layout) {
		if (layout instanceof GridLayoutManager) {
			GridLayoutManager gridLayoutManager = (GridLayoutManager) layout;
			gridLayoutManager.setSpanSizeLookup(new GridSpanSizeLookup(gridLayoutManager));
			return gridLayoutManager.getSpanCount();
		} else if (layout instanceof StaggeredGridLayoutManager) {
			return ((StaggeredGridLayoutManager) layout).getSpanCount();
		} else {
			return 1;
		}
	}

	@Override
	public void swapAdapter(@SuppressWarnings("rawtypes") Adapter newAdapter, boolean removeAndRecycleExistingViews) {
		throw new RuntimeException("Please use setFetchableAdapter instead");
	}

	/**
	 * Launches a data refresh.
	 */
	public void refreshData() {
		if (adapter != null) {
			adapter.refreshData();
		}
	}

	/**
	 * Adds a view to show at the bottom.
	 */
	public void addFooterView(View view) {
		footers.add(view);
	}

	/**
	 * Returns the number of items in this recycler view.
	 */
	public int getItemCount() {
		return getAdapter().getItemCount();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		requestMoreDataIfNeeded();
	}

	/**
	 * Sets span size for service items.
	 */
	private class GridSpanSizeLookup extends SpanSizeLookup {
		private final GridLayoutManager gridLayoutManager;
		private final SpanSizeLookup baseSpanSizeLookup;

		public GridSpanSizeLookup(GridLayoutManager gridLayoutManager) {
			this.gridLayoutManager = gridLayoutManager;
			this.baseSpanSizeLookup = gridLayoutManager.getSpanSizeLookup();
		}

		@Override
		public int getSpanSize(int position) {
			AdapterWrapper<?> wrapper = (AdapterWrapper<?>) getAdapter();
			if (wrapper.shouldItemTakeAllRow(position)) {
				return gridLayoutManager.getSpanCount();
			} else {
				return baseSpanSizeLookup.getSpanSize(position);
			}
		}
	}

}
