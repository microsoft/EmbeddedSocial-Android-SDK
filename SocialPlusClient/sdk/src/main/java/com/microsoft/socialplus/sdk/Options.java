/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.sdk;

import android.text.TextUtils;

import com.microsoft.socialplus.ui.theme.ThemeGroup;

/**
 * Social Plus library options.
 */
@SuppressWarnings("FieldCanBeLocal")
public final class Options {

	private int version = 0;
	private Application application = null;
	private SocialNetworks socialNetworks = null;
	private DrawTheme theme = null;

	private Options() {
	}

	void verify() {
		if (version <= 0) {
			throwInvalidConfigException("invalid version");
		}
		checkValueIsNotNull("application", application);
		checkValueIsNotNull("socialNetworks", socialNetworks);
		checkValueIsNotNull("name", theme);
		checkValueIsNotNull("socialNetworks.facebook", socialNetworks.facebook);
		checkValueIsNotNull("socialNetworks.twitter", socialNetworks.twitter);
		checkValueIsNotNull("socialNetworks.google", socialNetworks.google);
		checkValueIsNotNull("socialNetworks.microsoft", socialNetworks.microsoft);
		checkValueIsNotEmpty("application.appHandle", application.appHandle);
		checkValueIsNotEmpty("application.appToken", application.appToken);
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
		throw new IllegalArgumentException("Invalid SocialPlus configuration file: " + message);
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

	public String getAppHandle() {
		return application.appHandle;
	}

	public String getAppToken() {
		return application.appHandle;
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

		private String appHandle = null;
		private String appToken = null;
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
