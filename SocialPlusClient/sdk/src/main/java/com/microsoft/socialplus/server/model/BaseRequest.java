/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server.model;

import android.text.TextUtils;

import com.microsoft.autorest.CommentLikesOperations;
import com.microsoft.autorest.CommentLikesOperationsImpl;
import com.microsoft.autorest.CommentRepliesOperations;
import com.microsoft.autorest.CommentRepliesOperationsImpl;
import com.microsoft.autorest.CommentsOperations;
import com.microsoft.autorest.CommentsOperationsImpl;
import com.microsoft.autorest.MyFollowingOperations;
import com.microsoft.autorest.MyFollowingOperationsImpl;
import com.microsoft.autorest.RepliesOperations;
import com.microsoft.autorest.RepliesOperationsImpl;
import com.microsoft.autorest.ReplyLikesOperations;
import com.microsoft.autorest.ReplyLikesOperationsImpl;
import com.microsoft.autorest.SocialPlusClient;
import com.microsoft.autorest.SocialPlusClientImpl;
import com.microsoft.autorest.TopicCommentsOperations;
import com.microsoft.autorest.TopicCommentsOperationsImpl;
import com.microsoft.autorest.TopicLikesOperations;
import com.microsoft.autorest.TopicLikesOperationsImpl;
import com.microsoft.autorest.TopicsOperations;
import com.microsoft.autorest.TopicsOperationsImpl;
import com.microsoft.autorest.models.PlatformType;
import com.microsoft.rest.ServiceException;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.sdk.SocialPlus;
import com.microsoft.socialplus.server.RequestInfoProvider;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 */
public class BaseRequest {
	protected static final Retrofit RETROFIT;
	protected static final SocialPlusClient CLIENT;

	protected static final TopicsOperations TOPICS;
	protected static final MyFollowingOperations FOLLOWING;
	protected static final TopicCommentsOperations TOPIC_COMMENTS;
	protected static final CommentsOperations COMMENTS;
	protected static final RepliesOperations REPLIES;
	protected static final CommentRepliesOperations COMMENT_REPLIES;
	protected static final TopicLikesOperations TOPIC_LIKES;
	protected static final CommentLikesOperations COMMENT_LIKES;
	protected static final ReplyLikesOperations REPLY_LIKES;

	static {
		RETROFIT = new Retrofit.Builder()
				.baseUrl(SocialPlus.API_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		CLIENT = new SocialPlusClientImpl();
		TOPICS = new TopicsOperationsImpl(RETROFIT, CLIENT);
		FOLLOWING = new MyFollowingOperationsImpl(RETROFIT, CLIENT);
		TOPIC_COMMENTS = new TopicCommentsOperationsImpl(RETROFIT, CLIENT);
		COMMENTS = new CommentsOperationsImpl(RETROFIT, CLIENT);
		REPLIES = new RepliesOperationsImpl(RETROFIT, CLIENT);
		COMMENT_REPLIES = new CommentRepliesOperationsImpl(RETROFIT, CLIENT);
		TOPIC_LIKES = new TopicLikesOperationsImpl(RETROFIT, CLIENT);
		COMMENT_LIKES = new CommentLikesOperationsImpl(RETROFIT, CLIENT);
		REPLY_LIKES = new ReplyLikesOperationsImpl(RETROFIT, CLIENT);
	}

	//TODO: init all fields
	private final String instanceHandle;
	protected final String platform = PlatformType.ANDROID.toValue();
	private String location;
	private final long time = System.currentTimeMillis();
	private final long sequenceId = System.currentTimeMillis();
	protected final String language;
	private final int networkType;
	protected final String appKey;
	protected final String instanceID;

	private transient boolean useCacheOnly;

	protected BaseRequest() {
		RequestInfoProvider requestInfoProvider = GlobalObjectRegistry.getObject(RequestInfoProvider.class);
		instanceHandle = requestInfoProvider.getInstanceHandle();
		networkType = requestInfoProvider.getNetworkType();
		Locale locale = Locale.getDefault();
		String language = locale.getLanguage();
		String country = locale.getCountry();
		String localeCode = null;
		if (!TextUtils.isEmpty(language)) {
			if (!TextUtils.isEmpty(country)) {
				localeCode = language + "-" + country;
			} else {
				localeCode = language;
			}
		}
		this.language = localeCode;
		appKey = SocialPlus.APP_KEY; // TODO This should be stored somewhere
		instanceID = "1"; // TODO
	}

	/**
	 * Launches the request and reads data from the server or cache
	 * @param <Response> generic response type
	 * @return result of contacting the server or cache
	 * @throws ServiceException
	 * @throws IOException
	 */
	public <Response> Response send() throws ServiceException, IOException {
		// Fails if specific request does not override
		throw new UnsupportedOperationException();
	}

	public void forceCacheUsage() {
		useCacheOnly = true;
	}

	public boolean isCacheOnly() {
		return useCacheOnly;
	}
}
