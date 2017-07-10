/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.viewholder;

import android.view.View;

import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.sdk.R;

/**
 * View holder for follow requests.
 */
public class FollowRequestHolder extends UserListItemHolder {

	public final View acceptButton;
	public final View rejectButton;

	public FollowRequestHolder(View itemView) {
		super(itemView);
		acceptButton = ViewUtils.findView(itemView, R.id.es_acceptButton);
		rejectButton = ViewUtils.findView(itemView, R.id.es_rejectButton);
	}
}
