/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.adapter.viewholder;

import android.content.res.Resources;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.autorest.models.PublisherType;
import com.microsoft.socialplus.base.event.EventBus;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.data.Preferences;
import com.microsoft.socialplus.event.click.OpenUserProfileEvent;
import com.microsoft.socialplus.image.ImageViewContentLoader;
import com.microsoft.socialplus.image.UserPhotoLoader;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.sdk.ui.AppProfile;
import com.microsoft.socialplus.server.model.view.ReplyView;
import com.microsoft.socialplus.server.model.view.TopicView;
import com.microsoft.socialplus.server.model.view.UserCompactView;
import com.microsoft.socialplus.ui.util.ContentUpdateHelper;
import com.microsoft.socialplus.ui.util.TimeUtils;

/**
 * ViewHolder part with user info layout
 */
public abstract class UserHeaderViewHolder extends BaseViewHolder {
	protected final int profileImageWidth;

	private ImageView profileImage;
	private ImageViewContentLoader profileContentLoader;
	private TextView profileName;
	protected TextView elapsedTime;
	protected View contextMenuButton;

	private View userHeaderButton;
	private static AppProfile appProfile;

	public UserHeaderViewHolder(View view) {
		super(view);
		this.profileImageWidth = view.getResources().getDimensionPixelSize(R.dimen.sp_user_icon_size);
		initViews(view);
	}

	public static void setAppProfile(AppProfile customAppProfile) {
		appProfile = customAppProfile;
	}

	private void initViews(View view) {
		userHeaderButton = view.findViewById(R.id.sp_userHeaderButton);
		profileImage = (ImageView) view.findViewById(R.id.sp_profileImage);
		profileContentLoader = new UserPhotoLoader(profileImage);
		profileName = (TextView) view.findViewById(R.id.sp_profileName);
		elapsedTime = (TextView) view.findViewById(R.id.sp_postTime);

		contextMenuButton = view.findViewById(R.id.sp_contextMenuButton);
	}

	protected void renderUserHeader(TopicView topic) {
		if (topic.getPublisherType() == PublisherType.USER) {
			setName(topic.getUser().getFirstName(), topic.getUser().getLastName());
			setProfileImage(topic.getUser().getUserPhotoUrl());
			contextMenuButton.setTag(R.id.sp_keyIsOwnContent,
					topic.getUser().getHandle().equals(Preferences.getInstance().getUserHandle()));
			contextMenuButton.setTag(R.id.sp_keyFollowerStatus, topic.getUser().getFollowerStatus());
			contextMenuButton.setTag(R.id.sp_keyUser, topic.getUser());
			userHeaderButton.setTag(R.id.sp_keyUser, topic.getUser());
		} else if (appProfile != null) { // PublisherType.APP
			try {
				profileName.setText(getContext().getString(appProfile.getName()));
			} catch (Resources.NotFoundException e) {
				DebugLog.logException(e);
			}
			try {
				int imageId = appProfile.getImage();
				// Test to ensure the resource exists
				getResources().getResourceName(imageId);
				setProfileImage(imageId);
			} catch (Resources.NotFoundException e) {
				DebugLog.logException(e);
			}
		}
		setTime(topic.getElapsedSeconds());

		contextMenuButton.setTag(R.id.sp_keyTopic, topic);
	}

	protected void renderUserHeader(ReplyView reply) {
		UserCompactView user = reply.getUser();
		setName(user.getFirstName(), user.getLastName());
		setTime(reply.getElapsedSeconds());
		setProfileImage(user.getUserPhotoUrl());

		contextMenuButton.setTag(R.id.sp_keyHandle, reply.getHandle());
		contextMenuButton.setTag(R.id.sp_keyIsOwnContent,
			user.getHandle().equals(Preferences.getInstance().getUserHandle()));
		contextMenuButton.setTag(R.id.sp_keyFollowerStatus, user.getFollowerStatus());
		contextMenuButton.setTag(R.id.sp_keyUser, user);
		userHeaderButton.setTag(R.id.sp_keyUser, user);
	}

	protected void renderUserHeader(UserCompactView user, String topicHandle, long elapsedTime) {
		setName(user.getFirstName(), user.getLastName());
		setTime(elapsedTime);
		setProfileImage(user.getUserPhotoUrl());

		contextMenuButton.setTag(R.id.sp_keyHandle, topicHandle);
		contextMenuButton.setTag(R.id.sp_keyIsOwnContent, UserAccount.getInstance().isCurrentUser(user.getHandle()));
		contextMenuButton.setTag(R.id.sp_keyFollowerStatus, user.getFollowerStatus());
		contextMenuButton.setTag(R.id.sp_keyUser, user);
		userHeaderButton.setTag(R.id.sp_keyUser, user);
	}

	protected void setContextMenuClickListener(View.OnClickListener contextMenuClickListener) {
		contextMenuButton.setOnClickListener(contextMenuClickListener);
	}

	protected void setHeaderClickable(boolean clickable) {
		userHeaderButton.setOnClickListener(clickable ? new UserHeaderClickListener() : null);
		userHeaderButton.setClickable(clickable);
	}

	private void setName(String firstName, String lastName) {
		profileName.setText(String.format("%s %s", firstName, lastName));
	}

	private void setTime(long elapsedSeconds) {
		this.elapsedTime.setText(TimeUtils.secondsToText(this.elapsedTime.getResources(), elapsedSeconds));
	}

	private void setProfileImage(String photoUrl) {
		ContentUpdateHelper.setProfileImage(getContext(), profileContentLoader, photoUrl);
	}

	private void setProfileImage(@DrawableRes int imageResId) {
		ContentUpdateHelper.setProfileImage(profileContentLoader, imageResId);
	}

	private static class UserHeaderClickListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			UserCompactView user = (UserCompactView) view.getTag(R.id.sp_keyUser);
			EventBus.post(new OpenUserProfileEvent(user));
		}
	}

	public enum HolderType {
		/**
		 * For the main page content (top of the page)
		 */
		CONTENT,
		/**
		 * for feed content
		 */
		FEED
	}
}
