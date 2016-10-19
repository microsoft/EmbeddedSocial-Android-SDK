/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.adapter.renderer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.socialplus.base.utils.ViewUtils;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.model.view.UserCompactView;
import com.microsoft.socialplus.ui.adapter.viewholder.UserListItemHolder;
import com.microsoft.socialplus.ui.util.ButtonStyleHelper;
import com.microsoft.socialplus.ui.util.ContentUpdateHelper;
import com.microsoft.socialplus.ui.util.ProfileOpenHelper;

/**
 * Base implementation of renderer for user list item.
 */
public abstract class BaseUserRenderer extends Renderer<UserCompactView, UserListItemHolder> {

	private final ButtonStyleHelper styleHelper;
	protected Context context;

	public BaseUserRenderer(Context context) {
		this.context = context;
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
		View view = ViewUtils.inflateLayout(R.layout.sp_user_list_item, parent);
		return new UserListItemHolder(view);
	}

}

