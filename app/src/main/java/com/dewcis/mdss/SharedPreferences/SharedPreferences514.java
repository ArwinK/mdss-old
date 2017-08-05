package com.dewcis.mdss.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Arwin Kish on 12/30/2016.
 */
public class SharedPreferences514 {

    public static final String PREFS_514 = "514_PREFS";
    public static final String PREFS_IDENTITY = "name";
    public static final String PREFS_VILLAGE = "village";
    public static final String PREFS_GENDER = "gender";
    public static final String PREFS_AGE_TYPE = "age_type";
    public static final String PREFS_AGE = "age";

    public SharedPreferences514() {
        super();
    }

    public void save(Context context, String name, int village, String gender, String age_type, int age) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_514, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.putString(PREFS_IDENTITY, name);
        editor.putInt(PREFS_VILLAGE, village);
        editor.putString(PREFS_GENDER, gender);
        editor.putString(PREFS_AGE_TYPE, age_type);
        editor.putInt(PREFS_AGE, age);

        editor.commit();
    }

    public int getValue(Context context, String category) {
        SharedPreferences settings;
        int value;

        settings = context.getSharedPreferences(PREFS_514, Context.MODE_PRIVATE);
        value = settings.getInt(category, 0);
        return value;
    }

    public String getValueStrings(Context context, String category) {
        SharedPreferences settings;
        String value;

        settings = context.getSharedPreferences(PREFS_514, Context.MODE_PRIVATE);
        value = settings.getString(category, null);
        return value;
    }

    public void clearSharedPreference(Context context) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_514, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.commit();
    }

    public void removeValue(Context context, String category) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_514, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.remove(category);
        editor.commit();
    }
}



