/*
 * Copyright (C) 2012 zxt
 * ManifestMetaData.java
 * Description:
 * Author: zxt
 * Date:  2012-11-19 下午2:39:31
 */

package com.zxt.download2;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public class ManifestMetaData {
    private static Object readKey(Context context, String keyName) {
        try {
            ApplicationInfo appi = context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = appi.metaData;
            Object value = bundle.get(keyName);
            return value;
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    public static int getInt(Context context, String keyName) {
        return (Integer) readKey(context, keyName);
    }

    public static String getString(Context context, String keyName) {
        return (String) readKey(context, keyName);
    }

    public static Boolean getBoolean(Context context, String keyName) {
        return (Boolean) readKey(context, keyName);
    }

    public static Object get(Context context, String keyName) {
        return readKey(context, keyName);
    }
}
