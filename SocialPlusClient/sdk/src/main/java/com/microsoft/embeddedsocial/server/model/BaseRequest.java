/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.model;

import android.text.TextUtils;

import com.microsoft.embeddedsocial.sdk.BuildConfig;
import com.microsoft.embeddedsocial.autorest.CommentLikesOperations;
import com.microsoft.embeddedsocial.autorest.CommentLikesOperationsImpl;
import com.microsoft.embeddedsocial.autorest.CommentRepliesOperations;
import com.microsoft.embeddedsocial.autorest.CommentRepliesOperationsImpl;
import com.microsoft.embeddedsocial.autorest.CommentsOperations;
import com.microsoft.embeddedsocial.autorest.CommentsOperationsImpl;
import com.microsoft.embeddedsocial.autorest.MyFollowingOperations;
import com.microsoft.embeddedsocial.autorest.MyFollowingOperationsImpl;
import com.microsoft.embeddedsocial.autorest.RepliesOperations;
import com.microsoft.embeddedsocial.autorest.RepliesOperationsImpl;
import com.microsoft.embeddedsocial.autorest.ReplyLikesOperations;
import com.microsoft.embeddedsocial.autorest.ReplyLikesOperationsImpl;
import com.microsoft.embeddedsocial.autorest.EmbeddedSocialClient;
import com.microsoft.embeddedsocial.autorest.EmbeddedSocialClientImpl;
import com.microsoft.embeddedsocial.autorest.TopicCommentsOperations;
import com.microsoft.embeddedsocial.autorest.TopicCommentsOperationsImpl;
import com.microsoft.embeddedsocial.autorest.TopicLikesOperations;
import com.microsoft.embeddedsocial.autorest.TopicLikesOperationsImpl;
import com.microsoft.embeddedsocial.autorest.TopicsOperations;
import com.microsoft.embeddedsocial.autorest.TopicsOperationsImpl;
import com.microsoft.embeddedsocial.autorest.models.Platform;
import com.microsoft.rest.ServiceException;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.server.RequestInfoProvider;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;

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
	protected static final EmbeddedSocialClient CLIENT;

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
		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

		if (BuildConfig.DEBUG) {
			// add http logging if this is a debug build
			HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
			logging.setLevel(HttpLoggingInterceptor.Level.BODY);
			httpClient.addInterceptor(logging);
		}

		RETROFIT = new Retrofit.Builder()
				.baseUrl(GlobalObjectRegistry.getObject(Options.class).getServerUrl())
				.addConverterFactory(GsonConverterFactory.create())
				.client(httpClient.build())
				.build();
		CLIENT = new EmbeddedSocialClientImpl();
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
