package com.home.ui.database;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.home.ui.tables.models.UserModel;

/**
 * Created by Narendra Singh on 29/9/17.
 */

public class AppPrefs {
    public static final String FILENAME = "demo_fb";
    public static final String USER_KEY = "user";
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static void setStringKeyValues(Context context, String key, String val) {
        SharedPreferences.Editor sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE).edit();
        sharedPreferences.putString(key, val);
        sharedPreferences.commit();
    }

    public static UserModel getUserModel(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
        return new Gson().fromJson(sharedPreferences.getString(USER_KEY, ""), UserModel.class);
    }
}
