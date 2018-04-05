package com.microsoft.demogenerator;

import com.microsoft.embeddedsocial.sdk.EmbeddedSocial;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class DemoGeneratorApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EmbeddedSocial.init(this, R.raw.embedded_social_config);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
