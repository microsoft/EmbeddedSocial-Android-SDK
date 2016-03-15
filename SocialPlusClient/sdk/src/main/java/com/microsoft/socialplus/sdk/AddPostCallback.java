/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.sdk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.microsoft.socialplus.fetcher.base.Callback;

import java.io.File;
import java.sql.SQLException;

public class AddPostCallback extends Callback {

    public static String PENDING_TITLE = null;
    public static Context CONTEXT = null;

    @Override
    public void onDataUpdated() {
        if (PENDING_TITLE == null) {
            return;
        }

        try {
            String topicHandle = SocialPlus.getTopicHandle(PENDING_TITLE);
            if (topicHandle == null) {
                // TODO
                System.out.println("The topic handle is still null!");
                return;
            }
            SocialPlus.launchCommentFeedActivity(CONTEXT, topicHandle);

            // add it to the alex-cache
            File file = SocialPlus.getDatabaseFile();
            if (file == null) {
                System.out.println("Alex cache not configured properly");
                return;
            }
            SQLiteDatabase alexCache = SQLiteDatabase.openOrCreateDatabase(file, null);
            SocialPlus.insertTopicHandleToAlexCache(alexCache, PENDING_TITLE, topicHandle);
            alexCache.close();
        } catch (SQLException e) {
            // TODO
            System.out.println("uh oh -- things went wrong in the cache");
        } finally {
            PENDING_TITLE = null;
            CONTEXT = null;
        }
    }
}