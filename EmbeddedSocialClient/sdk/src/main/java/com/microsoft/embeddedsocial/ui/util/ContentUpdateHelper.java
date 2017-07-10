/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.account.AuthorizationCause;
import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.Preferences;
import com.microsoft.embeddedsocial.data.storage.UserActionProxy;
import com.microsoft.embeddedsocial.image.ImageLoader;
import com.microsoft.embeddedsocial.image.ImageLocation;
import com.microsoft.embeddedsocial.image.ImageViewContentLoader;
import com.microsoft.embeddedsocial.pending.PendingLike;
import com.microsoft.embeddedsocial.pending.PendingPin;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.activity.ReportActivity;
import com.microsoft.embeddedsocial.ui.theme.ThemeAttributes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper methods to update topics, comments and replies state.
 */
public final class ContentUpdateHelper {
	private ContentUpdateHelper() {

	}

	public static void launchRemoveTopic(Context context, TopicView topic) {
		new UserActionProxy(context).removeTopic(topic);
	}

	public static void launchRemoveComment(Context context, CommentView commentView) {
		new UserActionProxy(context).removeComment(commentView);
	}

	public static void launchRemoveReply(Context context, ReplyView reply) {
		new UserActionProxy(context).removeReply(reply);
	}

	public static void launchLike(Context context, String contentHandle, ContentType contentType, boolean liked) {
		if (UserAccount.getInstance().checkAuthorization(AuthorizationCause.LIKE)) {
			new UserActionProxy(context).setLikeStatus(contentHandle, contentType, liked);
		} else {
			Preferences.getInstance().setPendingAction(new PendingLike(contentHandle, contentType, liked));
		}
	}

	public static void launchPin(Context context, String topicHandle, boolean pinned) {
		if (UserAccount.getInstance().checkAuthorization(AuthorizationCause.PIN)) {
			new UserActionProxy(context).setPinStatus(topicHandle, pinned);
		} else {
			Preferences.getInstance().setPendingAction(new PendingPin(topicHandle, pinned));
		}
	}

	public static void startContentReport(Context context, String contentHandle, ContentType contentType) {
		Intent intent = new Intent(context, ReportActivity.class);
		intent.putExtra(IntentExtras.REPORT_CONTENT_HANDLE_EXTRA, contentHandle);
		intent.putExtra(IntentExtras.REPORT_CONTENT_TYPE_EXTRA, contentType);
		context.startActivity(intent);
	}

	public static void startUserReport(Context context, String userHandle, String userName) {
		Intent intent = new Intent(context, ReportActivity.class);
		intent.putExtra(IntentExtras.USER_HANDLE, userHandle);
		intent.putExtra(IntentExtras.NAME, userName);
		context.startActivity(intent);
	}

	public static void setProfileImage(Context context, @NonNull ImageViewContentLoader imageViewContentLoader, String photoUrl) {
		int defaultDrawableId = ThemeAttributes.getResourceId(context, R.styleable.es_AppTheme_es_userNoPhotoIcon);
		setProfileImage(imageViewContentLoader, photoUrl, defaultDrawableId);
	}

	public static void setProfileImage(@NonNull ImageViewContentLoader imageViewContentLoader, String photoUrl, @DrawableRes int defaultResId) {
		ImageLocation profileImageLocation = ImageLocation.createUserPhotoImageLocation(photoUrl);
		imageViewContentLoader.cancel();
		if (profileImageLocation != null) {
			final int profileImageWidth = imageViewContentLoader.getImageView().getResources().getDimensionPixelSize(R.dimen.es_user_icon_size);
			imageViewContentLoader.load(profileImageLocation, profileImageWidth);
		} else {
			imageViewContentLoader.setImageResource(defaultResId);
		}
	}

	public static void setProfileImage(@NonNull ImageViewContentLoader imageViewContentLoader, @DrawableRes int imageResId) {
		imageViewContentLoader.setImageResource(imageResId);
	}

	private static final String SEARCH_SCHEME = "search://";
	private static final String HTTP_SCHEME = "http://";
	private static final String HTTPS_SCHEME = "https://";
	private static final Pattern TAG_MATCHER = Pattern.compile("#+[A-Za-z0-9-_]+\\b");

	public static void setTopicBody(Context context, @NonNull TextView topicBody, String topicText) {
		if (TextUtils.isEmpty(topicText)) {
			topicBody.setVisibility(View.GONE);
		} else {
			boolean hasLink = false;
			Spannable spannable = new SpannableString(topicText);
			Matcher matcher = TAG_MATCHER.matcher(spannable);
			while (matcher.find()) {
				hasLink = true;
				final int start = matcher.start();
				final int end = matcher.end();
				spannable.setSpan(new URLSpanNoUnderline(SEARCH_SCHEME + spannable.subSequence(start + 1, end)), start, end, 0);
				spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.es_text_link_color)), start, end, 0);
			}

			Matcher urlMatcher = Patterns.WEB_URL.matcher(spannable);
			while (urlMatcher.find()) {
				hasLink = true;
				final int start = urlMatcher.start();
				final int end = urlMatcher.end();
				String url = "" + spannable.subSequence(start, end);
				if (!url.startsWith(HTTP_SCHEME) && ! url.startsWith(HTTPS_SCHEME)) {
					url = HTTP_SCHEME + url;
				}
				spannable.setSpan(new URLSpan(url), start, end, 0);
			}

			if (hasLink) {
				topicBody.setMovementMethod(LinkMovementMethod.getInstance());
			}
			topicBody.setText(spannable);
			topicBody.setVisibility(View.VISIBLE);
		}
	}

	public static void setTopicAppIcon(@NonNull ImageView postAppIcon, String appIconUrl) {
		ImageLocation postAppIconLocation = ImageLocation.createUserPhotoImageLocation(appIconUrl);

		ImageLoader.cancel(postAppIcon);
		if (postAppIconLocation != null) {
			final int postAppIconWidth = postAppIcon.getResources().getDimensionPixelSize(R.dimen.es_button_icon_size);
			postAppIcon.setVisibility(View.VISIBLE);
			ImageLoader.load(postAppIcon, postAppIconLocation.getUrl(postAppIconWidth));
		} else {
			postAppIcon.setVisibility(View.GONE);
		}
	}

	public static void setTopicCoverImage(@NonNull ImageViewContentLoader imageViewContentLoader, ImageLocation coverImageLocation) {
		imageViewContentLoader.cancel();
		ImageView coverImage = imageViewContentLoader.getImageView();
		if (coverImage == null) {
			return;
		}
		if (coverImageLocation != null) {
			final int coverImageWidth = coverImage.getResources().getDimensionPixelSize(R.dimen.es_card_cover_image_width);
			coverImage.setVisibility(View.VISIBLE);
			imageViewContentLoader.load(coverImageLocation, coverImageWidth);
		} else {
			coverImage.setVisibility(View.GONE);
		}

	}

	private static class URLSpanNoUnderline extends URLSpan {
		public URLSpanNoUnderline(String url) {
			super(url);
		}

		@Override
		public void updateDrawState(@NonNull TextPaint textPaint) {
			super.updateDrawState(textPaint);
			textPaint.setUnderlineText(false);
		}

		@Override
		public void onClick(@NonNull View widget) {
			Uri uri = Uri.parse(getURL());
			Context context = widget.getContext();
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			String packageName = context.getPackageName();
			intent.putExtra(Browser.EXTRA_APPLICATION_ID, packageName);
			intent.setPackage(packageName);
			try {
				context.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				DebugLog.w("Activity was not found for intent, " + intent.toString());
			}
		}
	}

}
