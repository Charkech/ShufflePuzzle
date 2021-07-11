package com.shufflepuzzle;

import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Constants {
    public static Typeface FONT_GAME_MAIN;
    public static String[] SETTINGS_NAME = new String[]{"mute","mode"};
    public static String getCurrentDate()
    {
        String dateAndTime="";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dateAndTime = sdf.format(new Date());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return dateAndTime;
    }

    public static Typeface getFontGameMain() {
        return FONT_GAME_MAIN;
    }

    public static void setFontGameMain(Typeface fontGameMain) {
        Constants.FONT_GAME_MAIN = fontGameMain;
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
}
