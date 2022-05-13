package com.abin.pcbahwtest.utils;

import android.text.TextUtils;
import android.util.Log;

//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;

/**
 * Created by a_Bin on 2017/12/4.
 * Email : ybjaychou@gmail.com
 */

public class ALOG {

    private static final boolean DBG = true;

    private static String getCallerClass() {
        StackTraceElement[] traceElements = new Throwable().getStackTrace();
        if (traceElements.length >= 3) {
            StackTraceElement element = traceElements[2];
            String className = element.getClassName();
            String str[] = className.trim().split("\\.");
            return str[str.length - 1];
        } else {
            return "";
        }
    }

    public static void D(/*@NonNull*/ String format, /*@Nullable*/ Object... args) {
        if (!DBG) return;
        String tag = getCallerClass();
        String msg = String.format(format, args);
        if (!TextUtils.isEmpty(tag))
            Log.d(tag, msg);
    }

    public static void I(/*@NonNull*/ String format, /*@Nullable*/ Object... args) {
        String tag = getCallerClass();
        String msg = String.format(format, args);
        if (!TextUtils.isEmpty(tag))
            Log.i(tag, msg);
    }
    public static void E(/*@NonNull*/ String format, /*@Nullable*/ Object... args) {
        String tag = getCallerClass();
        String msg = String.format(format, args);
        if (!TextUtils.isEmpty(tag))
            Log.e(tag, msg);
    }
}
