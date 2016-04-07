/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server;


import android.content.Context;

import com.microsoft.socialplus.data.storage.AccountServiceCachingWrapper;
import com.microsoft.socialplus.data.storage.ActivityServiceCachingWrapper;
import com.microsoft.socialplus.data.storage.AuthenticationServiceWrapper;
import com.microsoft.socialplus.data.storage.ContentServiceCachingWrapper;
import com.microsoft.socialplus.data.storage.ImageServiceWrapper;
import com.microsoft.socialplus.data.storage.NotificationServiceCachingWrapper;
import com.microsoft.socialplus.data.storage.RelationshipServiceCachingWrapper;
import com.microsoft.socialplus.data.storage.ReportServiceWrapper;
import com.microsoft.socialplus.data.storage.SearchServiceCachingWrapper;

/**
 * Wrapper for interactions with REST-services
 */
public final class SocialPlusServiceProvider {

	private final IAccountService accountService;
	private final IActivityService activityService;
	private final IAuthenticationService authenticationService;
	private final IContentService contentService;
	private final INotificationService notificationService;
	private final IRelationshipService relationshipService;
	private final IReportService reportService;
	private final ISearchService searchService;
	private final IImageService imageService;

	/**
	 * Constructor
	 */
	public SocialPlusServiceProvider(Context context) {
		accountService = new AccountServiceCachingWrapper();
		activityService = new ActivityServiceCachingWrapper(context);
		authenticationService = new AuthenticationServiceWrapper();
		contentService = new ContentServiceCachingWrapper(context);
		notificationService = new NotificationServiceCachingWrapper(context);
		relationshipService = new RelationshipServiceCachingWrapper();
		reportService = new ReportServiceWrapper();
		searchService = new SearchServiceCachingWrapper();
		imageService = new ImageServiceWrapper();
	}

	public IAccountService getAccountService() {
		return accountService;
	}

	public IActivityService getActivityService() {
		return activityService;
	}

	public IAuthenticationService getAuthenticationService() {
		return authenticationService;
	}

	public IContentService getContentService() {
		return contentService;
	}

	public INotificationService getNotificationService() {
		return notificationService;
	}

	public IRelationshipService getRelationshipService() {
		return relationshipService;
	}

	public IReportService getReportService() {
		return reportService;
	}

	public ISearchService getSearchService() {
		return searchService;
	}

	public IImageService getImageService() {
		return imageService;
	}
}
