/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.microsoft.socialplus.base.utils.ViewUtils;
import com.microsoft.socialplus.data.storage.UserActionProxy;
import com.microsoft.socialplus.image.CoverLoader;
import com.microsoft.socialplus.image.ImageLocation;
import com.microsoft.socialplus.image.ImageViewContentLoader;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.model.view.TopicView;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.ui.fragment.base.BaseEditPostFragment;
import com.microsoft.socialplus.ui.util.TextHelper;

/**
 * Post editing.
 */
public class EditPostFragment extends BaseEditPostFragment {

	private TopicView topic;
	@SuppressWarnings("FieldCanBeLocal")
	private ImageViewContentLoader coverLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		topic = getActivity().getIntent().getParcelableExtra(IntentExtras.TOPIC_EXTRA);
	}

	@Override
	protected boolean isInputEmpty() {
		return TextHelper.areEqual(topic.getTopicTitle(), getTitle()) && TextHelper.areEqual(topic.getTopicText(), getDescription());
	}

	@Override
	protected void onFinishedEditing() {
		if (!isInputEmpty()) {
			topic.setTopicTitle(getTitle());
			topic.setTopicText(getDescription());
			new UserActionProxy(getContext()).updateTopic(topic);
		}
		finishActivity();
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState == null) {
			setTitle(topic.getTopicTitle());
			setDescription(topic.getTopicText());
		}
		TextView imageMessageView = getImageMessageView();
		ImageLocation imageLocation = topic.getImageLocation();
		if (imageLocation != null) {
			coverLoader = new CoverLoader(getCoverView()) {
				@Override
				protected void onBitmapLoaded(Bitmap bitmap) {
					super.onBitmapLoaded(bitmap);
					getImageMessageView().setVisibility(View.GONE);
				}
			};
			coverLoader.load(imageLocation, ViewUtils.getDisplayWidth(getActivity()));
			imageMessageView.setText("");
		} else {
			imageMessageView.setText(R.string.sp_no_image);
		}
	}
}
