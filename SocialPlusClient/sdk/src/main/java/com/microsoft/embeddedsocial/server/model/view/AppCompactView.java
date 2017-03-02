/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model.view;

import android.os.Parcel;
import android.os.Parcelable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.server.model.UniqueItem;

/**
 *
 */
@DatabaseTable(tableName = DbSchemas.Apps.TABLE_NAME)
public class AppCompactView implements Parcelable, UniqueItem {

	@DatabaseField(id = true)
	private String appHandle;

	@DatabaseField
	private String appName;

	@DatabaseField
	private String appIconUrl;

	@DatabaseField
	private String appDeepLink;

	@DatabaseField
	private String appStoreLink;

	/**
	 * Is used by ORM.
	 */
	@SuppressWarnings("unused")
	AppCompactView() {  }

	@Override
	public String getHandle() {
		return appHandle;
	}

	public String getAppName() {
		return appName;
	}

	public String getAppIconUrl() {
		return appIconUrl;
	}

	public String getAppDeepLink() {
		return appDeepLink;
	}

	public String getAppStoreLink() {
		return appStoreLink;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flag) {
		out.writeString(appHandle);
		out.writeString(appName);
		out.writeString(appIconUrl);
		out.writeString(appDeepLink);
		out.writeString(appStoreLink);
	}

	private AppCompactView(Parcel in) {
		appHandle = in.readString();
		appName = in.readString();
		appIconUrl = in.readString();
		appDeepLink = in.readString();
		appStoreLink = in.readString();
	}

	public AppCompactView(com.microsoft.embeddedsocial.autorest.models.AppCompactView view) {
		appName = view.getName();
		appIconUrl = view.getIconUrl();
		appDeepLink = view.getDeepLink();
		appStoreLink = view.getStoreLink();
	}

	public static final Parcelable.Creator<AppCompactView> CREATOR = new Parcelable.Creator<AppCompactView>() {
		public AppCompactView createFromParcel(Parcel in) {
			return new AppCompactView(in);
		}

		public AppCompactView[] newArray(int size) {
			return new AppCompactView[size];
		}
	};
}
