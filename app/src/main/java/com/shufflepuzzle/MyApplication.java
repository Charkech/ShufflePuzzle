package com.shufflepuzzle;

import android.app.Application;
import android.graphics.Typeface;

import com.google.android.gms.ads.MobileAds;

import static com.shufflepuzzle.Constants.FONT_GAME_MAIN;

public class MyApplication extends Application {
    private static MyApplication sPhotoApp;


    @Override
    public void onCreate() {
        super.onCreate();
        sPhotoApp = this;
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));
        FONT_GAME_MAIN = Typeface.createFromAsset(getAssets(), "CooperBlack.otf");

    }


}

