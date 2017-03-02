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
import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.server.model.TimedItem;
import com.microsoft.embeddedsocial.server.model.UniqueItem;
import com.microsoft.embeddedsocial.ui.util.TimeUtils;

@DatabaseTable(tableName = DbSchemas.Replies.TABLE_NAME)
public class ReplyView implements Parcelable, UniqueItem, TimedItem {

	@DatabaseField(id = true, columnName = DbSchemas.Replies.REPLY_HANDLE)
	private String replyHandle;

	@DatabaseField(columnName = DbSchemas.Replies.COMMENT_HANDLE)
	private String commentHandle;

	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private UserCompactView user;

	@DatabaseField
	private String replyText;

	@DatabaseField
	private long createdTime;

	@DatabaseField(columnName = DbSchemas.Replies.TOTAL_LIKES)
	private long totalLikes;

	@DatabaseField(columnName = DbSchemas.Replies.LIKE_STATUS)
	private boolean likeStatus;

	private boolean local;

	private int offlineId;

	ReplyView() {  }

	public ReplyView(String commentHandle, String replyHandle, String replyText) {
		this.replyHandle = replyHandle;
		this.commentHandle = commentHandle;
		this.user = UserAccount.getInstance().generateCompactUserView();
		this.replyText = replyText;
		this.createdTime = System.currentTimeMillis();
		this.totalLikes = 0;
		this.likeStatus = false;
	}

	public ReplyView(Parcel in) {
		replyHandle = in.readString();
		commentHandle = in.readString();
		user = in.readParcelable(UserCompactView.class.getClassLoader());
		replyText = in.readString();
		createdTime = in.readLong();
		totalLikes = in.readLong();
		likeStatus = in.readByte() == 1;
		local = in.readByte() == 1;
	}

	public ReplyView(com.microsoft.embeddedsocial.autorest.models.ReplyView view) {
		replyHandle = view.getReplyHandle();
		commentHandle = view.getCommentHandle();
		user = new UserCompactView(view.getUser());
		replyText = view.getText();
		createdTime = view.getCreatedTime().getMillis();
		totalLikes = view.getTotalLikes();
		likeStatus = view.getLiked();
		local = false;
	}

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

	public String getReplyHandle() {
		return replyHandle;
	}

	public String getCommentHandle() {
		return commentHandle;
	}

	@Override
	public String getHandle() {
		return replyHandle;
	}

	public UserCompactView getUser() {
		return user;
	}

	public String getReplyText() {
		return replyText;
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

	public boolean isLikeStatus() {
		return likeStatus;
	}

	public void setLikeStatus(boolean likeStatus) {
		this.likeStatus = likeStatus;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flag) {
		out.writeString(replyHandle);
		out.writeString(commentHandle);
		out.writeParcelable(user, flag);
		out.writeString(replyText);
		out.writeLong(createdTime);
		out.writeLong(totalLikes);
		out.writeByte((byte) (likeStatus ? 1 : 0));
		out.writeByte((byte) (local ? 1 : 0));
	}

	public static final Parcelable.Creator<ReplyView> CREATOR = new Parcelable.Creator<ReplyView>() {
		public ReplyView createFromParcel(Parcel in) {
			return new ReplyView(in);
		}

		public ReplyView[] newArray(int size) {
			return new ReplyView[size];
		}
	};
}
