/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.renderer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.image.ImageLoader;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.ActivityView;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.ui.util.ContentUpdateHelper;
import com.microsoft.embeddedsocial.autorest.models.ActivityType;
import com.microsoft.embeddedsocial.autorest.models.BlobType;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.image.ImageLocation;
import com.microsoft.embeddedsocial.image.ImageViewContentLoader;
import com.microsoft.embeddedsocial.image.UserPhotoLoader;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.BaseViewHolder;
import com.microsoft.embeddedsocial.ui.util.OnActivityItemClickListener;
import com.microsoft.embeddedsocial.ui.util.TimeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base recent activity renderer.
 */
abstract class BaseRecentActivityRenderer extends Renderer<ActivityView, UserRecentActivityRenderer.ViewHolder> {

	protected static final Map<ActivityType, Integer> DECORATION_BY_TYPE = new HashMap<>();
	private static final Typeface TYPEFACE_BOLD = Typeface.defaultFromStyle(Typeface.BOLD);
	private static final Typeface TYPEFACE_NORMAL = Typeface.defaultFromStyle(Typeface.NORMAL);

	static {
		DECORATION_BY_TYPE.put(ActivityType.LIKE, R.drawable.es_decor_like);
		DECORATION_BY_TYPE.put(ActivityType.FOLLOWACCEPT, R.drawable.es_decor_follow);
		DECORATION_BY_TYPE.put(ActivityType.FOLLOWREQUEST, R.drawable.es_decor_follow);
		DECORATION_BY_TYPE.put(ActivityType.FOLLOWING, R.drawable.es_decor_follow);
		DECORATION_BY_TYPE.put(ActivityType.COMMENTPEER, R.drawable.es_decor_comment);
		DECORATION_BY_TYPE.put(ActivityType.REPLYPEER, R.drawable.es_decor_comment);
		DECORATION_BY_TYPE.put(ActivityType.COMMENT, R.drawable.es_decor_comment);
		DECORATION_BY_TYPE.put(ActivityType.REPLY, R.drawable.es_decor_comment);
	}

	@Override
	public ViewHolder createViewHolder(ViewGroup parent) {
		View view = ViewUtils.inflateLayout(R.layout.es_recent_activity_list_item, parent);
		return new ViewHolder(view);
	}

	@Override
	protected void onItemRendered(ActivityView item, ViewHolder holder) {
		holder.reset();
		renderCommonInfo(item, holder);
		renderSpecificInfo(item, holder);
	}

	private void renderCommonInfo(ActivityView item, ViewHolder holder) {
		Resources resources = holder.getResources();

		holder.timestamp.setText(TimeUtils.secondsToText(resources, item.getElapsedSeconds()));

		holder.itemView.setOnClickListener(new OnActivityItemClickListener(item));

		renderContentImage(item, holder, resources);

		Typeface typeface = item.isUnread() ? TYPEFACE_BOLD : TYPEFACE_NORMAL;
		holder.text.setTypeface(typeface);

		UserCompactView user = item.getActorUsers().get(0);
		ContentUpdateHelper.setProfileImage(holder.getContext(), holder.photoContentLoader, user.getUserPhotoUrl());

		Integer decoration = DECORATION_BY_TYPE.get(item.getActivityType());
		holder.decoration.setImageResource(decoration != null ? decoration : -1);
	}

	private void renderContentImage(ActivityView item, ViewHolder holder, Resources resources) {
		String imageUrl = item.getActedOnContentBlobUrl();
		if (item.getActedOnContentBlobType() == BlobType.IMAGE && !TextUtils.isEmpty(imageUrl)) {
			ImageLocation imageLocation = ImageLocation.createTopicImageLocation(imageUrl);
			ImageLoader.load(holder.blobImage, imageLocation.getUrl(resources.getDimensionPixelSize(R.dimen.es_user_icon_size)));
		} else {
			holder.blobImage.setVisibility(View.GONE);
		}
	}

	private void renderSpecificInfo(ActivityView item, ViewHolder holder) {
		ActivityType activityType = item.getActivityType();
		Context context = holder.getContext();
		switch (activityType) {
			case FOLLOWING:
				renderFollowingEvent(context, item, holder);
				break;
			case LIKE:
				renderLike(context, item, holder);
				break;
			case FOLLOWREQUEST:
				renderFollowRequest(context, item, holder);
				break;
			case COMMENTPEER:
			case REPLYPEER:
				renderChildPeer(context, item, holder);
				break;
			case COMMENT:
			case REPLY:
				renderChild(context, item, holder);
				break;
			case FOLLOWACCEPT:
				renderFollowAccepted(context, item, holder);
				break;
			default:
				DebugLog.e("Don't know how to render activity of type " + item.getActivityType());
				break;
		}
	}

	protected abstract void renderFollowingEvent(Context context, ActivityView item, ViewHolder holder);

	protected abstract void renderMention(Context context, ActivityView item, ViewHolder holder);

	protected abstract void renderLike(Context context, ActivityView item, ViewHolder holder);

	protected abstract void renderFollowRequest(Context context, ActivityView item, ViewHolder holder);

	protected abstract void renderChildPeer(Context context, ActivityView item, ViewHolder holder);

	protected abstract void renderChild(Context context, ActivityView item, ViewHolder holder);

	protected abstract void renderFollowAccepted(Context context, ActivityView item, ViewHolder holder);

	protected String formatActors(Context context, ActivityView activity) {
		List<UserCompactView> actorUsers = activity.getActorUsers();
		switch (activity.getCount()) {
			case 1:
				return context.getString(R.string.es_one_person, actorUsers.get(0).getFullName());
			case 2:
				return context.getString(R.string.es_two_persons, actorUsers.get(0).getFullName(), actorUsers.get(1).getFullName());
			case 3:
				return context.getString(R.string.es_three_persons, actorUsers.get(0).getFullName(), actorUsers.get(1).getFullName());
			default:
				return context.getString(R.string.es_many_persons, actorUsers.get(0).getFullName(), actorUsers.get(1).getFullName(), activity.getCount() - 2);
		}
	}

	/**
	 * View holder.
	 */
	protected static class ViewHolder extends BaseViewHolder {

		public final ImageViewContentLoader photoContentLoader;
		public final TextView text;
		public final TextView timestamp;
		public final ImageView blobImage;
		public final ImageView decoration;

		public ViewHolder(View itemView) {
			super(itemView);
			ImageView userPhoto = ViewUtils.findView(itemView, R.id.es_photo);
			photoContentLoader = new UserPhotoLoader(userPhoto);
			text = ViewUtils.findView(itemView, R.id.es_text);
			timestamp = ViewUtils.findView(itemView, R.id.es_timestamp);
			blobImage = ViewUtils.findView(itemView, R.id.es_blobImage);
			decoration = ViewUtils.findView(itemView, R.id.es_decoration);
		}

		public void reset() {
			blobImage.setVisibility(View.VISIBLE);
			text.setText("");
		}
	}
}
