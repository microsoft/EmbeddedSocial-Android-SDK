/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.image.ImageViewContentLoader;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.image.UserPhotoLoader;

/**
 * View holder for user list item.
 */
public class UserListItemHolder extends BaseViewHolder {

	public final ImageView photoView;
	public final ImageViewContentLoader photoContentLoader;
	public final TextView fullNameView;
	public final TextView actionButton;
	public final TextView removeFollowerButton;

	public UserListItemHolder(View itemView) {
		super(itemView);
		photoView = ViewUtils.findView(itemView, R.id.es_photo);
		photoContentLoader = new UserPhotoLoader(photoView);
		fullNameView = ViewUtils.findView(itemView, R.id.es_fullName);
		actionButton = ViewUtils.findView(itemView, R.id.es_actionButton);
		removeFollowerButton = ViewUtils.findView(itemView, R.id.es_removeFollowerButton);
	}

}
