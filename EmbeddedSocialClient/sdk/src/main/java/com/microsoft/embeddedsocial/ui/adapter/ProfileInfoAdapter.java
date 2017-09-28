/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.fetcher.base.Fetcher;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.activity.BlockedUsersActivity;
import com.microsoft.embeddedsocial.ui.activity.FollowRequestsActivity;
import com.microsoft.embeddedsocial.ui.adapter.renderer.ProfileInfoRenderer;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.SingleViewHolder;
import com.microsoft.embeddedsocial.autorest.models.FollowerStatus;
import com.microsoft.embeddedsocial.ui.adapter.renderer.Renderer;

import java.util.ArrayList;

/**
 * Adapter for the profile tab on the user's profile page.
 */
public class ProfileInfoAdapter extends MultiTypeAdapter<AccountData, RecyclerView.ViewHolder> {

	private static final int VIEW_TYPE_PROFILE = 0;
	private static final int VIEW_TYPE_PRIVATE_USER_MESSAGE = 1;
	private static final int VIEW_TYPE_BLOCKED_USERS = 2;
	private static final int VIEW_TYPE_FOLLOW_REQUESTS = 3;

	private static final Renderer<Object, SingleViewHolder> PRIVATE_MESSAGE_RENDERER = new Renderer<Object, SingleViewHolder>() {
		@Override
		public SingleViewHolder createViewHolder(ViewGroup parent) {
			return SingleViewHolder.create(R.layout.es_private_user_message, parent);
		}
	};

	private final boolean isCurrentUser;
	private ArrayList<Integer> itemTypes = new ArrayList<>();

	public ProfileInfoAdapter(Context context, Fetcher<AccountData> fetcher, String userHandle) {
		super(fetcher);
		isCurrentUser = UserAccount.getInstance().isCurrentUser(userHandle);

		registerViewType(VIEW_TYPE_PROFILE, new ProfileInfoRenderer(context, userHandle, ProfileInfoRenderer.RenderType.LARGE));
		registerViewType(VIEW_TYPE_PRIVATE_USER_MESSAGE, PRIVATE_MESSAGE_RENDERER, dummyGetMethod());
		registerViewType(VIEW_TYPE_BLOCKED_USERS, createBlockedUsersRenderer(context), dummyGetMethod());
		registerViewType(VIEW_TYPE_FOLLOW_REQUESTS, createFollowRequestsRenderer(context), dummyGetMethod());

		initItemTypes();
		registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onChanged() {
				initItemTypes();
			}
		});
	}

	private ButtonRenderer createBlockedUsersRenderer(Context context) {
		return new ButtonRenderer(
			R.string.es_blocked_users_header,
			v -> context.startActivity(new Intent(context, BlockedUsersActivity.class))
		);
	}

	private ButtonRenderer createFollowRequestsRenderer(Context context) {
		return new ButtonRenderer(
			R.string.es_title_follow_requests,
			v -> context.startActivity(new Intent(context, FollowRequestsActivity.class))
		);
	}

	private void initItemTypes() {
		boolean userRelationsEnabled = GlobalObjectRegistry.getObject(Options.class).userRelationsEnabled();
		itemTypes.clear();
		if (getDataSize() > 0) {
			itemTypes.add(VIEW_TYPE_PROFILE);
			AccountData accountData = getItem(0);

			// Only show blocked users, follow requests, and private profiles if user relations are enabled
			if (userRelationsEnabled) {
				if (isCurrentUser) {
					itemTypes.add(VIEW_TYPE_BLOCKED_USERS);
					if (accountData.isPrivate()) {
						itemTypes.add(VIEW_TYPE_FOLLOW_REQUESTS);
					}
				} else if (accountData.isPrivate() && accountData.getFollowedStatus() != FollowerStatus.FOLLOW) {
					itemTypes.add(VIEW_TYPE_PRIVATE_USER_MESSAGE);
				}
			}
		}
	}

	public void onFollowedStatusChanged(FollowerStatus followedStatus) {
		if (getDataSize() >= 1) {
			AccountData accountData = getItem(0);
			accountData.setFollowedStatus(followedStatus);
			notifyDataSetChanged();
		}
	}

	@Override
	public int getItemCount() {
		return itemTypes.size();
	}

	@Override
	public int getItemViewType(int position) {
		return itemTypes.get(position);
	}

	/**
	 * Renderer for buttons.
	 */
	private static class ButtonRenderer extends Renderer<Object, RecyclerView.ViewHolder> {

		private final int textId;
		private final View.OnClickListener onClickListener;

		ButtonRenderer(int textId, View.OnClickListener onClickListener) {
			this.onClickListener = onClickListener;
			this.textId = textId;
		}

		@Override
		public RecyclerView.ViewHolder createViewHolder(ViewGroup parent) {
			Button button = (Button) ViewUtils.inflateLayout(R.layout.es_profile_button, parent);
			button.setOnClickListener(onClickListener);
			button.setText(textId);
			return new SingleViewHolder(button);
		}
	}

}
