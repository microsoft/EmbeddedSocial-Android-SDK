/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Is used to send intent to a processing service.
 * @param <T>	processing service action enum class
 */
public class ServiceLauncher<T extends Enum<T>> {

	private static final char PACKAGE_NAME_SEPARATOR = '.';
	
	private final Class<? extends Service> serviceClass;
	private final Context context;

	/**
	 * Creates an instance.
	 * @param context
	 * @param serviceClass
	 */
	public ServiceLauncher(Context context, Class<? extends Service> serviceClass) {
		this.serviceClass = serviceClass;
		this.context = context;
	}
	
	/**
	 * Starts processing service with the specified action.
	 * @param	action	service action
	 */
	public void launchService(T action) {
		launchService(action, Bundle.EMPTY);
	}
	
	/**
	 * Starts processing service with the specified action and intent params.
	 * @param action	service action
	 * @param params	parameters to put into the intent
	 */
	public void launchService(T action, Bundle params) {
		Intent intent = getLaunchIntent(action, params);
		context.startService(intent);
	}

	/**
	 * Creates an intent to launch the service with the specified action.
	 * @param	action	the action to process
	 * @return	{@link android.content.Intent} instance.
	 */
	public Intent getLaunchIntent(T action) {
		return getLaunchIntent(action, Bundle.EMPTY);
	}

	/**
	 * Creates an intent to launch the service with the specified action.
	 * @param	action	the action to process
	 * @param	params	intent parameters
	 * @return	{@link android.content.Intent} instance.
	 */
	public Intent getLaunchIntent(T action, Bundle params) {
		return new Intent(getActionWithPackage(action))
			.putExtras(params != null ? params : Bundle.EMPTY)
			.setClass(context, serviceClass);
	}
	
	private String getActionWithPackage(T action) {
		return context.getPackageName() + PACKAGE_NAME_SEPARATOR + action.name();
	}
}
