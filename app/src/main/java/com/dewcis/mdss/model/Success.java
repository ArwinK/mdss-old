package com.dewcis.mdss.model;

import android.util.Log;

import com.dewcis.mdss.MApplication;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by henriquedn on 6/24/15.
 */
public class Success {
    static final String SUCCESS = "success", MESSAGE = "message", SURVEY_ID = "survey_id";
    static final int OK = 1, FAIL = 0;
    boolean success;
    String message;
    int survey_id;

    public Success(){

    }

    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public void setSuccess(int success) {
        this.success = (success == 1);
    }
    public String getMessage() {
        return message;
    }
    public void setSurvey(int survey_id) {
        this.survey_id = survey_id;
    }
    public int getSurveyId() {
        return survey_id;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public static Success makeFromJson(JSONObject jsonObject){
        Success success = new Success();
        try {
            success.setSuccess( jsonObject.getInt(SUCCESS));
            success.setMessage(jsonObject.getString(MESSAGE));
            success.setSurvey(jsonObject.getInt(SURVEY_ID));
        } catch (JSONException e) {
            Log.i(MApplication.TAG, Success.class.getName() + " : Error " + e.toString());
        }
        return success;
    }
}
