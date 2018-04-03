/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils.debug;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Is used to log large entities (such as server requests, responses, files etc.)
 */
public final class EntityLog {

	private static final int BUFFER_SIZE = 16384;
	private static final String DEFAULT_PACKAGE = "default";
	private static final String FOLDER_NAME = "debug_entities";

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final AtomicLong FILE_ID_COUNTER = new AtomicLong(0);
	private static final AtomicBoolean ENABLED = new AtomicBoolean(false);

	private static File BASE_FOLDER;

	static {
		File externalStorage = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
		BASE_FOLDER = new File(externalStorage, DEFAULT_PACKAGE);
	}

	/**
	 * Initializes entity log.
	 *
	 * @param context application context
	 */
	public static void init(Context context) {
		File externalStorage = new File(Environment.getExternalStorageDirectory(), FOLDER_NAME);
		BASE_FOLDER = new File(externalStorage, context.getPackageName());
	}

	/**
	 * Enables or disables the log.
	 *
	 * @param enabled <code>true</code> if the log should be enabled
	 */
	public static void setEnabled(boolean enabled) {
		ENABLED.set(enabled);
	}

	/**
	 * Logs an entity.
	 *
	 * @param entity logging entity
	 */
	public static void logEntity(LoggingEntity entity) {
		if (!ENABLED.get()) {
			return;
		}

		File tagFolder = new File(BASE_FOLDER, entity.tag);
		if (!tagFolder.exists()) {
			tagFolder.mkdirs();
		}

		File entityFile = new File(tagFolder, newFilename(entity.getEntityFilenamePrefix()));

		int readCount = 0;
		byte[] buffer = new byte[BUFFER_SIZE];

		OutputStream output = null;
		try {
			 output= new FileOutputStream(entityFile);
			do {
				readCount = entity.stream.read(buffer);
				if (readCount > 0) {
					output.write(buffer, 0, readCount);
				}
			} while (readCount > 0);

			DebugLog.i(entity.tag + " saved to " + entityFile.getName()
					+ " (" + entityFile.length() + " bytes)");
		} catch (IOException e) {
			DebugLog.logException(e);
		} finally {
			try {
				if (output != null) {
					output.close();
				}
				entity.stream.close();
			} catch (IOException e) {
				DebugLog.logException(e);
			}
		}
	}

	private static String newFilename(String customPrefix) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmssSSS", Locale.getDefault());
		String prefix = TextUtils.isEmpty(customPrefix) ? "" : customPrefix;

		if (customPrefix.matches(".*((\\.\\.)|(/)|(\\\\)).*")) {
			DebugLog.e("entity name prefix " + customPrefix + " rejected:"
					+ " entity name prefixes can't contain path separators");
			prefix = "";
		}

		return prefix + format.format(new Date()) + "-id" + newFileId();
	}

	private static String newFileId() {
		return String.valueOf(FILE_ID_COUNTER.incrementAndGet());
	}

	private EntityLog() {
	}

	/**
	 * Is used to log objects as JSON.
	 */
	public static class JsonLoggingEntity extends LoggingEntity {

		/**
		 * Creates an instance.
		 *
		 * @param data the object to log. Class name of data object
		 *             will be used as entity tag.
		 */
		public JsonLoggingEntity(Object data) {
			super(data.getClass().getSimpleName(), GSON.toJson(data));
		}

		/**
		 * Creates an instance.
		 *
		 * @param tag  entity tag
		 * @param data the object to log
		 */
		public JsonLoggingEntity(String tag, Object data) {
			super(tag, GSON.toJson(data));
		}
	}

	/**
	 * Is used to log network entities (requests and responses from the server).
	 */
	public static class NetworkLoggingEntity extends LoggingEntity {

		private static final int NO_STATUS = -1;
		private static final String NO_CONTENT = "<none>";

		private int httpStatus = NO_STATUS;

		/**
		 * Creates an instance.
		 *
		 * @param url         network URL
		 * @param requestBody body of the request. Might be null.
		 * @param response    response content. Might be null.
		 */
		public NetworkLoggingEntity(String url, String requestBody, String response) {
			super(getTag(url), combineRequestData(requestBody, response));
		}

		private static String combineRequestData(String requestBody, String response) {
			return "==== REQUEST ====\r\n\r\n"
					+ prepareContent(requestBody)
					+ "\r\n\r\n==== END REQUEST ====\r\n"
					+ "\r\n==== RESPONSE ====\r\n\r\n"
					+ prepareContent(response)
					+ "\r\n\r\n==== END RESPONSE ====\r\n";
		}

		private static String prepareContent(String content) {
			return TextUtils.isEmpty(content) ? NO_CONTENT : content;
		}

		/**
		 * Creates network logging entity for request that failed because of
		 * bad HTTP status.
		 *
		 * @param url            request URL
		 * @param requestBody    body of the request
		 * @param httpStatusCode HTTP status code returned by the server
		 * @return {@link NetworkLoggingEntity} instance.
		 */
		public static NetworkLoggingEntity forBadHttpStatus(String url, String requestBody,
															int httpStatusCode) {

			NetworkLoggingEntity entity = new NetworkLoggingEntity(url, requestBody, NO_CONTENT);
			entity.httpStatus = httpStatusCode;
			return entity;
		}

		private static String getTag(String url) {

			String tag = url
					.replaceAll("^http(s?)://", "")
					.replaceAll("\\?.*$", "")
					.replace('/', '_');

			try {
				return URLEncoder.encode(tag, "utf-8");
			} catch (UnsupportedEncodingException e) {
				return "__unknown_url";
			}
		}

		@Override
		protected String getEntityFilenamePrefix() {
			if (httpStatus != NO_STATUS) {
				return "[failed-http" + httpStatus + "] ";
			} else {
				return "";
			}
		}
	}

	/**
	 * A single logging entity (such as a file, server request/response, etc.)
	 */
	public static class LoggingEntity {

		private final String tag;
		private final InputStream stream;

		/**
		 * Creates an instance.
		 *
		 * @param tag  entity tag
		 * @param data entity data
		 */
		public LoggingEntity(String tag, byte[] data) {
			this(tag, new ByteArrayInputStream(data));
		}

		/**
		 * Creates an instance.
		 *
		 * @param tag    entity tag
		 * @param stream entity stream
		 */
		public LoggingEntity(String tag, InputStream stream) {
			this.tag = tag;
			this.stream = stream;
		}

		/**
		 * Creates an instance.
		 *
		 * @param tag  entity tag
		 * @param data entity data file
		 */
		public LoggingEntity(String tag, File data) throws FileNotFoundException {
			this(tag, new FileInputStream(data));
		}

		/**
		 * Creates an instance.
		 *
		 * @param tag  entity tag
		 * @param data entity data
		 */
		public LoggingEntity(String tag, String data) {
			this(tag, stringToBytes(data));
		}

		private static byte[] stringToBytes(String data) {
			return !TextUtils.isEmpty(data) ? data.getBytes() : new byte[0];
		}

		/**
		 * Gets the prefix that will be added to the filename of this entity.
		 * Default implementation returns empty prefix.
		 *
		 * @return filename prefix.
		 */
		protected String getEntityFilenamePrefix() {
			return "";
		}
	}
}
