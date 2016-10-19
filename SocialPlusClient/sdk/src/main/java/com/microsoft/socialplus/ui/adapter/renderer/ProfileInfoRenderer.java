/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.adapter.renderer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.socialplus.autorest.models.FollowerStatus;
import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.base.utils.ViewUtils;
import com.microsoft.socialplus.data.model.AccountData;
import com.microsoft.socialplus.image.ImageLocation;
import com.microsoft.socialplus.image.ImageViewContentLoader;
import com.microsoft.socialplus.image.UserPhotoLoader;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.ui.activity.EditProfileActivity;
import com.microsoft.socialplus.ui.activity.FollowersActivity;
import com.microsoft.socialplus.ui.activity.FollowingActivity;
import com.microsoft.socialplus.ui.adapter.QuantityStringUtils;
import com.microsoft.socialplus.ui.adapter.viewholder.BaseViewHolder;
import com.microsoft.socialplus.ui.theme.ThemeAttributes;
import com.microsoft.socialplus.ui.util.ButtonStyleHelper;

/**
 * Renderer for {@link AccountData}.
 */
public class ProfileInfoRenderer extends Renderer<AccountData, ProfileInfoRenderer.ProfileViewHolder> {
	private final String userHandle;
	private final boolean isCurrentUser;
	private final ButtonStyleHelper styleHelper;
	private final Context context;
	private final RenderType renderType;

	public ProfileInfoRenderer(Context context, String userHandle, RenderType renderType) {
		this.context = context;
		this.userHandle = userHandle;
		this.renderType = renderType;
		isCurrentUser = UserAccount.getInstance().isCurrentUser(userHandle);
		styleHelper = new ButtonStyleHelper(context);
	}

	public void inflatePhoto(ViewGroup root) {
		LayoutInflater inflater = LayoutInflater.from(root.getContext());
		final FrameLayout photoContent = (FrameLayout) root.findViewById(R.id.sp_photoContent);
		if (photoContent == null) {
			return;
		}
		final ViewGroup photoContentParent = (ViewGroup) photoContent.getParent();
		final int index = photoContentParent.indexOfChild(photoContent);
		photoContentParent.removeView(photoContent);
		final int photoLayoutId =
			(renderType == RenderType.LARGE && isTablet()) ? R.layout.sp_view_profile_photo_large : R.layout.sp_view_profile_photo;
		photoContentParent.addView(inflater.inflate(photoLayoutId, root, false), index);
	}

	@Override
	public ProfileViewHolder createViewHolder(ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		final ViewGroup view = (ViewGroup) inflater.inflate(R.layout.sp_profile_brief, parent, false);
		inflatePhoto(view);
		return new ProfileViewHolder(view, userHandle);
	}

	@Override
	protected void onItemRendered(AccountData account, ProfileViewHolder viewHolder) {
		ImageLocation imageLocation = account.getUserPhotoLocation();
		viewHolder.photoContentLoader.cancel();
		Resources resources = context.getResources();
		if (imageLocation != null) {
			int picSize = resources.getDimensionPixelSize(R.dimen.sp_user_pic_large_size);
			viewHolder.photoContentLoader.load(imageLocation, picSize);
		} else {
			viewHolder.photoContentLoader.setImageResource(ThemeAttributes.getResourceId(context, R.styleable.sp_AppTheme_sp_userNoPhotoIcon));
		}
		viewHolder.fullName.setText(account.getFullName());
		viewHolder.bio.setText(account.getBio());
		if (TextUtils.isEmpty(account.getBio())) {
			viewHolder.bio.setVisibility(View.GONE);
		} else {
			viewHolder.bio.setVisibility(View.VISIBLE);
		}
		long followersCount = account.getFollowersCount();
		viewHolder.followers.setText(resources.getQuantityString(R.plurals.sp_button_followers,
				QuantityStringUtils.convertLongToInt(followersCount), followersCount));
		viewHolder.following.setText(context.getString(R.string.sp_button_following, account.getFollowingCount()));
		boolean canReadFollowers = isCurrentUser || account.arePostsReadable();
		viewHolder.followers.setEnabled(canReadFollowers);
		viewHolder.following.setEnabled(canReadFollowers);
		if (!isCurrentUser) {
			initFollowingStatusButton(account, viewHolder);

		}
	}

