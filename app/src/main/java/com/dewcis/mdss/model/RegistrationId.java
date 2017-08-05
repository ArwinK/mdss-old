package com.dewcis.mdss.model;

import android.util.Log;
import com.dewcis.mdss.MApplication;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
/**
 * Created by Arwin Kish on 10/19/2016.
 */
public class RegistrationId {

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

    public String getName() {
        return returnReason;
    }

    public void setName(String returnReason) {
        this.returnReason = returnReason;
    }


    public RegistrationId(){

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

    public static RegistrationId makeFromJSON(JSONObject jsonObject){
        RegistrationId s = new RegistrationId();
        s.setJsurvey(jsonObject);
        try{
            JSONObject basicInfo = jsonObject.getJSONObject("basicInfo");

            s.setSurvey_id(basicInfo.getInt("survey_id"));
            s.setTxtGender(basicInfo.getString("gender"));
            s.setName(basicInfo.getString("name"));

        }catch (JSONException e){
            if(doLog) Log.e(TAG, CTAG  + e.toString());
        }
        return s;
    }

    public String getTxtGender() {
        return txtVillageName;
    }

    public void setTxtGender(String txtVillageName) {
        this.txtVillageName = txtVillageName;
    }


    public void setTxtRemarks(String txtRemarks) {
        this.txtRemarks = txtRemarks;
    }

    public String getTxtOficerName() {
        return txtHouseholdMember;
    }


    public void setTxtOficerName(String txtOficerName) {
        this.txtOficerName = txtOficerName;
    }
}

