package com.example.lightmap.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static com.example.lightmap.activity.MainActivity.mInterstitialAd;

public class Ads {
    public static void showAds(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long currentTimeMillis = System.currentTimeMillis();
        long lastTime = sharedPreferences.getLong("lastTime", 0);
        if (currentTimeMillis - lastTime > 60000) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("lastTime", currentTimeMillis);
            editor.apply();
        }
    }
}