	private void initFollowingStatusButton(AccountData account, ProfileViewHolder viewHolder) {
		FollowerStatus followedStatus = account.getFollowedStatus();
		if (followedStatus == FollowerStatus.BLOCKED) {
			viewHolder.followingStatus.setVisibility(View.GONE);
		} else {
			viewHolder.followingStatus.setVisibility(View.VISIBLE);
			switch (followedStatus) {
				case PENDING:
					viewHolder.followingStatus.setEnabled(false);
					viewHolder.followingStatus.setText(R.string.sp_pending);
					styleHelper.applyGrayStyle(viewHolder.followingStatus);
					break;
				case FOLLOW:
					viewHolder.followingStatus.setText(R.string.sp_following);
					viewHolder.followingStatus.setEnabled(true);
					viewHolder.followingStatus.setOnClickListener(v -> UserAccount.getInstance().unfollowUser(userHandle));
					styleHelper.applyGreenCompletedStyle(viewHolder.followingStatus);
					break;
				case NONE:
					viewHolder.followingStatus.setText(R.string.sp_button_follow);
					viewHolder.followingStatus.setEnabled(true);
					viewHolder.followingStatus.setOnClickListener(v -> UserAccount.getInstance().followUser(userHandle, account));
					styleHelper.applyGreenStyle(viewHolder.followingStatus);
					break;
			}
		}
	}

	/**
	 * View holder for profile info.
	 */
	public static class ProfileViewHolder extends BaseViewHolder implements View.OnClickListener {

		private String userHandle;

		public final ViewGroup itemView;
		public ImageViewContentLoader photoContentLoader;
		public TextView fullName;
		public TextView bio;
		public TextView followers;
		public TextView following;
		public TextView editProfile;
		public TextView followingStatus;
		public View cardContent;

		public ProfileViewHolder(ViewGroup itemView) {
			super(itemView);
			this.itemView = itemView;
			initViews();
		}

		public void initViews() {
			ImageView photo = ViewUtils.findView(itemView, R.id.sp_photo);
			if (photo == null) {
				return;
			}
			photoContentLoader = new UserPhotoLoader(photo);
			fullName = ViewUtils.findView(itemView, R.id.sp_fullName);
			bio = ViewUtils.findView(itemView, R.id.sp_bio);
			followers = ViewUtils.findView(itemView, R.id.sp_followersCount);
			following = ViewUtils.findView(itemView, R.id.sp_followingCount);
			editProfile = ViewUtils.findView(itemView, R.id.sp_editProfile);
			followingStatus = ViewUtils.findView(itemView, R.id.sp_followingStatus);
			cardContent = ViewUtils.findView(itemView, R.id.sp_cardContent);

			following.setOnClickListener(this);
			followers.setOnClickListener(this);
			editProfile.setOnClickListener(this);
		}

		public ViewGroup getRootView() {
			return itemView;
		}

		public ProfileViewHolder(ViewGroup itemView, String userHandle) {
			this(itemView);
			setUserHandle(userHandle);
		}

		public void setUserHandle(String userHandle) {
			this.userHandle = userHandle;
			boolean isCurrentUser = UserAccount.getInstance().isCurrentUser(userHandle);
			ViewUtils.setVisible(followingStatus, !isCurrentUser);
			ViewUtils.setVisible(editProfile, isCurrentUser);
		}

		@Override
		public void onClick(View view) {
			final Context context = view.getContext();
			int i = view.getId();
			if (i == R.id.sp_editProfile) {
				context.startActivity(new Intent(context, EditProfileActivity.class));

			} else if (i == R.id.sp_followersCount) {
				startUserListActivity(context, FollowersActivity.class);

			} else if (i == R.id.sp_followingCount) {
				startUserListActivity(context, FollowingActivity.class);

			}
		}

		private void startUserListActivity(Context context, Class<?> activityClass) {
			Intent intent = new Intent(context, activityClass);
			intent.putExtra(IntentExtras.USER_HANDLE, userHandle);
			context.startActivity(intent);
		}

	}

	private boolean isTablet() {
		return context.getResources().getBoolean(R.bool.sp_isTablet);
	}

	public enum RenderType {
		SMALL,
		LARGE
	}
}
