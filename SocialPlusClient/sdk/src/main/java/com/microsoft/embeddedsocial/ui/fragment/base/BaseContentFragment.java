/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.fetcher.base.FetchableAdapter;
import com.microsoft.embeddedsocial.fetcher.base.FetchableRecyclerView;
import com.microsoft.embeddedsocial.fetcher.base.FetcherState;
import com.microsoft.embeddedsocial.fetcher.base.ViewState;
import com.microsoft.embeddedsocial.fetcher.base.ViewStateListener;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.NetworkAvailability;
import com.microsoft.embeddedsocial.ui.fragment.module.SlowConnectionMessageModule;
import com.microsoft.embeddedsocial.fetcher.base.Callback;

/**
 * Base class for fragments loading and displaying data. It contains a RecyclerView to show data, empty data message view,
 * error message view, loading indicator view and switches between them depending on data loading state.
 * Also includes the pull-to-refresh functionality. Data loading is delegated to {@link FetchableAdapter}.
 *
 * @param <AT> type of adapter
 */
public abstract class BaseContentFragment<AT extends FetchableAdapter<?, ?>> extends BaseFragment implements ViewStateListener, OnRefreshListener {

	// TODO: find a generic way to keep the last Fetcher instance to reuse

	private static final String PREF_ERROR_MESSAGE = "contentErrorMessage";
	private static final String PREF_EMPTY_MESSAGE = "contentEmptyMessage";

	private FetchableRecyclerView recyclerView;
	private SwipeRefreshLayout swipeRefreshLayout;
	private View progressView;
	private TextView messageView;
	private View contentView;
	private View[] stateViews;

	private String emptyDataMessage;
	private String errorMessage;

	private AT adapter;

	private boolean justStarted;

	private final SlowConnectionMessageModule slowConnectionMessageModule = new SlowConnectionMessageModule(this, R.string.es_close, null);

	private final Callback loadingStateListener = new Callback() {

		@Override
		public void onStateChanged(FetcherState newState) {
			if (newState == FetcherState.LOADING) {
				onLoadingStarted();
			} else {
				onLoadingFinished();
			}
		}
	};

	public BaseContentFragment() {
		addThemeToMerge(R.style.EmbeddedSocialSdkThemeOverlayContentFragment);
		addModule(slowConnectionMessageModule);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		justStarted = true;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.es_fragment_base_fetchable;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		TypedArray typedArray = getContext().obtainStyledAttributes(R.styleable.es_ThemeOverlay);
		int contentLayoutId = typedArray.getResourceId(R.styleable.es_ThemeOverlay_es_baseContentLayout, R.layout.es_content_view);
		int contentViewId = typedArray.getResourceId(R.styleable.es_ThemeOverlay_es_baseContentViewId, R.id.es_recyclerView);
		typedArray.recycle();

		ViewGroup contentLayout = findView(view, R.id.es_contentLayout);
		getLayoutInflater(savedInstanceState).inflate(contentLayoutId, contentLayout);
		restoreMessages(savedInstanceState);
		initViews(view, contentViewId);

		return view;
	}

	private void restoreMessages(@Nullable Bundle savedInstanceState) {
		if (savedInstanceState == null) {
			emptyDataMessage = getString(R.string.es_message_no_data);
			errorMessage = getString(R.string.es_message_failed_to_load_data);
		} else {
			emptyDataMessage = savedInstanceState.getString(PREF_EMPTY_MESSAGE);
			errorMessage = savedInstanceState.getString(PREF_ERROR_MESSAGE);
		}
	}

	private void initViews(View view, int contentViewId) {
		contentView = findView(view, contentViewId);
		swipeRefreshLayout = findView(view, R.id.es_swipeRefresh);
		swipeRefreshLayout.setOnRefreshListener(this);
		recyclerView = findView(view, R.id.es_recyclerView);
		setContentLayoutManager(createInitialContentLayoutManager());
		progressView = findView(view, R.id.es_progress);
		messageView = findView(view, R.id.es_message);
		stateViews = new View[]{contentView, progressView, messageView};
		initRecyclerView();
		setHasOptionsMenu(true);
	}

	protected void initRecyclerView() {
		AT initialAdapter = createInitialAdapter();
		setAdapter(initialAdapter);
	}

	/**
	 * Creates an adapter to set on fragment creation.
	 */
	protected abstract AT createInitialAdapter();

