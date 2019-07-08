package com.feed.plugin.widget.cookicrop;

import android.util.Log;


public class Logger {

    private static boolean sEnabled = false;

    public static void log(String message) {
//        if (BuildConfig.DEBUG || sEnabled) {
//            Log.d("cookie-cutter", message);
//        }
    }

    public static void setEnabled(boolean sEnabled) {
        Logger.sEnabled = sEnabled;
    }
}
