package com.dewcis.mdss.model;

import android.util.Log;

import com.dewcis.mdss.MApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Arwin Kish on 11/9/2016.
 */
public class Alerts {
    static final String TAG = MApplication.TAG;
    static boolean doLog = MApplication.LOGDEBUG;
    static String CTAG = Alerts.class.getName() + " : " ;

    public static final String SURVEY = "Alerts";
    public static final String VILLAGE_ID = "village_id";

    public JSONObject getJsurvey() {
        return jsurvey;
    }

    public void setJsurvey(JSONObject jsurvey) {
        this.jsurvey = jsurvey;
    }

    JSONObject jsurvey ;

    public int getSurvey_id() {
        return survey_id;
    }

    public void setSurvey_id(int survey_id) {
        this.survey_id = survey_id;
    }

    public int getvillageID() {
        return villageID;
    }

    public void setvillageID(int villageID) {
        this.villageID = villageID;
    }

    int survey_id, villageID;

    //data for one submission

    String txtVillageName;
    String txtHouseholdNum;
    String txtHouseholdMember;
    String returnReason;
    String txtRemarks;
    String txtOficerName;

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }


    public Alerts(){

    }

    public static ArrayList<Alerts> makeArraylist(JSONArray jsonArray){

        ArrayList<Alerts> alerts = new ArrayList<Alerts>();
        try{
            for(int i = 0; i < jsonArray.length(); i++){
                alerts.add(Alerts.makeFromJSON(jsonArray.getJSONObject(i)));
            }
        }catch (JSONException e){
            if(doLog) Log.e(TAG, CTAG  + e.toString());
        }

        return alerts;
    }

    public static Alerts makeFromJSON(JSONObject jsonObject){
        Alerts s = new Alerts();
        s.setJsurvey(jsonObject);
        try{
            JSONObject basicInfo = jsonObject.getJSONObject("Alerts");

            s.setSurvey_id(basicInfo.getInt("survey_id"));
            s.setvillageID(basicInfo.getInt(VILLAGE_ID));
            s.setTxtVillageName(basicInfo.getString("village_name"));
            s.setTxtHouseholdNum(basicInfo.getString("patient_name"));
            s.setTxtHouseholdMember(basicInfo.getString("category"));
            s.setTxtOficerName(basicInfo.getString("health_facility_name"));
            s.setTxtRemarks(basicInfo.getString("location_lat"));
            s.setReturnReason(basicInfo.getString("category"));

        }catch (JSONException e){
            if(doLog) Log.e(TAG, CTAG  + e.toString());
        }
        return s;
    }



    public String getTxtVillageName() {
        return txtVillageName;
    }

    public void setTxtVillageName(String txtVillageName) {
        this.txtVillageName = txtVillageName;
    }

    public String getTxtHouseholdNum() {
        return txtHouseholdNum;
    }

    public void setTxtHouseholdNum(String txtHouseholdNum) {
        this.txtHouseholdNum = txtHouseholdNum;
    }

    public String getTxtHouseholdMember() {
        return txtHouseholdMember;
    }

    public void setTxtHouseholdMember(String txtHouseholdMember) {
        this.txtHouseholdMember = txtHouseholdMember;
    }

    public void setTxtRemarks(String txtRemarks) {
        this.txtRemarks = txtRemarks;
    }

    public String getTxtRemarks() {
        return txtRemarks;
    }

    public String getTxtOficerName() {
        return txtHouseholdMember;
    }


    public void setTxtOficerName(String txtOficerName) {
        this.txtOficerName = txtOficerName;
    }
}
