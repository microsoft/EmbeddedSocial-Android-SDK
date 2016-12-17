/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.view;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.autorest.models.BlobType;
import com.microsoft.embeddedsocial.image.ImageLocation;
import com.microsoft.embeddedsocial.server.model.TimedItem;
import com.microsoft.embeddedsocial.server.model.UniqueItem;
import com.microsoft.embeddedsocial.ui.util.TimeUtils;

/**
 *
 */
@DatabaseTable(tableName = DbSchemas.Comments.TABLE_NAME)
public class CommentView implements Parcelable, UniqueItem, TimedItem {

	@DatabaseField(id = true, columnName = DbSchemas.Comments.COMMENT_HANDLE)
	private String commentHandle;

	@DatabaseField(columnName = DbSchemas.Comments.TOPIC_HANDLE)
	private String topicHandle;

	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private UserCompactView user;

	@DatabaseField
	private String commentText;

	@DatabaseField
	private int commentBlobType;

	@DatabaseField
	private String commentBlobUrl;

	@DatabaseField(columnName = DbSchemas.Comments.CREATED_TIME)
	private long createdTime;

	@DatabaseField(columnName = DbSchemas.Comments.TOTAL_LIKES)
	private long totalLikes;

	@DatabaseField(columnName = DbSchemas.Comments.TOTAL_REPLIES)
	private long totalReplies;

	@DatabaseField(columnName = DbSchemas.Comments.LIKE_STATUS)
	private boolean likeStatus;

	private boolean local = false;

	private int offlineId;

	private AccountData userProfile;

	/**
	 * For ORM.
	 */
	CommentView() {  }

	public boolean isLocal() {
		return local;
	}

	public void setLocal(int offlineId) {
		local = true;
		this.offlineId = offlineId;
	}

	public int getOfflineId() {
		return offlineId;
	}

	public String getHandle() {
		return commentHandle;
	}

	public void setTotalReplies(long totalReplies) {
		this.totalReplies = totalReplies;
	}

	public String getTopicHandle() {
		return topicHandle;
	}

	public UserCompactView getUser() {
		return user;
	}

	public String getCommentText() {
		return commentText;
	}

	/**
	 * For update fetcher.
	 */
	public CommentView(String topicHandle, String commentHandle, String contentText, String blobUrl) {
		this.commentHandle = commentHandle;
		this.topicHandle = topicHandle;
		this.user = UserAccount.getInstance().generateCompactUserView();
		this.commentText = contentText;
		this.commentBlobType = 0;
		this.commentBlobUrl = blobUrl;
		this.createdTime = System.currentTimeMillis();
		this.totalLikes = 0;
		this.totalReplies = 0;
		this.likeStatus = false;
	}

	public CommentView(Parcel in) {
		commentHandle = in.readString();
		topicHandle = in.readString();
		user = in.readParcelable(UserCompactView.class.getClassLoader());
		commentText = in.readString();
		commentBlobType = in.readInt();
		commentBlobUrl = in.readString();
		createdTime = in.readLong();
		totalLikes = in.readLong();
		totalReplies = in.readLong();
		likeStatus = in.readByte() == 1;
		local = in.readByte() == 1;
		offlineId = in.readInt();
	}

	public CommentView(com.microsoft.embeddedsocial.autorest.models.CommentView view) {
		commentHandle = view.getCommentHandle();
		topicHandle = view.getTopicHandle();
		com.microsoft.embeddedsocial.autorest.models.UserCompactView viewUser = view.getUser();
		if (viewUser != null) {
			user = new UserCompactView(viewUser);
		}
		commentText = view.getText();
		commentBlobType = view.getBlobType().ordinal();
		commentBlobUrl = view.getBlobUrl();
		createdTime = view.getCreatedTime().getMillis();
		totalLikes = view.getTotalLikes();
		totalReplies = view.getTotalReplies();
		likeStatus = view.getLiked();
		local = false; //TODO
		offlineId = -1; // TODO
	}

	@Override
	public long getElapsedSeconds() {
		return TimeUtils.elapsedSeconds(createdTime);
	}

	public long getTotalLikes() {
		return totalLikes;
	}

	public void setTotalLikes(long totalLikes) {
		this.totalLikes = totalLikes;
	}

	public long getTotalReplies() {
		return totalReplies;
	}

	public boolean isLikeStatus() {
		return likeStatus;
	}

	public void setLikeStatus(boolean likeStatus) {
		this.likeStatus = likeStatus;
	}

	public AccountData getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(AccountData userProfile) {
		this.userProfile = userProfile;
	}

	public ImageLocation getImageLocation() {
		String url = commentBlobType == BlobType.IMAGE.ordinal() ? commentBlobUrl : null;
		if (local) {
			return ImageLocation.createLocalImageLocation(url);
		} else {
			return ImageLocation.createTopicImageLocation(url);
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flag) {
		out.writeString(commentHandle);
		out.writeString(topicHandle);
		out.writeParcelable(user, flag);
		out.writeString(commentText);
		out.writeInt(commentBlobType);
		out.writeString(commentBlobUrl);
		out.writeLong(createdTime);
		out.writeLong(totalLikes);
		out.writeLong(totalReplies);
		out.writeByte((byte) (likeStatus ? 1 : 0));
		out.writeByte((byte) (local ? 1 : 0));
		out.writeInt(offlineId);
	}

	public static final Parcelable.Creator<CommentView> CREATOR = new Parcelable.Creator<CommentView>() {
		public CommentView createFromParcel(Parcel in) {
			return new CommentView(in);
		}

		public CommentView[] newArray(int size) {
			return new CommentView[size];
		}
	};

}
