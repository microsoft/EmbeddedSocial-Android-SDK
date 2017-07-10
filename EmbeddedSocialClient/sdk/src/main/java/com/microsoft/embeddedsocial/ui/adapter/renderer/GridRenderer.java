/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.renderer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.event.click.OpenTopicEvent;
import com.microsoft.embeddedsocial.image.CoverLoader;
import com.microsoft.embeddedsocial.image.ImageLocation;
import com.microsoft.embeddedsocial.image.ImageViewContentLoader;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.BaseViewHolder;

/**
 * Renders topics in grid.
 */
public class GridRenderer extends Renderer<TopicView, GridRenderer.ViewHolder> {

	private int imageSize;

	public GridRenderer(Context context) {
		imageSize = context.getResources().getDimensionPixelSize(R.dimen.es_grid_cell_size);
	}

	@Override
	public ViewHolder createViewHolder(ViewGroup parent) {
		Context context = parent.getContext();
		View itemView = LayoutInflater.from(context).inflate(R.layout.es_grid_item, parent, false);
		return new ViewHolder(itemView);
	}

	@Override
	protected void onItemRendered(TopicView topic, ViewHolder viewHolder) {
		ImageLocation imageLocation = topic.getImageLocation();
		viewHolder.imageViewContentLoader.cancel();
		if (imageLocation != null) {
			viewHolder.imageViewContentLoader.load(imageLocation, imageSize);
			viewHolder.noImageView.setVisibility(View.GONE);
		} else {
			viewHolder.imageView.setImageBitmap(null);
			viewHolder.noImageView.setVisibility(View.VISIBLE);
		}
		ViewUtils.setVisible(viewHolder.uploadingIndicator, topic.isLocal());
		viewHolder.topic = topic;
	}

	/**
	 * View holder.
	 */
	static class ViewHolder extends BaseViewHolder implements OnClickListener {

		final ImageViewContentLoader imageViewContentLoader;
		final ImageView imageView;
		final View noImageView;
		final View uploadingIndicator;

		TopicView topic;

		public ViewHolder(View itemView) {
			super(itemView);
			noImageView = ViewUtils.findView(itemView, R.id.es_noImage);
			imageView = ViewUtils.findView(itemView, R.id.es_image);
			imageViewContentLoader = new CoverLoader(imageView);
			uploadingIndicator = ViewUtils.findView(itemView, R.id.es_uploadingIndicator);
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			EventBus.post(new OpenTopicEvent(topic));
		}
	}
}
