/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server;


import android.content.Context;

import com.microsoft.embeddedsocial.data.storage.AccountServiceCachingWrapper;
import com.microsoft.embeddedsocial.data.storage.ActivityServiceCachingWrapper;
import com.microsoft.embeddedsocial.data.storage.AuthenticationServiceWrapper;
import com.microsoft.embeddedsocial.data.storage.BlobServiceWrapper;
import com.microsoft.embeddedsocial.data.storage.ImageServiceWrapper;
import com.microsoft.embeddedsocial.data.storage.RelationshipServiceCachingWrapper;
import com.microsoft.embeddedsocial.data.storage.SearchServiceCachingWrapper;
import com.microsoft.embeddedsocial.data.storage.BuildServiceWrapper;
import com.microsoft.embeddedsocial.data.storage.ContentServiceCachingWrapper;
import com.microsoft.embeddedsocial.data.storage.NotificationServiceCachingWrapper;
import com.microsoft.embeddedsocial.data.storage.ReportServiceWrapper;

/**
 * Wrapper for interactions with REST-services
 */
public final class EmbeddedSocialServiceProvider {

	private final IAccountService accountService;
	private final IActivityService activityService;
	private final IAuthenticationService authenticationService;
	private final IContentService contentService;
	private final INotificationService notificationService;
	private final IRelationshipService relationshipService;
	private final IReportService reportService;
	private final ISearchService searchService;
	private final IImageService imageService;
	private final IBlobService blobService;
	private final IBuildService buildService;

	/**
	 * Constructor
	 */
	public EmbeddedSocialServiceProvider(Context context) {
		accountService = new AccountServiceCachingWrapper();
		activityService = new ActivityServiceCachingWrapper(context);
		authenticationService = new AuthenticationServiceWrapper();
		contentService = new ContentServiceCachingWrapper(context);
		notificationService = new NotificationServiceCachingWrapper(context);
		relationshipService = new RelationshipServiceCachingWrapper();
		reportService = new ReportServiceWrapper();
		searchService = new SearchServiceCachingWrapper();
		imageService = new ImageServiceWrapper();
		blobService = new BlobServiceWrapper();
		buildService = new BuildServiceWrapper();
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

	public IBlobService getBlobService() {
		return blobService;
	}

	public IBuildService getBuildService() {
		return buildService;
	}
}
