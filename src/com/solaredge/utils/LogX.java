
package com.solaredge.utils;

import android.util.Log;

import com.solaredge.fusion.ServerInfo;

public class LogX {

    private static boolean needRecord = ServerInfo.LOG_ENABLE_ON_RELEASE ? true
            : (ServerInfo.IS_RELEASE ? false : true);

    public static void d(String tag, String message) {
        if (needRecord) {
            Log.d(tag, message);
        }
    }

    public static void snipet(String tag, String message) {
        if (!needRecord) {
            return;
        }
        int maxLogSize = 1000;
        for (int i = 0; i <= message.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > message.length() ? message.length() : end;
            Log.d(tag, message.substring(start, end));
        }
    }

    public static void e(String tag, String message) {
        Log.e(tag, message);
    }

    public static void trace(String tag, String message) {
        if (needRecord) {
            Log.i(tag, message);
        }
    }

    public LogX(String path) {
    }
}
