/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils.debug;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enables logging to system log/sdcard file.
 */
public final class DebugLog {

	public static final String LOG_DIRECTORY = "debug_logs";
	public static final String LOG_EXTENSION = ".txt";

	private static final String LOG_FIELD_SEPARATOR = " | ";
	private static final String UNKNOWN_SIGNATURE = "[unknown]";
	private static final String DEFAULT_PACKAGE_NAME = "default";
	private static final String NO_APPLICATION_INFO_MESSAGE = "[No application info]";

	private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss.SSS] ", Locale.getDefault());

	private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("([A-Z]*|(^[a-z]))[_\\da-z\\$]*");

	private static final Gson JSON_SERIALIZER = new GsonBuilder()
			.disableHtmlEscaping()
			.setPrettyPrinting()
			.serializeNulls()
			.create();

	private static boolean enabled = false;
	private static boolean echoEnabled = false;

	private static Level savingLevel = Level.Verbose;
	private static Level echoLevel = Level.Verbose;

	private static String packageName = DEFAULT_PACKAGE_NAME;

	/**
	 * Logging level.
	 */
	public enum Level {
		Verbose("V"),
		Debug("D"),
		Information("I"),
		Warning("W"),
		Error("E");

		private final String label;

		Level(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return label;
		}
	}

	private DebugLog() {
	}

	/**
	 * Prepares debug log for usage.
	 *
	 * @param context valid context
	 */
	public static synchronized void prepare(Context context) {
		if (context != null) {
			packageName = context.getPackageName();
			printVersionInfo(context);
		} else {
			packageName = DEFAULT_PACKAGE_NAME;
		}
	}

	private static void printVersionInfo(Context context) {
		PackageManager packageMgr = context.getPackageManager();
		String message;

		try {
			PackageInfo packageInfo = packageMgr.getPackageInfo(packageName, 0);
			int appLabelId = packageInfo.applicationInfo.labelRes;
			message = (appLabelId != 0 ? context.getString(appLabelId) : UNKNOWN_SIGNATURE) + " v" + packageInfo.versionName + " #" + packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			DebugLog.logException(e);
			message = NO_APPLICATION_INFO_MESSAGE;
		}

		DebugLog.i("APP_INFO", message);
	}

	/**
	 * Writes tag and message to the log file and also calls Log.d with given params.
	 */
	public static void d(String tag, String message) {
		logMessage(tag, message, Level.Debug);
	}

	/**
	 * Writes tag and message to the log file and also calls Log.w with given params.
	 */
	public static void w(String tag, String message) {
		logMessage(tag, message, Level.Warning);
	}

	/**
	 * Writes tag and message to the log file and also calls Log.e with given params.
	 */
	public static void e(String tag, String message) {
		logMessage(tag, message, Level.Error);
	}

	/**
	 * Writes tag and message to the log file and also calls Log.i with given params.
	 */
	public static void i(String tag, String message) {
		logMessage(tag, message, Level.Information);
	}

	/**
	 * Writes a message about exception to system log and to the file.
	 *
	 * @param tag   tag string
	 * @param where a string describing where the exception happened
	 * @param e     an exception which class name and message will be saved in log
	 */
	public static void e(String tag, String where, Throwable e) {
		StringBuilder builder = new StringBuilder("Exception in ").append(where).append(": ").append(e.getClass().getName()).append(LOG_FIELD_SEPARATOR).append(e.getMessage());

		if (e.getCause() != null) {
			builder.append("\ncause: ")
					.append(e.getCause().getClass().getName())
					.append(LOG_FIELD_SEPARATOR)
					.append(e.getCause().getMessage());
		}

		e(tag, builder.toString());
	}

	/**
	 * Logs a message with DEBUG level and uses caller class name as a tag.
	 *
	 * @param message the message to log
	 */
	public static void d(String message) {
		d(getCallerMethodName().logTag, message);
	}

	/**
	 * Logs a message with INFO level and uses caller class name as a tag.
	 *
	 * @param message the message to log
	 */
	public static void i(String message) {
		i(getCallerMethodName().logTag, message);
	}

	public static void fi(String messageTemplate, Object... parameters) {
		String message = formatMessage(messageTemplate, parameters);
		i(message);
	}

	private static String formatMessage(String messageTemplate, Object[] parameters) {
		try {
			return String.format(messageTemplate, parameters);
		} catch (Exception e) {
			e("Unable to format message, format = \"" + messageTemplate + "\", parameters = " + Arrays.toString(parameters));
			return "";
		}
	}

	/**
	 * Logs a message with WARN level and uses caller class name as a tag.
	 *
	 * @param message the message to log
	 */
	public static void w(String message) {
		w(getCallerMethodName().logTag, message);
	}

	/**
	 * Logs a message with ERROR level and uses caller class name as a tag.
	 *
	 * @param message the message to log
	 */
	public static void e(String message) {
		e(getCallerMethodName().logTag, message);
	}

	/**
	 * Logs exception information.
	 *
	 * @param e the exception to log
	 */
	public static void logException(Throwable e) {
		MethodNameInfo method = getCallerMethodName();

		if (e != null) {
			e(method.logTag, method.methodName, e);
		} else {
			e(method.logTag, method.methodName, new Exception("Unknown exception"));
		}
	}

	/**
	 * Logs exception information including its stack trace.
	 *
	 * @param e the exception to log
	 */
	public static void logExceptionWithStackTrace(Throwable e) {
		String tag = getCallerMethodName().logTag;

		if (e != null) {
			String exceptionStackTrace = Log.getStackTraceString(e);
			e(tag, exceptionStackTrace);
		} else {
			e(tag, "", new Exception("Unknown exception"));
		}
	}

	/**
	 * Prints the name of currently executed method to system log,
	 * using caller class name as log tag.
	 */
	public static void logMethod() {
		MethodNameInfo method = getCallerMethodName();
		i(method.logTag, method.methodName);
	}

	/**
	 * Checks if saving logs to file is enabled.
	 *
	 * @return <code>true</code> if saving logs to file is enabled.
	 */
	public static boolean isLogSavingEnabled() {
		return enabled;
	}

	/**
	 * Sets log saving state.
	 *
	 * @param fileLogEnabled <code>true</code> enables saving logs to file.
	 */
	public static synchronized void setLogSavingEnabled(boolean fileLogEnabled) {
		DebugLog.enabled = fileLogEnabled;
	}

	/**
	 * Checks if logging to logcat is enabled.
	 *
	 * @return <code>true</code> if logging to logcat is enabled.
	 */
	public static boolean isEchoEnabled() {
		return echoEnabled;
	}

	/**
	 * When echo is not enabled, messages are not printed to Android Log.
	 */
	public static void setEchoEnabled(boolean echoEnabled) {
		DebugLog.echoEnabled = echoEnabled;
	}

	/**
	 * Gets minimal level of log entries that get saved into the log file.
	 *
	 * @return one of {@link Level} values.
	 */
	public static Level getLogSavingLevel() {
		return savingLevel;
	}

	/**
	 * Sets minimal level of log entries that get saved into the log file.
	 *
	 * @param level one of {@link Level} values
	 */
	public static void setLogSavingLevel(Level level) {
		if (level != null) {
			DebugLog.savingLevel = level;
		}
	}

	/**
	 * Gets current level of logs entries that can be echoed to logcat.
	 *
	 * @return one of {@link Level} values.
	 */
	public static Level getEchoLevel() {
		return echoLevel;
	}

	/**
	 * Sets minimal level of logs entries that can be echoed to logcat.
	 */
	public static void setEchoLevel(Level echoLevel) {
		if (echoLevel != null) {
			DebugLog.echoLevel = echoLevel;
		}
	}

	/**
	 * Logs object contents as pretty-printed JSON.
	 *
	 * @param object the object to put to log
	 */
	public static void logObject(Object object) {
		String tag = getCallerMethodName().logTag;
		String value;
		if (object == null) {
			value = "<null>";
		} else {
			String className = object.getClass().getSimpleName();
			String objectValueString = JSON_SERIALIZER.toJson(object);
			value = className + ":\n" + objectValueString.replaceAll("(?<=(^|\n))", "*");
		}
		i(tag, value);
	}

	/**
	 * Deletes log file.
	 */
	public static void deleteLogFile() {
		try {
			File externalStorage = Environment.getExternalStorageDirectory();
			if (externalStorage == null) {
				return;
			}

			File logfile = new File(externalStorage, getLogFilename());
			if (logfile.exists()) {
				logfile.delete();
			}
		} catch (Exception e) {
			// ignore it
		}
	}

	public static void logBundle(Bundle data) {
		StringBuilder content = new StringBuilder("Bundle:");
		Set<String> keySet = data.keySet();

		if (keySet.isEmpty()) {
			content.append(" empty");
		} else {
			for (String key : keySet) {
				content.append("\r\n    ")
					.append(key)
					.append(" = ")
					.append(data.get(key));
			}
		}
		i(content.toString());
	}

	private static void logMessage(String tag, String message, Level level) {
		if (echoEnabled && level.ordinal() >= echoLevel.ordinal()) {
			echoMessage(tag, message, level);
		}
		if (enabled && level.ordinal() >= savingLevel.ordinal()) {
			logToFile(tag, message, level);
		}
	}

	private static void echoMessage(String tag, String message, Level level) {
		switch (level) {
			case Debug:
				Log.d(tag, message);
				break;

			case Error:
				Log.e(tag, message);
				break;

			case Warning:
				Log.w(tag, message);
				break;

			case Information:
				Log.i(tag, message);
				break;

			case Verbose:
				Log.v(tag, message);
				break;
		}
	}

	private static synchronized void logToFile(String tag, String message, Level level) {
		Writer writer = getLogWriter();

		if (writer != null) {
			try {
				writer.write(buildLogMessage(tag, message, level));
			} catch (IOException e) {
				// can't do anything about this exception
			} finally {
				closeSafely(writer);
			}
		}
	}

	private static void closeSafely(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException e) {
			// ignoring
		}
	}

	private static String buildLogMessage(String tag, String message, Level level) {
		String editedTag = tag != null ? tag.replace('|', '/') : "";
		return TIME_FORMAT.format(new Date()) + level + LOG_FIELD_SEPARATOR + editedTag + LOG_FIELD_SEPARATOR + message + "\r\n";
	}

	private static Writer getLogWriter() {
		FileWriter writer = null;

		File externalStorage = Environment.getExternalStorageDirectory();

		prepareLogsFolder(externalStorage);

		File logfile = new File(externalStorage, getLogFilename());

		try {
			writer = new FileWriter(logfile, true);
		} catch (IOException e) {
			// ignoring this
		}

		return writer;
	}

	private static void prepareLogsFolder(File externalStorage) {
		File logDir = new File(externalStorage, LOG_DIRECTORY);
		if (!logDir.exists()) {
			logDir.mkdir();
		}
	}

	private static String getLogFilename() {
		return LOG_DIRECTORY + File.separatorChar + packageName + LOG_EXTENSION;
	}

	private static String tokenizeClassName(String className) {
		List<String> parts = new ArrayList<>();
		String result;

		try {
			Matcher matcher = CLASS_NAME_PATTERN.matcher(className);

			while (matcher.find()) {
				String part = className.substring(matcher.start(), matcher.end());
				if (!TextUtils.isEmpty(part.trim())) {
					parts.add(part.toUpperCase());
				}
			}

			result = parts.isEmpty() ? className : TextUtils.join("_", parts);
		} catch (Exception e) {
			result = className;
		}

		return result;
	}

	private static MethodNameInfo getCallerMethodName() {
		String className;
		String methodName;
		final int CALLER_STACK_INDEX = 4;

		try {
			StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
			if (callStack.length > CALLER_STACK_INDEX) {
				className = getShortClassName(callStack[CALLER_STACK_INDEX].getClassName());
				methodName = callStack[CALLER_STACK_INDEX].getMethodName();
			} else {
				className = UNKNOWN_SIGNATURE;
				methodName = UNKNOWN_SIGNATURE;
			}
		} catch (Exception e) {
			className = UNKNOWN_SIGNATURE;
			methodName = UNKNOWN_SIGNATURE;
		}

		return new MethodNameInfo(className, methodName);
	}

	private static String getShortClassName(String fullClassName) {
		int lastPoint = fullClassName.lastIndexOf(".");

		return lastPoint >= 0 ? fullClassName.substring(lastPoint + 1) : fullClassName;
	}

	/**
	 * Internal class representing caller method.
	 */
	@SuppressWarnings("unused")
	private static final class MethodNameInfo {

		private final String className;
		private final String methodName;
		private final String logTag;

		private MethodNameInfo(String className, String methodName) {
			this.className = className;
			this.methodName = methodName;
			this.logTag = tokenizeClassName(className);
		}
	}

}
