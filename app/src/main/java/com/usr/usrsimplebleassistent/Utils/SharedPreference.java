package com.usr.usrsimplebleassistent.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2016/9/12.
 */
public class SharedPreference {
    // 文件名
    private static final String FILE_NAME = "Ying";
    private static SharedPreferences sp;

    // 保存int值
    public static void saveInt(Context context, String key, int values) {
        if (sp == null) {
            sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putInt(key, values).commit();
    }

    // 获取int值
    public static int getInt(Context context, String key, int defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getInt(key, defValue);
    }

    // 保存float值
    public static void saveFloat(Context context, String key, float values) {
        if (sp == null) {
            sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putFloat(key, values).commit();
    }

    // 获取float值
    public static float getFloat(Context context, String key,
                                 float defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getFloat(key, defValue);
    }

    // 保存boolean值
    public static void saveBoolean(Context context, String key, boolean values) {
        if (sp == null) {
            sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key, values).commit();
    }

    // 获取boolean值
    public static boolean getBoolean(Context context, String key,
                                     boolean defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key, defValue);
    }

    // 保存String值
    public static void saveString(Context context, String key, String values) {
        if (sp == null) {
            sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        sp.edit().putString(key, values).commit();
    }

    // 获取String值
    public static String getString(Context context, String key, String defValue) {
        if (sp == null) {
            sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        }
        return sp.getString(key, defValue);
    }
}
