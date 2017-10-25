/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.sdk;

import android.text.TextUtils;

import com.microsoft.embeddedsocial.ui.theme.ThemeGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Embedded Social library options.
 */
@SuppressWarnings("FieldCanBeLocal")
public final class Options {

	private Application application = null;
	private SocialNetworks socialNetworks = null;
	private DrawTheme theme = null;

	private Options() {
	}

	void verify() {
		checkValueIsNotNull("application", application);
		checkValueIsNotNull("socialNetworks", socialNetworks);
		checkValueIsNotNull("name", theme);
		checkValueIsNotNull("socialNetworks.facebook", socialNetworks.facebook);
		checkValueIsNotNull("socialNetworks.twitter", socialNetworks.twitter);
		checkValueIsNotNull("socialNetworks.google", socialNetworks.google);
		checkValueIsNotNull("socialNetworks.microsoft", socialNetworks.microsoft);
		checkValueIsNotEmpty("application.serverUrl", application.serverUrl);
		checkValueIsNotEmpty("application.appKey", application.appKey);
		checkValueIsNotEmpty("socialNetworks.facebook.clientId", socialNetworks.facebook.clientId);
		checkValueIsNotEmpty("socialNetworks.microsoft.clientId", socialNetworks.microsoft.clientId);
		checkValueIsNotEmpty("socialNetworks.google.clientId", socialNetworks.google.clientId);
		checkValueIsNotNull("theme.accentColor", theme.accentColor);

		if (application.numberOfCommentsToShow <= 0) {
			throwInvalidConfigException("application.numberOfCommentsToShow must be greater then 0");
		}
		if (application.numberOfRepliesToShow <= 0) {
			throwInvalidConfigException("application.numberOfRepliesToShow must be greater then 0");
		}
		if (!(socialNetworks.facebook.loginEnabled
			|| socialNetworks.google.loginEnabled
			|| socialNetworks.microsoft.loginEnabled
			|| socialNetworks.twitter.loginEnabled)) {
			throwInvalidConfigException("login via at least one social network must be enabled");
		}
	}

	private void throwInvalidConfigException(String message) {
		throw new IllegalArgumentException("Invalid EmbeddedSocial configuration file: " + message);
	}

	private void checkValueIsNotEmpty(String name, String value) {
		if (TextUtils.isEmpty(value)) {
			throw new IllegalArgumentException("field \"" + name + " must be not empty");
		}
	}

	private void checkValueIsNotNull(String name, Object value) {
		if (value == null) {
			throwInvalidConfigException("field \"" + name + "\" is missed");
		}
	}

	public int getNumberOfCommentsToShow() {
		return application.numberOfCommentsToShow;
	}

	public int getNumberOfRepliesToShow() {
		return application.numberOfRepliesToShow;
	}

	public boolean isFacebookLoginEnabled() {
		return socialNetworks.facebook.loginEnabled;
	}

	public boolean isTwitterLoginEnabled() {
		return socialNetworks.twitter.loginEnabled;
	}

	public boolean isMicrosoftLoginEnabled() {
		return socialNetworks.microsoft.loginEnabled;
	}

	public boolean isGoogleLoginEnabled() {
		return socialNetworks.google.loginEnabled;
	}

	public String getServerUrl() {
		return application.serverUrl;
	}

	public String getAppKey() {
		return application.appKey;
	}

	public void setAppKey(String appKey) {
		application.appKey = appKey;
	}

	public boolean isSearchEnabled() {
		return application.searchEnabled;
	}

	public boolean showGalleryView() {
		return application.showGalleryView;
	}

	public boolean userTopicsEnabled() { return application.userTopicsEnabled; }

	public boolean userRelationsEnabled() { return application.userRelationsEnabled; }

	public List<String> disableNavigationDrawerForActivities() {
		return application.disableNavigationDrawerForActivities;
	}

	public String getFacebookApplicationId() {
		return socialNetworks.facebook.clientId;
	}

	public String getMicrosoftClientId() {
		return socialNetworks.microsoft.clientId;
	}

	public String getGoogleClientId() {
		return socialNetworks.google.clientId;
	}

	public int getAccentColor() {
		return (int) Long.parseLong(theme.accentColor, 16);
	}

	public ThemeGroup getThemeGroup() {
		return theme.name == null ? ThemeGroup.LIGHT : theme.name;
	}

	/**
	 * General application's options.
	 */
	private static class Application {

		private static final int DEFAULT_NUMBER_OF_DISCUSSION_ITEMS = 20;

		private String serverUrl = null;
		private String appKey = null;
		private boolean searchEnabled = true;
		private boolean showGalleryView = true;
		private boolean userTopicsEnabled = true;
		private boolean userRelationsEnabled = true;
		private List<String> disableNavigationDrawerForActivities = new ArrayList<>();
		private int numberOfCommentsToShow = DEFAULT_NUMBER_OF_DISCUSSION_ITEMS;
		private int numberOfRepliesToShow = DEFAULT_NUMBER_OF_DISCUSSION_ITEMS;
	}

	/**
	 * Options for a social network authorization.
	 */
	private static class SocialNetwork {
		private boolean loginEnabled = true;
		private String clientId = null;
	}

	/**
	 * Options for a social networks.
	 */
	@SuppressWarnings("unused")
	private static class SocialNetworks {
		private SocialNetwork facebook;
		private SocialNetwork google;
		private SocialNetwork microsoft;
		private SocialNetwork twitter;
	}

	private class DrawTheme {
		private String accentColor;
		private ThemeGroup name;
	}
}
