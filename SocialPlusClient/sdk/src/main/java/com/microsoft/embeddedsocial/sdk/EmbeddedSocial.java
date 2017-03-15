/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.sdk;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorRes;
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
import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.autorest.models.PublisherType;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.image.ImageLoader;
import com.microsoft.embeddedsocial.sdk.ui.AppProfile;
import com.microsoft.embeddedsocial.sdk.ui.ToolbarColorizer;
import com.microsoft.embeddedsocial.server.NetworkAvailability;
import com.microsoft.embeddedsocial.service.WorkerService;
import com.microsoft.embeddedsocial.ui.activity.AddPostActivity;
import com.microsoft.embeddedsocial.ui.activity.HomeActivity;
import com.microsoft.embeddedsocial.ui.activity.MyProfileActivity;
import com.microsoft.embeddedsocial.ui.activity.PinsActivity;
import com.microsoft.embeddedsocial.ui.activity.PopularActivity;
import com.microsoft.embeddedsocial.ui.activity.RecentActivityActivity;
import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.UserHeaderViewHolder;
import com.microsoft.embeddedsocial.ui.fragment.PinsFragment;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragment;
import com.microsoft.embeddedsocial.ui.notification.NotificationController;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.data.Preferences;
import com.microsoft.embeddedsocial.data.storage.DatabaseHelper;
import com.microsoft.embeddedsocial.sdk.ui.DrawerDisplayMode;
import com.microsoft.embeddedsocial.server.RequestInfoProvider;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.service.ServiceAction;
import com.microsoft.embeddedsocial.ui.activity.OptionsActivity;
import com.microsoft.embeddedsocial.ui.activity.SearchActivity;
import com.microsoft.embeddedsocial.ui.activity.SignInActivity;
import com.microsoft.embeddedsocial.ui.activity.TopicActivity;
import com.microsoft.embeddedsocial.ui.fragment.AddPostFragment;
import com.microsoft.embeddedsocial.ui.fragment.CommentFeedFragment;
import com.microsoft.embeddedsocial.ui.fragment.ReplyFeedFragment;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

/**
 * Embedded Social SDK facade.
 */
public final class EmbeddedSocial {

	/**
	 * Private constructor to forbid instantiation.
	 */
	private EmbeddedSocial() {  }

    /**
     * Initializes Embedded Social SDK.
     * @param application   the application instance
     * @param configResId   resource id of the JSON configuration file for the SDK
     */
    public static void init(Application application, @RawRes int configResId) {
        init(application, configResId, null);
    }

    /**
     * Initializes Embedded Social SDK.
     * @param application   the application instance
     * @param configResId   resource id of the JSON configuration file for the SDK
     * @param appKey        application key
     */
    public static void init(Application application, @RawRes int configResId, String appKey) {
        if (BuildConfig.DEBUG) {
            initLogging(application);
        }
        InputStream is = application.getResources().openRawResource(configResId);
        Reader reader = new InputStreamReader(is);
        Options options = new Gson().fromJson(reader, Options.class);
        if (appKey != null) {
            options.setAppKey(appKey);
        }
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
		EmbeddedSocialServiceProvider serviceProvider = new EmbeddedSocialServiceProvider(context);
		GlobalObjectRegistry.addObject(EmbeddedSocialServiceProvider.class, serviceProvider);
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

    /**
     * Sets the background color and text color of the toolbar
     */
    public static void setToolbarColors(ToolbarColorizer colorizer) {
        BaseActivity.setToolbarColorizer(colorizer);
    }

    public static void setAppProfile(AppProfile appProfile) {
        UserHeaderViewHolder.setAppProfile(appProfile);
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

    public static Fragment getCommentFeedFragmentByName(String topicName,
            PublisherType publisherType, HashMap<Integer, Integer> errorMessages) {
        return CommentFeedFragment.getCommentFeedFragmentFromTopicName(topicName, publisherType, errorMessages);
    }

    public static Fragment getCommentFeedFragment(String topicHandle, HashMap<Integer, Integer> errorMessages) {
        return CommentFeedFragment.getCommentFeedFragmentFromTopicHandle(topicHandle, errorMessages);
    }

    public static Fragment getCommentFeedFragment(String topicHandle) {
        return getCommentFeedFragment(topicHandle, null);
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

    public static void launchSignInActivity(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
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

    /**
     * @return true if the user is signed in
     */
    public static boolean isSignedIn() {
        return UserAccount.getInstance().isSignedIn();
    }

    public static void setReportHandler(IReportHandler reportHandler) {
        GlobalObjectRegistry.addObject(IReportHandler.class, reportHandler);
    }

    public static void setNavigationDrawerHandler(INavigationDrawerHandler navigationDrawerHandler) {
        GlobalObjectRegistry.addObject(INavigationDrawerHandler.class, navigationDrawerHandler);
    }
}
