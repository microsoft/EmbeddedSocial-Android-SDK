/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.sdk;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.facebook.FacebookSdk;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.microsoft.socialplus.autorest.models.ContentType;
import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.autorest.models.Reason;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.data.Preferences;
import com.microsoft.socialplus.data.storage.DatabaseHelper;
import com.microsoft.socialplus.image.ImageLoader;
import com.microsoft.socialplus.sdk.ui.DrawerDisplayMode;
import com.microsoft.socialplus.server.NetworkAvailability;
import com.microsoft.socialplus.server.RequestInfoProvider;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.service.ServiceAction;
import com.microsoft.socialplus.service.WorkerService;
import com.microsoft.socialplus.ui.activity.AddPostActivity;
import com.microsoft.socialplus.ui.activity.HomeActivity;
import com.microsoft.socialplus.ui.activity.MyProfileActivity;
import com.microsoft.socialplus.ui.activity.OptionsActivity;
import com.microsoft.socialplus.ui.activity.PinsActivity;
import com.microsoft.socialplus.ui.activity.PopularActivity;
import com.microsoft.socialplus.ui.activity.RecentActivityActivity;
import com.microsoft.socialplus.ui.activity.SearchActivity;
import com.microsoft.socialplus.ui.activity.TopicActivity;
import com.microsoft.socialplus.ui.activity.base.BaseActivity;
import com.microsoft.socialplus.ui.fragment.AddPostFragment;
import com.microsoft.socialplus.ui.fragment.CommentFeedFragment;
import com.microsoft.socialplus.ui.fragment.PinsFragment;
import com.microsoft.socialplus.ui.fragment.ReplyFeedFragment;
import com.microsoft.socialplus.ui.notification.NotificationController;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Social Plus SDK facade.
 */
public final class SocialPlus {

	/**
	 * Private constructor to forbid instantiation.
	 */
	private SocialPlus() {  }

	/**
	 * Initializes Social Plus SDK.
	 * @param application   the application instance
	 * @param configResId   resource id of the JSON configuration file for the SDK
	 */
	public static void init(Application application, @RawRes int configResId) {
		if (BuildConfig.DEBUG) {
			initLogging(application);
		}
		InputStream is = application.getResources().openRawResource(configResId);
		Reader reader = new InputStreamReader(is);
		Options options = new Gson().fromJson(reader, Options.class);
		options.verify();
		GlobalObjectRegistry.addObject(options);
		initGlobalObjects(application, options);
		WorkerService.getLauncher(application).launchService(ServiceAction.BACKGROUND_INIT);
		// TODO: Added to main activity access token tracking
		// https://developers.facebook.com/docs/facebook-login/android/v2.2#access_profile
	}

	/**
	 * Initializes navigation drawer.
	 * @param application               application instance
	 * @param fragmentFactory           the factory that produces navigation fragments
	 * @param drawerDisplayMode         display mode of the drawer
	 * @param hostingAppMenuTitleId     resource id of the title of hosting app navigation menu
	 */
	public static void initDrawer(Application application, INavigationDrawerFactory fragmentFactory,
	                              DrawerDisplayMode drawerDisplayMode, @StringRes int hostingAppMenuTitleId) {

		initDrawer(application, fragmentFactory, drawerDisplayMode, application.getString(hostingAppMenuTitleId));
	}

	/**
	 * Initializes navigation drawer.
	 * @param application               application instance
	 * @param fragmentFactory           the factory that produces navigation fragments
	 * @param drawerDisplayMode         display mode of the drawer
	 * @param hostingAppMenuTitle       the title of hosting app navigation menu
	 */
	public static void initDrawer(Application application, INavigationDrawerFactory fragmentFactory,
	                              DrawerDisplayMode drawerDisplayMode,
	                              @Nullable CharSequence hostingAppMenuTitle) {

		GlobalObjectRegistry.addObject(
                new NavigationMenuDescription(fragmentFactory, drawerDisplayMode, hostingAppMenuTitle));
	}

	private static void initGlobalObjects(Context context, Options options) {
		GlobalObjectRegistry.addObject(OpenHelperManager.getHelper(context, DatabaseHelper.class));
		Gson gson = new GsonBuilder()
			.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
			.create();
		GlobalObjectRegistry.addObject(gson);
		ImageLoader.init(context);
		SocialPlusServiceProvider serviceProvider = new SocialPlusServiceProvider(context);
		GlobalObjectRegistry.addObject(SocialPlusServiceProvider.class, serviceProvider);
		GlobalObjectRegistry.addObject(new Preferences(context));
		GlobalObjectRegistry.addObject(new RequestInfoProvider(context));
		GlobalObjectRegistry.addObject(new UserAccount(context));
		GlobalObjectRegistry.addObject(new NotificationController(context));
		NetworkAvailability networkAccessibility = new NetworkAvailability();
		networkAccessibility.startMonitoring(context);
		GlobalObjectRegistry.addObject(networkAccessibility);
		FacebookSdk.sdkInitialize(context);
		FacebookSdk.setApplicationId(options.getFacebookApplicationId());
	}

