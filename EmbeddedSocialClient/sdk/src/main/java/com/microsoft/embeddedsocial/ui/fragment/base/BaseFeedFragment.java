/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.base;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Display;
import android.view.View;

import com.microsoft.embeddedsocial.data.display.DisplayMethod;
import com.microsoft.embeddedsocial.event.sync.PostUploadedEvent;
import com.microsoft.embeddedsocial.fetcher.base.FetchableAdapter;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.ui.adapter.FetchableListAdapter;
import com.microsoft.embeddedsocial.ui.adapter.renderer.CardViewRenderer;
import com.microsoft.embeddedsocial.ui.fragment.module.CommonTopicFeedBehaviorModule;
import com.microsoft.embeddedsocial.data.Preferences;
import com.microsoft.embeddedsocial.event.click.DisplayMethodChangedEvent;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.ui.adapter.renderer.GridRenderer;
import com.microsoft.embeddedsocial.ui.adapter.renderer.Renderer;
import com.microsoft.embeddedsocial.ui.util.DisplayParams;
import com.squareup.otto.Subscribe;

/**
 * Base class for fragments showing topic feed.
 */
public abstract class BaseFeedFragment extends BaseContentFragment<FetchableAdapter<TopicView, ?>> {

	private Fetcher<TopicView> fetcher;
	private DisplayMethod lastDisplayMethod;
	private int gallerySpanCount = 1;

	protected BaseFeedFragment() {
		addThemeToMerge(R.style.EmbeddedSocialSdkThemeOverlayFeed);
		addModule(new CommonTopicFeedBehaviorModule(this));
		addEventListener(new Object() {

			@Subscribe
			public void onDisplayMethodChanged(DisplayMethodChangedEvent event) {
				BaseFeedFragment.this.onDisplayMethodChanged();
			}

			@Subscribe
			public void onPostUploaded(PostUploadedEvent event) {
				if (canContainLocalPosts()) {
					refreshSilentlyIfNeeded();
				}
			}

		});
	}

	protected FetchableAdapter<TopicView, ?> createAdapter(DisplayParams displayParams) {
		if (fetcher == null) {
			fetcher = createFetcher();
		}
		Renderer<TopicView, ?> renderer = createRenderer(displayParams.method);
		return new FetchableListAdapter.Builder<>(fetcher, renderer)
			.setVerticalPadding(displayParams.method.getVerticalPadding(getContext()))
			.build();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setSidePadding(getCurrentDisplayParams().method.getHorizontalPadding(getContext()));
	}

	@Override
	public void resetAdapter() {
		fetcher = null;
		DisplayParams displayParams = getCurrentDisplayParams();
		setContentLayoutManager(createContentLayoutManager(displayParams));
		setAdapter(createAdapter(displayParams));
	}

	protected abstract Fetcher<TopicView> createFetcher();

	private void onDisplayMethodChanged() {
		DisplayParams displayParams = getCurrentDisplayParams();
		setContentLayoutManager(createContentLayoutManager(displayParams));
		setAdapter(createAdapter(displayParams));
		lastDisplayMethod = displayParams.method;
		setSidePadding(displayParams.method.getHorizontalPadding(getContext()));
	}

	@Override
	protected FetchableAdapter<TopicView, ?> createInitialAdapter() {
		DisplayParams displayParams = getCurrentDisplayParams();
		lastDisplayMethod = displayParams.method;
		return createAdapter(displayParams);
	}

	public DisplayParams getCurrentDisplayParams() {
		DisplayMethod displayMethod = Preferences.getInstance().getDisplayMethod();
		int spanCount;
		if (displayMethod == DisplayMethod.GALLERY) {
			spanCount = gallerySpanCount;
		} else {
			Resources resources = getResources();
			spanCount = resources.getBoolean(R.bool.es_isLandscape) && resources.getBoolean(R.bool.es_isTablet) ? 2 : 1;
		}
		return new DisplayParams(displayMethod, spanCount);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (lastDisplayMethod != getCurrentDisplayParams().method) {
			onDisplayMethodChanged();
		}
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		gallerySpanCount = calculateSpanCount();
	}

	@Override
	protected RecyclerView.LayoutManager createInitialContentLayoutManager() {
		return createContentLayoutManager(getCurrentDisplayParams());
	}

	private RecyclerView.LayoutManager createContentLayoutManager(DisplayParams displayParams) {
		if (displayParams.method == DisplayMethod.GALLERY) {
			return new GridLayoutManager(getActivity(), displayParams.spanCount);
		} else {
			if (displayParams.spanCount == 1) {
				return new LinearLayoutManager(getContext());
			} else {
				if (Preferences.getInstance().getUseStaggeredLayoutManager()) {
					return new StaggeredGridLayoutManager(displayParams.spanCount, StaggeredGridLayoutManager.VERTICAL);
				} else {
					return new GridLayoutManager(getActivity(), displayParams.spanCount);
				}
			}
		}
	}

	private int calculateSpanCount() {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int cellSize = getResources().getDimensionPixelSize(R.dimen.es_grid_cell_size);
		return Math.max(1, (int) Math.round((double) width / cellSize));
	}

	protected Renderer<TopicView, ? extends RecyclerView.ViewHolder> createRenderer(DisplayMethod displayMethod) {
		switch (displayMethod) {
			case GALLERY:
				return createGridRenderer();
			case LIST:
				return createCardRenderer();
			default:
				throw new RuntimeException("unknown display method");
		}
	}

	protected Renderer<TopicView, ? extends RecyclerView.ViewHolder> createCardRenderer() {
		return new CardViewRenderer(getContext());
	}

	protected Renderer<TopicView, ? extends RecyclerView.ViewHolder> createGridRenderer() {
		return new GridRenderer(getContext());
	}

	/**
	 * Returns whether the feed might includes local topics.
	 */
	protected boolean canContainLocalPosts() {
		return false;
	}

}