	/**
	 * Sets an data adapter.
	 */
	protected void setAdapter(AT adapter) {
		AT oldAdapter = this.adapter;
		if (oldAdapter != null && (adapter == null || oldAdapter.getFetcher() != adapter.getFetcher())) {
			oldAdapter.getFetcher().dispose();
		}
		this.adapter = adapter;
		swipeRefreshLayout.setRefreshing(false);
		recyclerView.setFetchableAdapter(adapter);
		slowConnectionMessageModule.onOperationFinished();
		if (adapter != null) {
			adapter.addFetcherCallback(loadingStateListener);
			if (adapter.getFetcher().isLoading()) {
				slowConnectionMessageModule.onOperationStarted();
			}
			adapter.setViewStateListener(this);
		}
	}

	/**
	 * Returns the current data adapter.
	 */
	public AT getAdapter() {
		return adapter;
	}

	/**
	 * Creates a layout manager to set on fragment creation.
	 */
	protected abstract LayoutManager createInitialContentLayoutManager();

	/**
	 * Sets a layout manager for RecyclerView.
	 */
	protected void setContentLayoutManager(LayoutManager layoutManager) {
		recyclerView.setLayoutManager(layoutManager);
	}

	@Override
	public void onViewStateChanged(ViewState viewState, Exception exception) {
		swipeRefreshLayout.setRefreshing(viewState == ViewState.REFRESHING);
		swipeRefreshLayout.setEnabled(viewState != ViewState.LOADING && viewState != ViewState.REFRESHING);
		switch (viewState) {
			case DATA:
				checkConnection();
				switchToView(contentView);
				break;
			case LOADING:
				switchToView(progressView);
				break;
			case EMPTY:
				checkConnection();
				messageView.setText(getEmptyDataMessage());
				switchToView(messageView);
				break;
			case ERROR:
				checkConnection();
				messageView.setText(getErrorMessage(exception));
				switchToView(messageView);
				break;
		}
	}

	private void checkConnection() {
		if (!isNetworkAvailable()) {
			View view = getView();
			if (view != null) {
				Snackbar.make(view, R.string.es_message_no_connection, Snackbar.LENGTH_LONG).show();
			}
		}
	}

	private boolean isNetworkAvailable() {
		return GlobalObjectRegistry.getObject(NetworkAvailability.class).isNetworkAvailable();
	}

	protected String getErrorMessage(Exception exception) {
		return errorMessage;
	}

	public void setErrorMessage(@StringRes int errorMessageId) {
		this.errorMessage = getString(errorMessageId);
	}

	protected String getEmptyDataMessage() {
		return emptyDataMessage;
	}

	protected void setEmptyDataMessage(@StringRes int emptyDataMessageId) {
		this.emptyDataMessage = getString(emptyDataMessageId);
	}

	public void setEmptyDataMessage(String emptyDataMessage) {
		this.emptyDataMessage = emptyDataMessage;
	}

	private void switchToView(View view) {
		for (View v : stateViews) {
			ViewUtils.setVisible(v, v == view);
		}
	}

	protected FetchableRecyclerView getRecyclerView() {
		return recyclerView;
	}

	@Override
	public void onRefresh() {
		recyclerView.refreshData();
	}

	@Override
	public void onStart() {
		super.onStart();
		refreshSilentlyIfNeeded();
		justStarted = false;
	}

	protected void refreshSilentlyIfNeeded() {
		// replace data from the cache on return to the activity if needed
		if (!justStarted
			&& adapter != null
			&& adapter.getViewState() != ViewState.ERROR
			&& adapter.getViewState() != ViewState.LOADING
			&& !isRestarting()) {

			adapter.getFetcher().syncWithCache();
		}
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(PREF_EMPTY_MESSAGE, emptyDataMessage);
		outState.putString(PREF_ERROR_MESSAGE, errorMessage);
	}

	protected void setSidePadding(int sidePadding) {
		Rect padding = ViewUtils.getPadding(getRecyclerView());
		padding.right = sidePadding;
		padding.left = sidePadding;
		ViewUtils.setPadding(getRecyclerView(), padding);
	}

	protected void onLoadingFinished() {
		slowConnectionMessageModule.onOperationFinished();
	}

	protected void onLoadingStarted() {
		slowConnectionMessageModule.onOperationStarted();
	}

	public void resetAdapter() {
		setAdapter(createInitialAdapter());
	}

}
