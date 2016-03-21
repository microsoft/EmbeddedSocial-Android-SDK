/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server;


import android.content.Context;

import com.google.gson.Gson;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.data.storage.AccountServiceCachingWrapper;
import com.microsoft.socialplus.data.storage.ActivityServiceCachingWrapper;
import com.microsoft.socialplus.data.storage.ContentServiceCachingWrapper;
import com.microsoft.socialplus.data.storage.NotificationServiceCachingWrapper;
import com.microsoft.socialplus.data.storage.RelationshipServiceCachingWrapper;
import com.microsoft.socialplus.data.storage.SearchServiceCachingWrapper;
import com.microsoft.socialplus.sdk.SocialPlus;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//import retrofit.ErrorHandler;
//import retrofit.RequestInterceptor;
//import retrofit.RetrofitError;
//import retrofit.client.OkClient;
//import retrofit.client.Response;
//import retrofit.converter.ConversionException;
//import retrofit.converter.Converter;
//import retrofit.converter.GsonConverter;
//import retrofit.mime.TypedInput;
//import retrofit.mime.TypedOutput;

/**
 * Wrapper for interactions with REST-services
 */
public final class SocialPlusServiceProvider {

	private static final OkHttpClient HTTP_CLIENT;
	private static final long DEFAULT_READ_WRITE_TIMEOUT = 20; //seconds
	private static final long DEFAULT_CONNECT_TIMEOUT = 15; //seconds

	private final IAccountService accountService;
	private final IActivityService activityService;
	private final IAuthenticationService authenticationService;
	private final IContentService contentService;
	private final INotificationService notificationService;
	private final IRelationshipService relationshipService;
	private final IReportService reportService;
	private final ISearchService searchService;
	private final IImageService imageService;

	static {
		HTTP_CLIENT = new OkHttpClient();
		HTTP_CLIENT.setConnectionPool(new ConnectionPool(0, 1000)); // TODO check this is the correct value
		HTTP_CLIENT.setRetryOnConnectionFailure(false);
		HTTP_CLIENT.setConnectTimeout(DEFAULT_CONNECT_TIMEOUT, TimeUnit.SECONDS);
		HTTP_CLIENT.setReadTimeout(DEFAULT_READ_WRITE_TIMEOUT, TimeUnit.SECONDS);
		HTTP_CLIENT.setWriteTimeout(DEFAULT_READ_WRITE_TIMEOUT, TimeUnit.SECONDS);
	}

	public static OkHttpClient getHttpClient() {
		return HTTP_CLIENT;
	}

	/**
	 * Constructor
	 */
	public SocialPlusServiceProvider(Context context) {

//		OkClient okClient = new OkClient(HTTP_CLIENT);

		SocialPlusServiceErrorHandler errorHandler = new SocialPlusServiceErrorHandler();
		SocialPlusGsonConverter converter = new SocialPlusGsonConverter();
		RetrofitLogger logger = new RetrofitLogger();
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(SocialPlus.API_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
//		RestAdapter restAdapter = new RestAdapter.Builder()
//			.setClient(okClient)
//			.setConverter(converter)
//			.setEndpoint(BuildConfig.SERVER.getServer())
//			.setRequestInterceptor(new SocialPlusRequestInterceptor())
//			.setErrorHandler(errorHandler)
//			.setLogLevel(RestAdapter.LogLevel.FULL)
//			.setLog(logger)
//			.build();

		accountService = new AccountServiceCachingWrapper(retrofit.create(IAccountService.class));
		activityService = new ActivityServiceCachingWrapper(
				retrofit.create(IActivityService.class),
			context
		);
		authenticationService = retrofit.create(IAuthenticationService.class);
		contentService = new ContentServiceCachingWrapper(context, retrofit.create(IContentService.class));
		notificationService = new NotificationServiceCachingWrapper(
			context,
				retrofit.create(INotificationService.class)
		);
		relationshipService = new RelationshipServiceCachingWrapper(
				retrofit.create(IRelationshipService.class));
		reportService = retrofit.create(IReportService.class);
		searchService = new SearchServiceCachingWrapper(retrofit.create(ISearchService.class));

//		RestAdapter imageRestAdapter = new RestAdapter.Builder()
//			.setClient(okClient)
//			.setConverter(converter)
//			.setEndpoint(BuildConfig.SERVER.getServer())
//			.setErrorHandler(errorHandler)
//			.setLogLevel(RestAdapter.LogLevel.HEADERS_AND_ARGS)
//			.setLog(logger)
//			.build();
		imageService = retrofit.create(IImageService.class);
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

	private static class SocialPlusServiceErrorHandler /*implements ErrorHandler */{


		public Throwable handleError(Throwable cause) {
			if (cause.getCause() instanceof IllegalArgumentException) {
				return cause;
			}
//			Response response = cause.getResponse();
//			if (response != null) {
//				switch (response.getStatus()) {
//					case BadRequestException.STATUS_CODE:
//						return new BadRequestException(cause);
//					case UnauthorizedException.STATUS_CODE:
//						return new UnauthorizedException(cause);
//					case NotFoundException.STATUS_CODE:
//						return new NotFoundException(cause);
//					case ResourceAlreadyExistsException.STATUS_CODE:
//						return new ResourceAlreadyExistsException(cause);
//					default:
//						return new ServerException(response.getStatus(), cause);
//				}
//			}
			return new NetworkRequestException(cause);
		}
	}

	private static class RetrofitLogger /*implements RestAdapter.Log*/ {

		public void log(String message) {
			DebugLog.d(message);
		}
	}

	private static class SocialPlusRequestInterceptor /*implements RequestInterceptor*/ {

		private static final String HEADER_CONTENT_TYPE = "Content-Type";
		private static final String HEADER_CONTENT_TYPE_VALUE = "application/json";

//		@Override
//		public void intercept(RequestFacade request) {
//			request.addHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE);
//		}

	}

	/**
	 * Enforces requests to be inherited from {@link com.microsoft.socialplus.server.model.BaseRequest}
	 */
	private static final class SocialPlusGsonConverter /*implements Converter*/ {

//		private final GsonConverter gsonConverter;

		private SocialPlusGsonConverter() {
			Gson gson = GlobalObjectRegistry.getObject(Gson.class);
//			gsonConverter = new GsonConverter(gson);
		}

//		@Override
//		public Object fromBody(TypedInput body, Type type) throws ConversionException {
//			if (type == String.class) {
//				return convertToString(body);
//			} else {
//				return gsonConverter.fromBody(body, type);
//			}
//		}
//
//		private Object convertToString(TypedInput body) throws ConversionException {
//			InputStream inputStream = null;
//			try {
//				inputStream = body.in();
//				return StreamUtils.readFullStream(inputStream);
//			} catch (IOException e) {
//				DebugLog.logException(e);
//				throw new ConversionException(e);
//			} finally {
//				StreamUtils.closeSafely(inputStream);
//			}
//		}
//
//		@Override
//		public TypedOutput toBody(Object object) {
//			if (!(object instanceof BaseRequest)) {
//				throw new IllegalArgumentException("Social Plus requests must be inherited from "
//					+ BaseRequest.class.getSimpleName());
//			} else if (((BaseRequest) object).isCacheOnly()) {
//				throw new IllegalArgumentException("cancel the request that should be accomplished from the cache only");
//			}
//			return gsonConverter.toBody(object);
//		}
	}
}
