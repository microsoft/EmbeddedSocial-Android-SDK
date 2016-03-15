/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model.view;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.data.model.AccountData;
import com.microsoft.socialplus.data.storage.DbSchemas;
import com.microsoft.socialplus.server.model.TimedItem;
import com.microsoft.socialplus.server.model.UniqueItem;

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

	@DatabaseField(columnName = DbSchemas.Comments.ELAPSED_TIME)
	private long elapsedTime;

	@DatabaseField(columnName = DbSchemas.Comments.TOTAL_LIKES)
	private int totalLikes;

	@DatabaseField(columnName = DbSchemas.Comments.TOTAL_REPLIES)
	private int totalReplies;

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

	public void setTotalReplies(int totalReplies) {
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
	public CommentView(String topicHandle, String commentHandle, String contentText) {
		this.commentHandle = commentHandle;
		this.topicHandle = topicHandle;
		this.user = UserAccount.getInstance().generateCompactUserView();
		this.commentText = contentText;
		this.commentBlobType = 0;
		this.commentBlobUrl = null;
		this.elapsedTime = 0;
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
		elapsedTime = in.readLong();
		totalLikes = in.readInt();
		totalReplies = in.readInt();
		likeStatus = in.readByte() == 1;
		local = in.readByte() == 1;
		offlineId = in.readInt();
	}

	public CommentView(com.microsoft.autorest.models.CommentView view) {
		commentHandle = view.getCommentHandle();
		topicHandle = view.getTopicHandle();
		user = new UserCompactView(view.getUser());
		commentText = view.getText();
		commentBlobType = view.getBlobType().ordinal();
		commentBlobUrl = view.getBlobUrl();
		elapsedTime = System.currentTimeMillis() - view.getCreatedTime().getMillis();
		totalLikes = (int)view.getTotalLikes(); //TODO make safe
		totalReplies = (int)view.getTotalReplies(); //TODO make safe
		likeStatus = view.getLiked();
		local = false; //TODO
		offlineId = -1; // TODO
	}

	@Override
	public long getElapsedTime() {
		return elapsedTime;
	}

	public int getTotalLikes() {
		return totalLikes;
	}

	public void setTotalLikes(int totalLikes) {
		this.totalLikes = totalLikes;
	}

	public int getTotalReplies() {
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
		out.writeLong(elapsedTime);
		out.writeInt(totalLikes);
		out.writeInt(totalReplies);
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
