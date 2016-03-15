/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.adapter.renderer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.microsoft.socialplus.base.event.EventBus;
import com.microsoft.socialplus.base.utils.ViewUtils;
import com.microsoft.socialplus.event.click.OnTrendingHashtagSelectedEvent;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.adapter.renderer.TrendingHashtagsRenderer.ViewHolder;
import com.microsoft.socialplus.ui.adapter.viewholder.BaseViewHolder;

/**
 * Renders trending hashtags.
 */
public class TrendingHashtagsRenderer extends Renderer<String, ViewHolder> {

	private final View.OnClickListener onTrendingHashtagClickListener;

	public TrendingHashtagsRenderer() {
		onTrendingHashtagClickListener = new OnTrendingHashtagClickListener();
	}

	@Override
	public ViewHolder createViewHolder(ViewGroup parent) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sp_trending_hashtag_list_item, parent, false);
		return new ViewHolder(view);
	}

	@Override
	protected void onItemRendered(String item, ViewHolder holder) {
		holder.text1.setText(item);
	}

	/**
	 * View holder.
	 */
	final class ViewHolder extends BaseViewHolder {
		private final TextView text1;

		private ViewHolder(View itemView) {
			super(itemView);
			text1 = ViewUtils.findView(itemView, android.R.id.text1);
			text1.setOnClickListener(onTrendingHashtagClickListener);
		}
	}

	private static class OnTrendingHashtagClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			EventBus.post(new OnTrendingHashtagSelectedEvent(((TextView) v).getText().toString()));
		}
	}
}
