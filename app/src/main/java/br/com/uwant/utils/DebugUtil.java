package br.com.uwant.utils;

import android.util.Log;

/**
 * Created by felipebenezi on 01/07/14.
 */
public abstract class DebugUtil {

    public static final boolean DEBUG_MODE = false;
    private static final String DEFAULT_TAG = "uWant-Debug";

    public static void debug(String tag, String message) {
        Log.d(tag, message);
    }

    public static void debug(String message) {
        debug(DEFAULT_TAG, message);
    }

    public static void info(String tag, String message) {
        Log.i(tag, message);
    }

    public static void info(String message) {
        info(DEFAULT_TAG, message);
    }

    public static void error(String tag, String message) {
        Log.w(tag, message);
    }

    public static void error(String message) {
        error(DEFAULT_TAG, message);
    }

}