    public static void initColors(int color) {
        BaseActivity.setColor(color);
    }

	private static void initLogging(Context context) {
		if (BuildConfig.DEBUG) {
			DebugLog.setEchoLevel(DebugLog.Level.Debug);
			DebugLog.setEchoEnabled(true);
			DebugLog.setLogSavingLevel(DebugLog.Level.Debug);
			DebugLog.setLogSavingEnabled(true);
			DebugLog.prepare(context);
		}
	}

	/**
	 * Starts an activity to add a new post.
	 * @param context           valid context
	 * @param title             new post title (optional, pass null or an empty string
	 *                             if not needed)
	 * @param description       new post description (optional, pass null or an empty string
	 *                             if not needed)
	 * @param imageUri          image URI (optional, pass null or {@linkplain Uri#EMPTY}
	 *                             if not needed)
     * @return the handle of the new topic
	 */
	public static void launchAddPostActivity(Context context, String title, String description,
                                              Uri imageUri, boolean automatic) {
        Intent intent = new Intent(context, AddPostActivity.class);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(IntentExtras.POST_TITLE, title);
        }
        if (!TextUtils.isEmpty(description)) {
            intent.putExtra(IntentExtras.POST_DESCRIPTION, description);
        }
        if (imageUri != null && imageUri != Uri.EMPTY) {
            intent.putExtra(IntentExtras.POST_IMAGE_URI, imageUri.toString());
        }

        if (automatic) {
            intent.putExtra(IntentExtras.AUTOMATIC, true);
        }

        context.startActivity(intent);
    }

    /**
     * Starts an activity to add a new post.
     * @param context   valid context
     */
    public static void launchAddPostActivity(Context context) {
        launchAddPostActivity(context, "", "", Uri.EMPTY, false);
    }

    public static void clearCache(Context context) {
        DatabaseHelper helper = new DatabaseHelper(context);
        helper.clearData();
    }

    public static Fragment getAddPostFragment() {
        AddPostFragment fragment = AddPostFragment.newInstance();
        return fragment;
    }

    public static Fragment getReplyFeedFragment(String commentHandle) {
        Fragment feedFragment = new ReplyFeedFragment();
        Bundle b = new Bundle();
        b.putCharSequence(IntentExtras.COMMENT_HANDLE, commentHandle);
        feedFragment.setArguments(b);
        return feedFragment;
    }

    public static Fragment getCommentFeedFragment(String topicHandle) {
        Fragment feedFragment = new CommentFeedFragment();
        Bundle b = new Bundle();
        b.putCharSequence(IntentExtras.TOPIC_HANDLE, topicHandle);
        feedFragment.setArguments(b);
        return feedFragment;
    }

    public static Fragment getPinsFragment() {
        return new PinsFragment();
    }

    public static void launchPinsActivity(Context context) {
        Intent intent = new Intent(context, PinsActivity.class);
        context.startActivity(intent);
    }

    public static void launchPopularActivity(Context context) {
        Intent intent = new Intent(context, PopularActivity.class);
        context.startActivity(intent);
    }

    public static void launchActivityFeedActivity(Context context) {
        Intent intent = new Intent(context, RecentActivityActivity.class);
        context.startActivity(intent);
    }

    public static void launchProfileActivity(Context context) {
        Intent intent = new Intent(context, MyProfileActivity.class);
        context.startActivity(intent);
    }

    public static void launchYourFeedActivity(Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    public static void launchSearchActivity(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    public static void launchOptionsActivity(Context context) {
        Intent intent = new Intent(context, OptionsActivity.class);
        context.startActivity(intent);
    }

    /**
     * Starts an activity to view a comment feed for a given topic
     * @param context	valid context
     */
    public static void launchCommentFeedActivity(Context context, String topicHandle) {
        Intent intent = new Intent(context, TopicActivity.class);
        intent.putExtra(IntentExtras.TOPIC_HANDLE, topicHandle);
        context.startActivity(intent);
    }

    public static void setReportHandler(IReportHandler reportHandler) {
        GlobalObjectRegistry.addObject(IReportHandler.class, reportHandler);
    }

    public static void setNavigationDrawerHandler(INavigationDrawerHandler navigationDrawerHandler) {
        GlobalObjectRegistry.addObject(INavigationDrawerHandler.class, navigationDrawerHandler);
    }
}
