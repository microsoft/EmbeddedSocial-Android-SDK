/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.renderer;

import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.UserListItemHolder;
import com.microsoft.embeddedsocial.ui.util.ButtonStyleHelper;
import com.microsoft.embeddedsocial.ui.util.ContentUpdateHelper;
import com.microsoft.embeddedsocial.ui.util.ProfileOpenHelper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

/**
 * Base implementation of renderer for user list item.
 */
public abstract class BaseUserRenderer extends Renderer<UserCompactView, UserListItemHolder> {

	private final ButtonStyleHelper styleHelper;
	protected final Fragment fragment;
	protected Context context;

	public BaseUserRenderer(Fragment fragment) {
		this.fragment = fragment;
		this.context = fragment.getContext();
		styleHelper = new ButtonStyleHelper(context);
	}

	protected ButtonStyleHelper getStyleHelper() {
		return styleHelper;
	}

	@Override
	protected void onItemRendered(UserCompactView user, UserListItemHolder holder) {
		ContentUpdateHelper.setProfileImage(context, holder.photoContentLoader, user.getUserPhotoUrl());
		holder.fullNameView.setText(user.getFullName());
		holder.itemView.setOnClickListener(v -> ProfileOpenHelper.openUserProfile(v.getContext(), user));
	}

	@Override
	public UserListItemHolder createViewHolder(ViewGroup parent) {
		View view = ViewUtils.inflateLayout(R.layout.es_user_list_item, parent);
		return new UserListItemHolder(view);
	}

}

