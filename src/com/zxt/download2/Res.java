
package com.zxt.download2;

import java.lang.reflect.Field;

import android.content.Context;
import android.util.Log;

public class Res {
    private static final String TAG = "Res";

    private static Res mRes;

    private Context mContext;

    private static Class<?> id= null;

    private static Class<?> drawable = null;

    private static Class<?> layout = null;

    private static Class<?> anim = null;

    private static Class<?> style = null;

    private static Class<?> string = null;

    private static Class<?> array = null;

    private Res(Context context) {
        this.mContext = context;
        try {
            Log.d(TAG, this.mContext.getPackageName() + ".R$drawable");
            drawable = Class.forName(this.mContext.getPackageName() + ".R$drawable");
        } catch (ClassNotFoundException ex) {
            Log.d(TAG, ex.getMessage());
        }
        try {
            layout = Class.forName(this.mContext.getPackageName() + ".R$layout");
        } catch (ClassNotFoundException e) {
            Log.d(TAG, e.getMessage());
        }
        try {
            id = Class.forName(this.mContext.getPackageName() + ".R$id");
        } catch (ClassNotFoundException e) {
            Log.d(TAG, e.getMessage());
        }
        try {
            anim = Class.forName(this.mContext.getPackageName() + ".R$anim");
        } catch (ClassNotFoundException e) {
            Log.d(TAG, e.getMessage());
        }
        try {
            style = Class.forName(this.mContext.getPackageName() + ".R$style");
        } catch (ClassNotFoundException e) {
            Log.d(TAG, e.getMessage());
        }
        try {
            string = Class.forName(this.mContext.getPackageName() + ".R$string");
        } catch (ClassNotFoundException e) {
            Log.d(TAG, e.getMessage());
        }
        try {
           array = Class.forName(this.mContext.getPackageName() + ".R$array");
        } catch (ClassNotFoundException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public synchronized static Res getInstance(Context context) {
        if (mRes == null)
            mRes = new Res(context);
        return mRes;
    }

    public int getAnim(String paramString) {
        return getR(anim, paramString);
    }

    public int getId(String paramString) {
        return getR(id, paramString);
    }

    public int getDrawable(String paramString) {
        return getR(drawable, paramString);
    }

    public int getLayout(String paramString) {
        return getR(layout, paramString);
    }

    public int getStyle(String paramString) {
        return getR(style, paramString);
    }

    public int getString(String paramString) {
        return getR(string, paramString);
    }

    public int getArray(String paramString) {
        return getR(array, paramString);
    }

    private int getR(Class<?> paramClass, String paramString) {
        if (paramClass == null) {
            Log.d(TAG, "getRes(null," + paramString + ")");
            throw new IllegalArgumentException("ResClass is not initialized.");
        }
        try {
            Field localField = paramClass.getField(paramString);
            int k = localField.getInt(paramString);
            return k;
        } catch (Exception localException) {
            Log.d(TAG, "getRes(" + paramClass.getName() + ", " + paramString + ")");
            Log.d(TAG,
                    "Error getting resource. Make sure you have copied all resources (res/) from SDK to your project. ");
            Log.d(TAG, localException.getMessage());
        }
        return -1;
    }
}
