/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.fetcher.base;

import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.SingleViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Wraps a {@link FetchableAdapter} and adds service items (progress indicator, error message etc.).
 *
 * @param <T> type of items
 */
class AdapterWrapper<T extends ViewHolder> extends Adapter<ViewHolder> {

	private static final int VIEW_TYPE_LOADING_MORE = -1; // progress indicator
	private static final int VIEW_TYPE_RETRY_DOWNLOAD = -2; // message about an error
	private static final int VIEW_TYPE_LOAD_MORE = -3; // suggestion to load more items; shown only if auto loading is disabled
	private static final int INNER_VIEW_TYPE_COUNT = 3;

	private final FetchableAdapter<?, T> baseAdapter; // original adapter set to recycler view
	private final List<View> footers; // additional view shown at bottom of recycler view after service items

	private FetcherState fetcherState;
	private RecyclerView owner;

	AdapterWrapper(FetchableAdapter<?, T> baseAdapter, List<View> footers) {
		this.baseAdapter = baseAdapter;
		this.footers = new ArrayList<>(footers);
		baseAdapter.addFetcherCallback(new Callback() {
			@Override
			public void onStateChanged(FetcherState newState) {
				handleStateChanged(newState);
			}
		});
		// repeat data callbacks to the base adapter
		baseAdapter.registerAdapterDataObserver(new AdapterDataObserver() {
			@Override
			public void onChanged() {
				notifyDataSetChanged();
			}

			@Override
			public void onItemRangeChanged(int positionStart, int itemCount) {
				notifyItemRangeChanged(fromBaseAdapterPosition(positionStart), itemCount);
			}

			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				notifyItemRangeInserted(fromBaseAdapterPosition(positionStart), itemCount);
			}

			@Override
			public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
				int from = fromBaseAdapterPosition(fromPosition);
				int to = fromBaseAdapterPosition(toPosition);
				for (int i = 0; i < itemCount; i++) {
					notifyItemMoved(from + i, to + i);
				}
			}

			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				notifyItemRangeRemoved(fromBaseAdapterPosition(positionStart), itemCount);
			}
		});
		fetcherState = baseAdapter.getFetcher().getState();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		switch (viewType) {
			case VIEW_TYPE_LOADING_MORE:
				return createLoadingMoreViewHolder(parent);
			case VIEW_TYPE_RETRY_DOWNLOAD:
				return createButtonViewHolder(parent, R.string.es_load_more_retry_button);
			case VIEW_TYPE_LOAD_MORE:
				return createButtonViewHolder(parent, baseAdapter.getLoadMoreSuggestion(parent.getContext()));
			default:
				if (viewType < 0 && viewType >= -INNER_VIEW_TYPE_COUNT - footers.size()) {
					return new SingleViewHolder(footers.get(-viewType - INNER_VIEW_TYPE_COUNT - 1));
				}
				return baseAdapter.onCreateViewHolder(parent, unwrapViewType(viewType));
		}
	}

	private ViewHolder createLoadingMoreViewHolder(ViewGroup parent) {
		View view = ViewUtils.inflateLayout(R.layout.es_load_more_indicator, parent);
		return new SingleViewHolder(view);
	}

	private ViewHolder createButtonViewHolder(ViewGroup parent, @StringRes int textId) {
		return createButtonViewHolder(parent, parent.getContext().getString(textId));
	}

	private ViewHolder createButtonViewHolder(ViewGroup parent, String text) {
		View view = ViewUtils.inflateLayout(R.layout.es_load_more_button, parent);
		TextView button = ViewUtils.findView(view, R.id.es_button);
		button.setOnClickListener(v -> baseAdapter.requestMoreData());
		button.setText(text);
		return new SingleViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		if (shouldItemTakeAllRow(position)) {
			RecyclerView.LayoutManager layoutManager = owner.getLayoutManager();
			if (layoutManager instanceof StaggeredGridLayoutManager) {
				StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
				if (layoutParams != null) {
					layoutParams.setFullSpan(true);
				}
			}
		}
		if (!isSpecialViewPosition(position)) {
			//noinspection unchecked
			baseAdapter.onBindViewHolder((T) holder, getBaseAdapterPosition(position));
		}
	}

	/**
	 * Calculates the corresponding item position in the base adapter.
	 *
	 * @param position position in this adapter
	 */
	private int getBaseAdapterPosition(int position) {
		return isSpecialViewVisible() && position > baseAdapter.getPositionForWrapperViews()
			? position - 1
			: position;
	}

	/**
	 * Calculates the corresponding item position from the position in the base adapter.
	 * @param position position in the base adapter
	 */
	private int fromBaseAdapterPosition(int position) {
		return isSpecialViewVisible() && position >= baseAdapter.getPositionForWrapperViews()
			? position + 1
			: position;
	}

	@Override
	public int getItemCount() {
		int count = baseAdapter.getItemCount() + footers.size();
		if (isSpecialViewVisible()) {
			count++;
		}
		return count;
	}

	private boolean isSpecialViewVisible() {
		return baseAdapter.getViewState() != ViewState.REFRESHING
			&& (
			fetcherState == FetcherState.LAST_ATTEMPT_FAILED
				|| fetcherState == FetcherState.LOADING
				|| (fetcherState == FetcherState.HAS_MORE_DATA && baseAdapter.shouldAskBeforeLoad()));
	}

	@Override
	public int getItemViewType(int position) {
		// returns a view types returned by the base adapter and own view type constants for service items
		// to avoid conflicts, negative view types returned by the base adapter are shifted
		if (isFooterPosition(position)) {
			return -INNER_VIEW_TYPE_COUNT - getItemCount() + position; // each footer view has its own view type
		} else if (isSpecialViewPosition(position)) {
			switch (fetcherState) {
				case HAS_MORE_DATA:
					return VIEW_TYPE_LOAD_MORE;
				case LAST_ATTEMPT_FAILED:
					return VIEW_TYPE_RETRY_DOWNLOAD;
				default:
					return VIEW_TYPE_LOADING_MORE;
			}
		} else {
			int superValue = baseAdapter.getItemViewType(position);
			return wrapViewType(superValue);
		}
	}

	/**
	 * Whether it is a position of a service or footer view.
	 */
	boolean isSpecialViewPosition(int position) {
		return (isSpecialViewVisible() && position == baseAdapter.getPositionForWrapperViews()) || isFooterPosition(position);
	}

	/**
	 * Whether it is a position of a footer view.
	 */
	private boolean isFooterPosition(int position) {
		return position >= getItemCount() - footers.size();
	}

	/**
	 * Transforms a view type returned by the base adapter to avoid conflicts.
	 */
	private int wrapViewType(int viewType) {
		if (viewType >= 0) {
			return viewType;
		} else {
			return viewType - INNER_VIEW_TYPE_COUNT - footers.size();
		}
	}

	/**
	 * Restores the original value of view type (for base adapter) from a result of {@link #wrapViewType(int)} method.
	 */
	private int unwrapViewType(int viewType) {
		if (viewType >= 0) {
			return viewType;
		} else {
			return viewType + INNER_VIEW_TYPE_COUNT + footers.size();
		}
	}

	private void handleStateChanged(FetcherState state) {
		if (fetcherState != state) {
			fetcherState = state;
			notifyDataSetChanged();
		}
	}

	@Override
	public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
		super.onDetachedFromRecyclerView(recyclerView);
		baseAdapter.onDetachedFromRecyclerView(recyclerView);
		owner = null;
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
		baseAdapter.onAttachedToRecyclerView(recyclerView);
		owner = recyclerView;
	}

	/**
	 * Whether an item at that position should take all row.
	 */
	boolean shouldItemTakeAllRow(int position) {
		return isSpecialViewPosition(position) || baseAdapter.shouldItemTakeAllRow(getBaseAdapterPosition(position));
	}

}
