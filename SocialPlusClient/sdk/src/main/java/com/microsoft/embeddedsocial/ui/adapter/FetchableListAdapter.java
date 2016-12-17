/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;

import com.microsoft.embeddedsocial.fetcher.base.FetchableAdapter;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.adapter.renderer.Renderer;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.SingleViewHolder;

/**
 * <p>Simple {@link FetchableAdapter}'s extension showing a simple list of items (optionally with a header)
 * and delegating rendering to a {@link Renderer}.</p>
 * <p>
 * It shows items in such order:
 * <list>
 * <li>1) padding (= empty space) (optional)</li>
 * <li>2) header (optional)</li>
 * <li>3) data items</li>
 * <li>4) padding (optional)</li>
 * </list>
 * </p>
 *
 * @param <T> items type
 * @param <V> view holder type
 */
public class FetchableListAdapter<T, V extends ViewHolder> extends MultiTypeAdapter<T, RecyclerView.ViewHolder> {

	private static final int VIEW_TYPE_REGULAR = 0;
	private static final int VIEW_TYPE_TITLE = 1;
	private static final int VIEW_TYPE_PADDING = 2;

	private String title = null;
	private int verticalPadding = 0;

	private boolean hasTitle = false;
	private boolean hasVerticalPadding = false;
	private int itemsShift = 0;
	private int extraItemCount = 0;

	public FetchableListAdapter(Fetcher<T> fetcher, Renderer<? super T, ? extends V> renderer) {
		super(fetcher);

		registerViewType(VIEW_TYPE_REGULAR, renderer);

		Renderer<Object, SingleViewHolder> titleRenderer = new Renderer<Object, SingleViewHolder>() {
			@Override
			public SingleViewHolder createViewHolder(ViewGroup parent) {
				return SingleViewHolder.create(R.layout.es_list_header, parent);
			}

			@Override
			protected void onItemRendered(Object item, SingleViewHolder holder) {
				((TextView) holder.itemView).setText(title);
			}
		};
		registerViewType(VIEW_TYPE_TITLE, titleRenderer, dummyGetMethod());

		Renderer<Object, SingleViewHolder> paddingRenderer = new Renderer<Object, SingleViewHolder>() {
			@Override
			public SingleViewHolder createViewHolder(ViewGroup parent) {
				SingleViewHolder holder = SingleViewHolder.create(R.layout.es_padding, parent);
				holder.itemView.setMinimumHeight(verticalPadding);
				return holder;
			}
		};
		registerViewType(VIEW_TYPE_PADDING, paddingRenderer, dummyGetMethod());
	}

	@Override
	public T getItem(int position) {
		return super.getItem(position - itemsShift);
	}

	@Override
	public int getItemCount() {
		return getDataSize() + extraItemCount;
	}

	@Override
	protected int getViewPosition(int dataItemPosition) {
		return dataItemPosition + itemsShift;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0 && hasTitle) {
			return VIEW_TYPE_TITLE;
		} else if (hasVerticalPadding && (position == countTrueValues(hasTitle) || position == getItemCount() - 1)) {
			return VIEW_TYPE_PADDING;
		} else {
			return VIEW_TYPE_REGULAR;
		}
	}

	@Override
	protected boolean shouldItemTakeAllRow(int position) {
		return getItemViewType(position) != VIEW_TYPE_REGULAR || super.shouldItemTakeAllRow(position);
	}

	private int countTrueValues(boolean... values) {
		int count = 0;
		for (boolean value : values) {
			if (value) {
				count++;
			}
		}
		return count;
	}

	private void completeInitialization() {
		itemsShift = countTrueValues(hasTitle, hasVerticalPadding);
		extraItemCount = countTrueValues(hasTitle, hasVerticalPadding, hasVerticalPadding);
	}

	/**
	 * Builds a {@link FetchableListAdapter} instance.
	 *
	 * @param <T> adapter's items type
	 * @param <V> adapter's view holder type
	 */
	public static class Builder<T, V extends ViewHolder> {

		private FetchableListAdapter<T, V> adapter;

		public Builder(Fetcher<T> fetcher, Renderer<? super T, ? extends V> renderer) {
			adapter = new FetchableListAdapter<>(fetcher, renderer);
		}

		public Builder<T, V> setTitle(String title) {
			adapter.title = title;
			adapter.hasTitle = !TextUtils.isEmpty(title);
			return this;
		}

		public Builder<T, V> setVerticalPadding(int verticalPadding) {
			adapter.verticalPadding = verticalPadding;
			adapter.hasVerticalPadding = verticalPadding > 0;
			return this;
		}

		public FetchableListAdapter<T, V> build() {
			adapter.completeInitialization();
			return adapter;
		}

	}
}
