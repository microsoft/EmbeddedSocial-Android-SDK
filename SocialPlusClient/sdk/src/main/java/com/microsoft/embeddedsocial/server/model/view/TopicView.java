/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.view;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.autorest.models.BlobType;
import com.microsoft.embeddedsocial.autorest.models.PublisherType;
import com.microsoft.embeddedsocial.base.utils.EnumUtils;
import com.microsoft.embeddedsocial.image.ImageLocation;
import com.microsoft.embeddedsocial.server.model.TimedItem;
import com.microsoft.embeddedsocial.server.model.UniqueItem;
import com.microsoft.embeddedsocial.ui.util.TimeUtils;

@DatabaseTable(tableName = DbSchemas.Topics.TABLE_NAME)
public class TopicView implements Parcelable, UniqueItem, TimedItem {

	/**
	 * The prefix that is added to generated local topic handles.
	 */
	public static final String LOCAL_TOPIC_HANDLE_PREFIX = "__local__";

	@DatabaseField(id = true, columnName = DbSchemas.Topics.TOPIC_HANDLE)
	private String topicHandle;

	@DatabaseField
	private int topicType;

	@DatabaseField
	private int publisherType;

	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true,
		columnName = DbSchemas.Topics.USER)
	private UserCompactView user;

	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	private AppCompactView app;

	@DatabaseField
	private String topicCategory;

	@DatabaseField(columnName = DbSchemas.Topics.TOPIC_TITLE)
	private String topicTitle;

	@DatabaseField(columnName = DbSchemas.Topics.TOPIC_TEXT)
	private String topicText;

	@DatabaseField
	private int topicBlobType;

	@DatabaseField
	private String topicBlobUrl;

	@DatabaseField
	private String topicDeepLink;

    @DatabaseField
	private String friendlyName;

	@DatabaseField
	private String group;

	@DatabaseField(columnName = DbSchemas.Topics.CREATED_TIME)
	private long createdTime;

	@DatabaseField(columnName = DbSchemas.Topics.TOTAL_LIKES)
	private long totalLikes;

	@DatabaseField(columnName = DbSchemas.Topics.TOTAL_COMMENTS)
	private long totalComments;

	@DatabaseField(columnName = DbSchemas.Topics.LIKE_STATUS)
	private boolean likeStatus;

	@DatabaseField(columnName = DbSchemas.Topics.PIN_STATUS)
	private boolean pinStatus;

	private AccountData userProfile;

	/**
	 * Marks topics that haven't been uploaded yet. Such topic is synthetic and is NOT persisted.
	 */
	private boolean local;

	private String localImage;

	/**
	 * Id of post data corresponding to a local topic.
	 */
	private int localPostId = -1;

	TopicView() {  }

	public boolean isLocal() {
		return local;
	}

	@Override
	public String getHandle() {
		return topicHandle;
	}

	public PublisherType getPublisherType() {
		return EnumUtils.valueToEnum(PublisherType.class, publisherType);
	}

	public UserCompactView getUser() {
		return user;
	}

	public AppCompactView getApp() {
		return app;
	}

	public String getTopicCategory() {
		return topicCategory;
	}

	public String getTopicTitle() {
		return topicTitle;
	}

	public String getTopicText() {
		return topicText;
	}

	public BlobType getTopicBlobType() {
		return EnumUtils.valueToEnum(BlobType.class, topicBlobType);
	}

	public String getTopicBlobUrl() {
		return topicBlobUrl;
	}

	public String getTopicDeepLink() {
		return topicDeepLink;
	}

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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

	public long getTotalComments() {
		return totalComments;
	}

	public void setTotalComments(long totalComments) {
		this.totalComments = totalComments;
	}

	public boolean isLikeStatus() {
		return likeStatus;
	}

	public void setLikeStatus(boolean likeStatus) {
		this.likeStatus = likeStatus;
	}

	public boolean isPinStatus() {
		return pinStatus;
	}

	public void setPinStatus(boolean pinStatus) {
		this.pinStatus = pinStatus;
	}

	public ImageLocation getImageLocation() {
		if (localImage != null) {
			return ImageLocation.createLocalImageLocation(localImage);
		}
		String url = topicBlobType == BlobType.IMAGE.ordinal() ? topicBlobUrl : null;
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
		out.writeString(topicHandle);
		out.writeInt(topicType);
		out.writeInt(publisherType);
		out.writeParcelable(user, flag);
		out.writeParcelable(app, flag);
		out.writeString(topicCategory);
		out.writeString(topicTitle);
		out.writeString(topicText);
		out.writeInt(topicBlobType);
		out.writeString(topicBlobUrl);
		out.writeString(topicDeepLink);
        out.writeString(friendlyName);
        out.writeString(group);
		out.writeLong(createdTime);
		out.writeLong(totalLikes);
		out.writeLong(totalComments);
		out.writeByte((byte) (likeStatus ? 1 : 0));
		out.writeByte((byte) (pinStatus ? 1 : 0));
		out.writeInt(local ? 1 : 0);
		out.writeInt(localPostId);
	}

	private TopicView(Parcel in) {
		topicHandle = in.readString();
		topicType = in.readInt();
		publisherType = in.readInt();
		user = in.readParcelable(UserCompactView.class.getClassLoader());
		app = in.readParcelable(AppCompactView.class.getClassLoader());
		topicCategory = in.readString();
		topicTitle = in.readString();
		topicText = in.readString();
		topicBlobType = in.readInt();
		topicBlobUrl = in.readString();
		topicDeepLink = in.readString();
        friendlyName = in.readString();
        group = in.readString();
		createdTime = in.readLong();
		totalLikes = in.readLong();
		totalComments = in.readLong();
		likeStatus = in.readByte() == 1;
		pinStatus = in.readByte() == 1;
		local = in.readInt() != 0;
		localPostId = in.readInt();
	}

	private void setApp(AppCompactView app) {
		this.app = app;
	}

	private void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}

	private void setPublisherType(int publisherType) {
		this.publisherType = publisherType;
	}

	private void setTopicBlobType(int topicBlobType) {
		this.topicBlobType = topicBlobType;
	}

	public void setTopicBlobUrl(String topicBlobUrl) {
		this.topicBlobUrl = topicBlobUrl;
	}

	public void setLocalImage(String localImage) {
		this.localImage = localImage;
	}

	private void setTopicCategory(String topicCategory) {
		this.topicCategory = topicCategory;
	}

	private void setTopicDeepLink(String topicDeepLink) {
		this.topicDeepLink = topicDeepLink;
	}

	private void setTopicHandle(String topicHandle) {
		this.topicHandle = topicHandle;
	}

	public void setTopicText(String topicText) {
		this.topicText = topicText;
	}

	public void setTopicTitle(String topicTitle) {
		this.topicTitle = topicTitle;
	}

	private void setTopicType(int topicType) {
		this.topicType = topicType;
	}

	private void setUser(UserCompactView user) {
		this.user = user;
	}

	private void setLocalPostId(int localPostId) {
		this.localPostId = localPostId;
	}

	public int getLocalPostId() {
		return localPostId;
	}

	public AccountData getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(AccountData userProfile) {
		this.userProfile = userProfile;
	}

	public static final Parcelable.Creator<TopicView> CREATOR = new Parcelable.Creator<TopicView>() {
		public TopicView createFromParcel(Parcel in) {
			return new TopicView(in);
		}

		public TopicView[] newArray(int size) {
			return new TopicView[size];
		}
	};

	public TopicView(com.microsoft.embeddedsocial.autorest.models.TopicView view) {
		topicHandle = view.getTopicHandle();
		publisherType = view.getPublisherType().ordinal();
		if (getPublisherType() == PublisherType.USER) {
			user = new UserCompactView(view.getUser());
		}
		app = new AppCompactView(view.getApp());
		topicCategory = view.getCategories();
		topicTitle = view.getTitle();
		topicText = view.getText();
		topicBlobType = view.getBlobType().ordinal();
		topicBlobUrl = view.getBlobUrl();
		topicDeepLink = view.getDeepLink();
        friendlyName = view.getFriendlyName();
        group = view.getGroup();
		createdTime = view.getCreatedTime().getMillis();
		totalLikes = view.getTotalLikes();
		totalComments = view.getTotalComments();
		likeStatus = view.getLiked();
		pinStatus = view.getPinned();
		local = false; //TODO
		localPostId = 0; //TODO make -1 when it is local
	}

	/**
	 * Is used to build {@linkplain TopicView} instances.
	 */
	public static class Builder {

		private final TopicView topic = new TopicView();

		public TopicView build() {
			return topic;
		}

		public Builder setApp(AppCompactView app) {
			topic.setApp(app);
			return this;
		}

		public Builder setCreatedTime(long createdTime) {
			topic.setCreatedTime(createdTime);
			return this;
		}

		public Builder setLikeStatus(boolean likeStatus) {
			topic.setLikeStatus(likeStatus);
			return this;
		}

		public Builder setLocal(boolean local) {
			topic.setLocal(local);
			return this;
		}

		public Builder setPinStatus(boolean pinStatus) {
			topic.setPinStatus(pinStatus);
			return this;
		}

		public Builder setPublisherType(int publisherType) {
			topic.setPublisherType(publisherType);
			return this;
		}

		public Builder setTopicBlobType(int topicBlobType) {
			topic.setTopicBlobType(topicBlobType);
			return this;
		}

		public Builder setTopicBlobUrl(String topicBlobUrl) {
			topic.setTopicBlobUrl(topicBlobUrl);
			return this;
		}

		public Builder setTopicCategory(String topicCategory) {
			topic.setTopicCategory(topicCategory);
			return this;
		}

        public Builder setTopicDeepLink(String topicDeepLink) {
            topic.setTopicDeepLink(topicDeepLink);
            return this;
        }

        public Builder setFriendlyName(String friendlyName) {
            topic.setFriendlyName(friendlyName);
            return this;
        }

        public Builder setGroup(String group) {
            topic.setGroup(group);
            return this;
        }

		public Builder setTopicHandle(String topicHandle) {
			topic.setTopicHandle(topicHandle);
			return this;
		}

		public Builder setTopicText(String topicText) {
			topic.setTopicText(topicText);
			return this;
		}

		public Builder setTopicTitle(String topicTitle) {
			topic.setTopicTitle(topicTitle);
			return this;
		}

		public Builder setTopicType(int topicType) {
			topic.setTopicType(topicType);
			return this;
		}

		public Builder setTotalComments(long totalComments) {
			topic.setTotalComments(totalComments);
			return this;
		}

		public Builder setTotalLikes(long totalLikes) {
			topic.setTotalLikes(totalLikes);
			return this;
		}

		public Builder setUser(UserCompactView user) {
			topic.setUser(user);
			return this;
		}

		public Builder setLocalPostId(int localPostId) {
			topic.setLocalPostId(localPostId);
			return this;
		}
	}
}
