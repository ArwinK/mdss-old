package com.dewcis.mdss.SharedPreferences;

/**
 * Created by Arwin Kish on 12/29/2016.
 */


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreference {

    public static final String PREFS_NAME = "HH_PREFS";
    public static final String PREFS_POSTPARTUM = "postpartum";
    public static final String PREFS_PREGNANT = "pregnant";
    public static final String PREFS_CHILDREN = "children";
    public static final String PREFS_INFANT = "infant";
    public static final String PREFS_OTHER_MOTHER = "other_mothers";
    public static final String PREFS_OTHERS = "other_members";
    public static final String PREFS_SURVEY = "survey_id";

    public SharedPreference() {
        super();
    }

    public void save(Context context, int postpartum, int pregnant, int children, int infant, int other_mothers, int other_members, int survey) {
        SharedPreferences settings;
        Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putInt(PREFS_POSTPARTUM, postpartum);
        editor.putInt(PREFS_PREGNANT, pregnant);
        editor.putInt(PREFS_CHILDREN, children);
        editor.putInt(PREFS_INFANT, infant);
        editor.putInt(PREFS_OTHER_MOTHER, other_mothers);
        editor.putInt(PREFS_OTHERS, other_members);
        editor.putInt(PREFS_SURVEY, survey);

        editor.apply();
    }

    public int getValue(Context context, String category) {
        SharedPreferences settings;
        int value;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        value = settings.getInt(category, 0);
        return value;
    }

    public void clearSharedPreference(Context context) {
        SharedPreferences settings;
        Editor editor;

        //settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.apply();
    }

    public static void removeValue(Context context, String category) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.remove(category);
        editor.apply();
    }
}
