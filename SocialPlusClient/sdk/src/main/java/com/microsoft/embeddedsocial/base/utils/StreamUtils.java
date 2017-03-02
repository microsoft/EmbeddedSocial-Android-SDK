/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Util methods for work with streams.
 */
public final class StreamUtils {

	private static final long MAX_STREAM_LENGTH = 1024 * 1024;
	private static final int BUFFER_SIZE = 32768;
	private static final String DEFAULT_ENCODING = "UTF-8";

	private StreamUtils() {
	}

	public static void closeSafely(Closeable resource) {
		if (resource != null) {
			try {
				resource.close();
			} catch (IOException e) {
				DebugLog.logException(e);
			}
		}
	}

	public static InputStream openStream(Context context, Uri uri) throws FileNotFoundException {
		ContentResolver contentResolver = context.getContentResolver();
		return contentResolver.openInputStream(uri);
	}


	/**
	 * Reads string from stream.
	 *
	 * @throws IOException if the method failed to read the stream
	 */
	public static String readFullStream(InputStream inputStream) throws IOException {
		return readFullStream(inputStream, DEFAULT_ENCODING, MAX_STREAM_LENGTH);
	}

	/**
	 * Reads the all stream's data and decode it to string.
	 *
	 * @throws IOException if the method failed to read the stream
	 */
	public static String readFullStream(InputStream inputStream, String encoding, long maxStringLength) throws IOException {
		Writer writer = new StringWriter();
		char[] buffer = new char[BUFFER_SIZE];
		InputStreamReader streamReader = new InputStreamReader(inputStream, encoding);
		BufferedReader reader = new BufferedReader(streamReader, BUFFER_SIZE);
		int readCount;
		int totalReadCount = 0;

		do {
			readCount = reader.read(buffer);
			totalReadCount += readCount;
			if (readCount > 0) {
				writer.write(buffer, 0, readCount);
			}
		} while (readCount > 0 && totalReadCount < maxStringLength);

		reader.close();

		return writer.toString();
	}

	/**
	 * Reads the full stream's data (or first <code>maxReadSize</code> byte in
	 * case the stream contains more data) and return it as bytes array.
	 *
	 * @throws IOException if the method failed to read the stream
	 */
	public static byte[] readFullByteStream(InputStream inputStream, long maxReadSize) throws IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		int readCount;
		int totalReadCount = 0;

		do {
			readCount = bis.read(buffer);
			totalReadCount += readCount;
			if (readCount > 0) {
				result.write(buffer, 0, readCount);
			}
		} while (readCount > 0 && totalReadCount < maxReadSize);

		return result.toByteArray();
	}

	/**
	 * Writes the string to the file.
	 *
	 * @throws IOException if the method failed to write the content
	 */
	public static void writeToFile(File file, String content) throws IOException {
		PrintWriter writer = new PrintWriter(file);
		writer.print(content);
		writer.close();
	}
}
