package com.dewcis.mdss.model;

/**
 * Created by henriquedn on 4/29/15.
 * henrydkm@gmail.com
 * henry.maina@dewcis.com
 */

import android.util.Log;
import com.dewcis.mdss.MApplication;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Message {
    static final String TAG = MApplication.TAG;
    static boolean doLog = MApplication.LOGDEBUG;
    static String CTAG = Survey.class.getName() + " : " ;

    public static final String SURVEY = "survey", VILLAGE_ID = "village_id", SUB_LOCATION_ID = "sub_location_id";

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


    public Message(){

    }

    public static ArrayList<Message> makeArraylist(JSONArray jsonArray){

        ArrayList<Message> messages = new ArrayList<Message>();
        try{
            for(int i = 0; i < jsonArray.length(); i++){
                messages.add(Message.makeFromJSON(jsonArray.getJSONObject(i)));
            }
        }catch (JSONException e){
            if(doLog) Log.e(TAG, CTAG  + e.toString());
        }

        return messages;
    }

    public static Message makeFromJSON(JSONObject jsonObject){
        Message s = new Message();
        s.setJsurvey(jsonObject);
        try{
            JSONObject basicInfo = jsonObject.getJSONObject("basicInfo");

            s.setSurvey_id(basicInfo.getInt("survey_id"));
            s.setvillageID(basicInfo.getInt(VILLAGE_ID));
            s.setTxtVillageName(basicInfo.getString("village_name"));
            s.setTxtHouseholdNum(basicInfo.getString("health_facility_name"));
            s.setTxtHouseholdMember(basicInfo.getString("patient_name"));
            s.setTxtOficerName(basicInfo.getString("location_lat"));
            s.setTxtRemarks(basicInfo.getString("remarks"));
            s.setReturnReason(basicInfo.getString("returnReason"));

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


