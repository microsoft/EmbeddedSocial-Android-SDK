/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.renderer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.ui.util.ContentUpdateHelper;
import com.microsoft.embeddedsocial.ui.util.ProfileOpenHelper;
import com.microsoft.embeddedsocial.data.model.FollowRequest;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.FollowRequestHolder;

/**
 * Renders follow requests.
 */
public class FollowRequestRenderer extends Renderer<FollowRequest, FollowRequestHolder> {

	public static final float INACTIVE_ALPHA = 0.5f;

	private final Context context;

	public FollowRequestRenderer(Context context) {
		this.context = context;
	}

	@Override
	public FollowRequestHolder createViewHolder(ViewGroup parent) {
		View view = ViewUtils.inflateLayout(R.layout.es_follow_request_list_item, parent);
		return new FollowRequestHolder(view);
	}

	@Override
	protected void onItemRendered(FollowRequest followRequest, FollowRequestHolder holder) {
		UserCompactView user = followRequest.getUser();
		ContentUpdateHelper.setProfileImage(context, holder.photoContentLoader, user.getUserPhotoUrl());
		holder.fullNameView.setText(user.getFullName());
		holder.itemView.setOnClickListener(v -> ProfileOpenHelper.openUserProfile(v.getContext(), user));
		updateButtonsState(followRequest, holder);
	}

	private void updateButtonsState(FollowRequest followRequest, FollowRequestHolder holder) {
		switch (followRequest.getStatus()) {
			case PENDING:
				setButtonActive(holder.rejectButton, v -> {
					followRequest.setStatus(FollowRequest.Status.REJECTED);
					renderRejectedRequest(holder);
					UserAccount.getInstance().rejectFollowRequest(followRequest.getUser().getHandle());
				});
				setButtonActive(holder.acceptButton, v -> {
					followRequest.setStatus(FollowRequest.Status.ACCEPTED);
					renderAcceptedRequest(holder);
					UserAccount.getInstance().acceptFollowRequest(followRequest.getUser().getHandle());
				});
				break;
			case ACCEPTED:
				renderAcceptedRequest(holder);
				break;
			case REJECTED:
				renderRejectedRequest(holder);
				break;
		}
	}

	private void renderRejectedRequest(FollowRequestHolder holder) {
		setButtonInactive(holder.rejectButton);
		setButtonHidden(holder.acceptButton);
	}

	private void renderAcceptedRequest(FollowRequestHolder holder) {
		setButtonInactive(holder.acceptButton);
		setButtonHidden(holder.rejectButton);
	}

	private void setButtonActive(View button, View.OnClickListener onClickListener) {
		button.setVisibility(View.VISIBLE);
		button.setAlpha(1);
		button.setEnabled(true);
		button.setOnClickListener(onClickListener);
	}

	private void setButtonInactive(View button) {
		button.setVisibility(View.VISIBLE);
		button.setAlpha(INACTIVE_ALPHA);
		button.setOnClickListener(null);
		button.setEnabled(false);
	}

	private void setButtonHidden(View button) {
		button.setVisibility(View.GONE);
	}

}
