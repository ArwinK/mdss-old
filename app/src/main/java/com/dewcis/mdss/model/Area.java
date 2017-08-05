package com.dewcis.mdss.model;

import android.app.Activity;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.dewcis.mdss.MApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by henriquedn on 8/26/15.
 * henrydkm@gmail.com
 * henry.maina@dewcis.com
 */
public class Area {
    static final boolean doLog = MApplication.LOGDEBUG;
    static final String TAG = MApplication.TAG, CTAG = Area.class.getName() + " : ";
    public static final String ID = "id", NAME = "name";
    Integer id;
    String name = "";

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) {  this.name = name;}

    public Area(){}


    public Area(int id, String name){
        this.setId(id);
        this.setName(name);
    }

    public static ArrayList<Area> makeArrayList(JSONArray jsonArray){
        ArrayList<Area> areas = new ArrayList<Area>();
        for(int i = 0; i < jsonArray.length(); i++ ){
            try{
                areas.add(makeFromJson(jsonArray.getJSONObject(i)));
            }catch (JSONException e){
                if(doLog) Log.e(TAG, CTAG + e.toString());
            }
        }


        return areas;
    }

    public static Area makeFromJson(JSONObject jarea){
        Area area = new Area();
        try {
            area.setId(jarea.getInt(ID));
            area.setName(jarea.getString(NAME));
        }catch (JSONException e){
            if(doLog) Log.e(TAG, CTAG + e.toString());
        }
        return area;
    }


    public static ArrayAdapter<String> getArrayAdapter(Activity activity,  ArrayList<Area> areas){
        ArrayList<String> sareas = new ArrayList<String>();
        for(int i = 0; i < areas.size(); i++){
            sareas.add(areas.get(i).getName());
        }

        return new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, sareas);
    }

}
