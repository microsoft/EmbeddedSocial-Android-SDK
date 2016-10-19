/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.server.model;

import android.text.TextUtils;

import com.microsoft.socialplus.autorest.CommentLikesOperations;
import com.microsoft.socialplus.autorest.CommentLikesOperationsImpl;
import com.microsoft.socialplus.autorest.CommentRepliesOperations;
import com.microsoft.socialplus.autorest.CommentRepliesOperationsImpl;
import com.microsoft.socialplus.autorest.CommentsOperations;
import com.microsoft.socialplus.autorest.CommentsOperationsImpl;
import com.microsoft.socialplus.autorest.MyFollowingOperations;
import com.microsoft.socialplus.autorest.MyFollowingOperationsImpl;
import com.microsoft.socialplus.autorest.RepliesOperations;
import com.microsoft.socialplus.autorest.RepliesOperationsImpl;
import com.microsoft.socialplus.autorest.ReplyLikesOperations;
import com.microsoft.socialplus.autorest.ReplyLikesOperationsImpl;
import com.microsoft.socialplus.autorest.SocialPlusClient;
import com.microsoft.socialplus.autorest.SocialPlusClientImpl;
import com.microsoft.socialplus.autorest.TopicCommentsOperations;
import com.microsoft.socialplus.autorest.TopicCommentsOperationsImpl;
import com.microsoft.socialplus.autorest.TopicLikesOperations;
import com.microsoft.socialplus.autorest.TopicLikesOperationsImpl;
import com.microsoft.socialplus.autorest.TopicsOperations;
import com.microsoft.socialplus.autorest.TopicsOperationsImpl;
import com.microsoft.socialplus.autorest.models.Platform;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.sdk.Options;
import com.microsoft.socialplus.sdk.SocialPlus;
import com.microsoft.socialplus.server.RequestInfoProvider;
import com.microsoft.socialplus.server.exception.BadRequestException;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.exception.NotFoundException;

import java.io.IOException;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
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
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
		httpClient.addInterceptor(logging);

		RETROFIT = new Retrofit.Builder()
				.baseUrl(GlobalObjectRegistry.getObject(Options.class).getServerUrl())
				.addConverterFactory(GsonConverterFactory.create())
				.client(httpClient.build())
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
	protected final Platform platform = Platform.ANDROID;
	private String location;
	private final long time = System.currentTimeMillis();
	private final long sequenceId = System.currentTimeMillis();
	protected final String language;
	private final int networkType;
	protected final String appKey;
	protected final String instanceId;

	private transient boolean useCacheOnly;

	protected BaseRequest() {
		RequestInfoProvider requestInfoProvider = GlobalObjectRegistry.getObject(RequestInfoProvider.class);
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
		appKey = GlobalObjectRegistry.getObject(Options.class).getAppKey();
		instanceId = requestInfoProvider.getInstanceId();
	}

	/**
	 * Launches the request and reads data from the server or cache
	 * @param <Response> generic response type
	 * @return result of contacting the server or cache
	 * @throws ServiceException
	 * @throws IOException
	 */
	public <Response> Response send() throws NetworkRequestException {
		// Fails if specific request does not override
		throw new UnsupportedOperationException();
	}

	protected void checkResponseCode(ServiceResponse serviceResponse) throws NetworkRequestException {
		Response response = serviceResponse.getResponse();
		if (!response.isSuccess()) {
			throw NetworkRequestException.generateException(response.code(), response.message());
		}
	}

	public void forceCacheUsage() {
		useCacheOnly = true;
	}

	public boolean isCacheOnly() {
		return useCacheOnly;
	}
}
