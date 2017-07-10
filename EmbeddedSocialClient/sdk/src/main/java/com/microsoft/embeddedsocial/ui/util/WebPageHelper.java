/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.microsoft.embeddedsocial.sdk.R;

public class WebPageHelper {
    public static void openPrivacyPolicy(Context context) {
        openWebPage(context, context.getString(R.string.es_privacy_policy_url));
    }

    public static void openTermsAndConditions(Context context) {
        openWebPage(context, context.getString(R.string.es_terms_url));
    }

    public static void openWebPage(Context context, String url) {
        Uri pageUri = Uri.parse(url);
        Intent openPage = new Intent(Intent.ACTION_VIEW, pageUri);
        context.startActivity(openPage);
    }
}
