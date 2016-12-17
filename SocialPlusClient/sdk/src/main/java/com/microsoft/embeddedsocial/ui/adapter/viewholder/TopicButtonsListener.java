/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.viewholder;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.activity.LikesActivity;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.service.IntentExtras;

/**
 * Click listener for all topic buttons.
 */
public abstract class TopicButtonsListener {
	protected Context context;

	public TopicButtonsListener(Context context) {
		this.context = context;
	}

	public void onClickLikesCount(View view) {
		Intent intent = new Intent(context, LikesActivity.class);
		intent.putExtra(IntentExtras.CONTENT_EXTRA, (String) view.getTag(R.id.es_keyHandle));
		intent.putExtra(IntentExtras.CONTENT_TYPE, ContentType.TOPIC.toValue());

		context.startActivity(intent);
	}

	public abstract void onClickContent(View view);

	public abstract void onClickCover(View view);

	public abstract void onClickLike(View view);

	public abstract void onClickContextMenu(View view);

	public abstract void onClickCommentsCount(View view);

	public abstract void onClickComment(View view);

	public abstract void onClickPin(View view);

}
