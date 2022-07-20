package com.abdul.jigsawpuzzle.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class DatabaseUtils {

    //saved data to shared preferences
    public static void putStringInPreferences(Context context, String value, String key, String pKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(pKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    //load data from shared preferences
    public static String getStringFromPreferences(Context context,String defaultValue, String key, String pKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(pKey, Context.MODE_PRIVATE);
        String temp = sharedPreferences.getString(key, defaultValue);
        return temp;
    }
}
