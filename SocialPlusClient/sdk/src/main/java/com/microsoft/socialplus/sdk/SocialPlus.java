/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.sdk;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.microsoft.socialplus.autorest.SocialPlusClient;
import com.microsoft.socialplus.autorest.SocialPlusClientImpl;
import com.microsoft.socialplus.autorest.TopicsOperations;
import com.microsoft.socialplus.autorest.TopicsOperationsImpl;
import com.microsoft.socialplus.autorest.models.ContentType;
import com.microsoft.rest.ServiceResponse;
import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.data.Preferences;
import com.microsoft.socialplus.data.model.CommentFeedType;
import com.microsoft.socialplus.data.storage.DatabaseHelper;
import com.microsoft.socialplus.data.storage.DbSchemas;
import com.microsoft.socialplus.fetcher.CommentFeedFetcher;
import com.microsoft.socialplus.fetcher.FetchersFactory;
import com.microsoft.socialplus.fetcher.base.Fetcher;
import com.microsoft.socialplus.fetcher.base.RequestType;
import com.microsoft.socialplus.image.ImageLoader;
import com.microsoft.socialplus.sdk.ui.DrawerDisplayMode;
import com.microsoft.socialplus.server.NetworkAvailability;
import com.microsoft.socialplus.server.RequestInfoProvider;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.model.view.TopicView;
import com.microsoft.socialplus.server.model.view.UserCompactView;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.service.ServiceAction;
import com.microsoft.socialplus.service.WorkerService;
import com.microsoft.socialplus.ui.activity.AddPostActivity;
import com.microsoft.socialplus.ui.activity.CommentActivity;
import com.microsoft.socialplus.ui.activity.HomeActivity;
import com.microsoft.socialplus.ui.activity.MyProfileActivity;
import com.microsoft.socialplus.ui.activity.OptionsActivity;
import com.microsoft.socialplus.ui.activity.PinsActivity;
import com.microsoft.socialplus.ui.activity.PopularActivity;
import com.microsoft.socialplus.ui.activity.RecentActivityActivity;
import com.microsoft.socialplus.ui.activity.SearchActivity;
import com.microsoft.socialplus.ui.activity.TopicActivity;
import com.microsoft.socialplus.ui.fragment.AddPostFragment;
import com.microsoft.socialplus.ui.fragment.CommentFeedFragment;
import com.microsoft.socialplus.ui.fragment.PinsFragment;
import com.microsoft.socialplus.ui.fragment.ReplyFeedFragment;
import com.microsoft.socialplus.ui.notification.NotificationController;

