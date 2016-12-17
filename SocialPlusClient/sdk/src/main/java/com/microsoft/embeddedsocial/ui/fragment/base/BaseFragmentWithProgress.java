/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.sdk.R;

/**
 * Base class for fragments switching to the progressbar during some action execution.
 */
public abstract class BaseFragmentWithProgress extends BaseFragment {

	private View contentView;
	private View progressView;

	@Override
	protected int getLayoutId() {
		return R.layout.es_fragment_base_with_progress;
	}

	@LayoutRes
	protected abstract int getContentLayoutId();

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		progressView = findView(view, R.id.es_progress);
		ViewGroup parent = (ViewGroup) view;
		contentView = getLayoutInflater(savedInstanceState).inflate(getContentLayoutId(), parent, false);
		parent.addView(contentView);
	}

	protected void setProgressVisible(boolean progressVisible) {
		ViewUtils.setVisible(progressView, progressVisible);
		ViewUtils.setVisible(contentView, !progressVisible);
	}
}
