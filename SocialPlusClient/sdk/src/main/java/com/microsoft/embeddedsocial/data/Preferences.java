/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.base.utils.TypeSafeJsonSerializer;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.display.DisplayMethod;
import com.microsoft.embeddedsocial.event.data.UpdateNotificationCountEvent;
import com.microsoft.embeddedsocial.pending.PendingAction;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.server.model.BaseRequest;

/**
 * Contains application preferences and config values. Encapsulates the work with SharedPreferences in the app.
 */
public class Preferences {

	private static final String PREF_FILE_NAME = "embeddedsocial.pref";
	private static final String INSTANCE_ID = "instance_id";
	private static final String USER_HANDLE = "userhandle";
	private static final String NOTIFICATION_COUNT = "notification_count";
	private static final String DISPLAY_APP = "display_app";
	private static final String PENDING_ACTION = "pending_action";
	private static final String DISPLAY_METHOD = "display_method";
	private static final String USE_STAGGERED_LAYOUT_MANAGER = "use_slm";
	private static final String AUTHORIZATION = "authorization";

	private final SharedPreferences sharedPreferences;
	/**
	 * This object converts objects to/from string keeping their exact class.
	 */
	private final TypeSafeJsonSerializer jsonSerializer = new TypeSafeJsonSerializer();

	public Preferences(Context context) {
		sharedPreferences = context.getApplicationContext()
			.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
	}

	/**
	 * Gets a generates app instance id.
	 *
	 * @see BaseRequest
	 */
	public String getInstanceId() {
		return sharedPreferences.getString(INSTANCE_ID, "");
	}

	/**
	 * Stores a generates app instance handle.
	 */
	public void setInstanceId(String instanceId) {
		editor().putString(INSTANCE_ID, instanceId).apply();
	}

	/**
	 * Gets current user's userHandle.
	 */
	public String getUserHandle() {
		return sharedPreferences.getString(USER_HANDLE, null);
	}

	/**
	 * Stores current user's userHandle.
	 */
	public void setUserHandle(String userHandle) {
		editor().putString(USER_HANDLE, userHandle).apply();
	}

	/**
	 * Gets current user's current session token. null if no session exists
	 */
	public String getAuthorizationToken() {
		return sharedPreferences.getString(AUTHORIZATION, null);
	}

	/**
	 * Stores current user's current session token.
	 */
	public void setAuthorizationToken(String authorizationToken) {
		editor().putString(AUTHORIZATION, authorizationToken).apply();
	}

	/**
	 * Stores the current feed display method (list or gallery).
	 */
	public void setDisplayMethod(DisplayMethod displayMethod) {
		editor().putInt(DISPLAY_METHOD, displayMethod.ordinal()).apply();
	}

	/**
	 * Gets the current feed display method (list or gallery).
	 */
	public DisplayMethod getDisplayMethod() {
		Options options = GlobalObjectRegistry.getObject(Options.class);
		if (options != null && options.showGalleryView()) {
			int displayMethodOrdinal = sharedPreferences.getInt(DISPLAY_METHOD, 0);
			if (displayMethodOrdinal < 0 || displayMethodOrdinal >= DisplayMethod.values().length) {
				displayMethodOrdinal = 0;
			}

			return DisplayMethod.values()[displayMethodOrdinal];
		} else {
			return DisplayMethod.LIST;
		}
	}

	/**
	 * Stores the number of unread notification.
	 */
	public void setNotificationCount(long notificationCount) {
		editor().putLong(NOTIFICATION_COUNT, notificationCount).apply();
		EventBus.post(new UpdateNotificationCountEvent());
	}

	private SharedPreferences.Editor editor() {
		return sharedPreferences.edit();
	}

	/**
	 * Sets the number of unread notification to 0.
	 */
	public void resetNotificationCount() {
		setNotificationCount(0);
	}

	/**
	 * Gets the number of unread notification.
	 */
	public long getNotificationCount() {
		return sharedPreferences.getLong(NOTIFICATION_COUNT, 0);
	}

	public void setDisplayApp(boolean isDisplayApp) {
		editor().putBoolean(DISPLAY_APP, isDisplayApp).apply();
	}

	public boolean isDisplayApp() {
		return sharedPreferences.getBoolean(DISPLAY_APP, true);
	}

	/**
	 * Stores an action pending for user authorization.
	 */
	public void setPendingAction(PendingAction postponedAction) {
		editor().putString(PENDING_ACTION, jsonSerializer.valueToString(postponedAction)).apply();
	}

	/**
	 * Deletes the store action pending for user authorization.
	 */
	public void clearPendingAction() {
		editor().remove(PENDING_ACTION).apply();
	}

	/**
	 * Stores the layout manager settings for tablets.
	 */
	// TODO: remove this method when decision is made
	public void setUseStaggeredLayoutManager(boolean use) {
		editor().putBoolean(USE_STAGGERED_LAYOUT_MANAGER, use).apply();
	}

	/**
	 * Gets the layout manager settings for tablets.
	 */
	// TODO: remove this method when decision is made
	public boolean getUseStaggeredLayoutManager() {
		return sharedPreferences.getBoolean(USE_STAGGERED_LAYOUT_MANAGER, true);
	}

	/**
	 * Gets the stored action pending for user authorization.
	 */
	public PendingAction getPendingAction() {
		try {
			String pendingActionString = sharedPreferences.getString(PENDING_ACTION, null);
			return TextUtils.isEmpty(pendingActionString) ? null : jsonSerializer.parseValue(pendingActionString);
		} catch (Exception e) {
			DebugLog.logException(e);
			return null;
		}
	}

	public static Preferences getInstance() {
		return GlobalObjectRegistry.getObject(Preferences.class);
	}
}