import junit.framework.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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


    public static void getTopicFromFetcher(String topicHandle) {
        new Thread(()->
        {
            CommentFeedFetcher commentFeedFetcher = new CommentFeedFetcher(CommentFeedType.RECENT, topicHandle, null);
            TopicView topicView = null;
            try {
                topicView = commentFeedFetcher.readTopic(RequestType.REGULAR);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (topicView != null) {
                printTopicInfo(topicView);
            }
        }
        ).start();
    }

    public static void getLikeFeed(String topicHandle) {
        new Thread(()->
        {
            Fetcher<UserCompactView> fetcher = FetchersFactory.createLikeFeedFetcher(topicHandle, ContentType.TOPIC);
            try {
                System.out.println("output: " + fetcher.getAllData());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ).start();
    }

    public static void printTopicInfo(TopicView topicView) {
        System.out.println("Title:  " + topicView.getTopicTitle());
        System.out.println("Text:   " + topicView.getTopicText());
        System.out.println("Author: " + topicView.getUser().getFullName());
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

    // TODO remove fake cache (also AddPostCallback class)
    protected static final String TABLE_NAME = "TitleToHandle";
    protected static final String CACHE_FILE = "alex.sqlite";

    /**
     * Determines if external storage is open for writes
     * @return true is external storage can be written to
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Retrieves the file containing the Alex cache
     * @return File with contents of Alex cache
     */
    public static File getDatabaseFile() {
        if (!isExternalStorageWritable()) {
            System.out.println("Cannot access external memory");
            return null;
        }

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        // ensure the Documents dir exists
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // access the database file
        File databaseFile = new File(dir, CACHE_FILE);

        if (!databaseFile.exists()) {
            // this file does not exist yet, let's create it with public permissions
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Could not create database file");
                System.out.println(e.getMessage());
                return null;
            }

            databaseFile.setReadable(true, false); // readable, ownerOnly
            databaseFile.setWritable(true, false);
        }

        return databaseFile;
    }

    public static void getOrCreateTopic(Context context, String topicTitle) {
        getOrCreateTopic(context, topicTitle, null, null);
    }

        /**
         * Gets a topic and associated comment feed based on topic title.
         * If topic does not yet exist, creates one transparently to the user.
         * @param context Context fo the calling application
         * @param topicTitle Title of the topic.
         * @param topicDescription description for the topic
         * @param imageURI URI for a picture associated with this topic
         */
    public static void getOrCreateTopic(Context context, String topicTitle, String topicDescription, Uri imageURI) {
        File file = getDatabaseFile();
        if (file == null) {
            System.out.println("Alex cache not configured properly");
            return;
        }
        SQLiteDatabase alexCache = SQLiteDatabase.openOrCreateDatabase(file, null);
        alexCache.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(Title VARCHAR, Handle VARCHAR);");

        String topicHandle = getTopicHandleFromAlexCache(alexCache, topicTitle);

        // check the real cache as well
        if (topicHandle == null) {
            try {
                topicHandle = getTopicHandle(topicTitle);
            } catch (SQLException e) {
                System.out.println("uh oh");
            }

            if (topicHandle != null) {
                // the topic was in the real cache but not the alex-cache
                insertTopicHandleToAlexCache(alexCache, topicTitle, topicHandle);
            }
        }

        if (topicHandle == null) {
            // neither cache found this topic so create it
            AddPostCallback.PENDING_TITLE = topicTitle;
            AddPostCallback.CONTEXT = context;
            launchAddPostActivity(context, topicTitle, topicDescription, imageURI, true);
        } else {
            launchCommentFeedActivity(context, topicHandle);
        }
        alexCache.close();
    }

    /**
     * Retreives a topic handle from the Alex cache
     * @param db to search
     * @param topicTitle title of topic being searched for
     * @return handle to topic
     */
    private static String getTopicHandleFromAlexCache(SQLiteDatabase db, String topicTitle) {
        String selectQuery = "SELECT Handle FROM " + TABLE_NAME + " WHERE Title=?";
        Cursor c = db.rawQuery(selectQuery, new String[]{topicTitle});
        if (!c.moveToFirst()) {
            return null;
        }

        Assert.assertEquals("Found more than one entry with the same title", 1, c.getCount());

        String handle = c.getString(0);
        c.close();
        return handle;
    }

    /**
     * Inserts a row into a custom SQLite db
     * @param alexCache the cache to use
     * @param topicTitle
     * @param topicHandle
     */
    protected static void insertTopicHandleToAlexCache(SQLiteDatabase alexCache,
                                                     String topicTitle, String topicHandle) {
        alexCache.execSQL("INSERT INTO " + SocialPlus.TABLE_NAME + " VALUES('"
                + topicTitle + "','" + topicHandle + "');");
    }

    /**
     * Retrieves the topic handle from the legitimate SDK local cache
     * @param topicTitle the title of the topic to find
     * @return handle of the topic
     * @throws SQLException
     */
    protected static String getTopicHandle(String topicTitle) throws SQLException {
        DatabaseHelper helper = GlobalObjectRegistry.getObject(DatabaseHelper.class);
        Dao<TopicView, String> topicViews = helper.getTopicDao();
        QueryBuilder<TopicView, String> builder = topicViews.queryBuilder();
        builder.where().eq(DbSchemas.Topics.TOPIC_TITLE, topicTitle);
        List<TopicView> topics = builder.query();

        if (topics.isEmpty()) {
            return null;
        }

//        Assert.assertEquals("More than one topic with the same title", topics.size(), 1);
        if (topics.size() > 1) {
            System.out.println("multiple topics with same title:::");
            for (TopicView topic : topics) {
                System.out.println(topic.getTopicTitle());
            }
        }

        // Assume there is only one entry
        return topics.get(0).getHandle();
    }

    public static void getReplyFeed(Context context, String commentHandle) {
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra(IntentExtras.COMMENT_HANDLE, commentHandle);
        context.startActivity(intent);
    }
}
