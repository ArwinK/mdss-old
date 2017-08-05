package com.dewcis.mdss.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.format.Time;
import android.widget.Toast;

import com.dewcis.mdss.Home;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Arwin Kish on 9/5/2016.
 */

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DraftActivity {

    Object value;
    String key;

    public DraftActivity(){

    }

    public JSONObject showDraft(final Activity activity, String trace) {

        final JSONObject jsonSend = new JSONObject();

        try {
            JSONObject jsonObj = new JSONObject(trace);
            Iterator<String> iter = jsonObj.keys();
            while (iter.hasNext()) {
                key = iter.next();
                try {
                    value = jsonObj.get(key);
                } catch (JSONException e) {

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jsonObject = new JSONObject(value.toString());
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key2 = iter.next();
                try {
                    Object value2 = jsonObject.get(key2);
                    jsonSend.put(key2, value2);

                } catch (JSONException e) {

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonSend;

    }
    public static void makeDraft(Activity activity, JSONObject details, String key) {

        try {
            String jsonList = activity.getPreferences(Context.MODE_PRIVATE).getString(key, "EMPTY");
            JSONObject varFocus = new JSONObject();

            if(jsonList.equals("EMPTY")){
                List<String> items = getShared(activity);
                items.add(key);
                save(activity, items);

                varFocus.put(key, details.toString());

                SharedPreferences.Editor sPEditor = activity.getPreferences(Context.MODE_PRIVATE).edit();
                sPEditor.putString(key, varFocus.toString());
                sPEditor.apply();

                Toast.makeText(activity, "m-PAMANECH has stored" , Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(activity, "Draft updated" , Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(activity, Home.class);
                activity.startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void removeDraft(Activity activity, int index) {

        List<String> items = getShared(activity);

        try{
            items.remove(index);
            save(activity, items);
        }catch (IndexOutOfBoundsException e){
            Toast.makeText(activity, "Unable to clear draft", Toast.LENGTH_SHORT).show();
        }

    }

    private static List<String> getShared(Activity activity){

        SharedPreferences sharedPreferences = activity.getSharedPreferences("DRAFT_PREFS", 0);

        String wordString = sharedPreferences.getString("draft_list", "");
        String[] itemsWords = wordString.split(",");
        List<String> items = new ArrayList<String>();

        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        if(itemsWords[0].equals("Draft is Empty")){
            itemsWords[0] = "Draft";
        }
        Collections.addAll(items, itemsWords);

        return  items;

    }

    private static void save(Activity activity, List<String> items){

        StringBuilder stringBuilder = new StringBuilder();
        for(String s : items){
            if(!s.equals("")){
                stringBuilder.append(s);
                stringBuilder.append(",");
            }
        }

        SharedPreferences sharedPreferences = activity.getSharedPreferences("DRAFT_PREFS", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("draft_list", stringBuilder.toString());
        editor.apply();
    }

    private static void clearDrafts(){

    }


}

