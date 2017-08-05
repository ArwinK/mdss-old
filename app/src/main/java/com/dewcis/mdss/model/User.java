package com.dewcis.mdss.model;

import android.util.Log;

import com.dewcis.mdss.MApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by henriquedn on 4/14/15.
 * henrydkm@gmail.com
 * henry.maina@dewcis.com
 */
public class User {
    static final boolean doLog = MApplication.LOGDEBUG;
    static final String TAG = MApplication.TAG, CTAG = User.class.getName() + " : ";
    public static final String ID = "health_worker_id",
            NAME = "worker_name",
            ORG_ID = "org_id",
            DEVICE_ID = "device_id",
            MOBILE_NUM = "worker_mobile_num",
            FIRST_LOGIN = "is_first_login",
            AUTH = "auth";

    int userId, deviceId, orgId;
    String   name = "", mobileNumber = "", auth = "";
    boolean firstLogin = true;

    public User(){

    }


    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getOrgId() {
        return orgId;
    }

    public void setOrgId(int orgId) {
        this.orgId = orgId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public boolean isFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(boolean firstLogin) {
        this.firstLogin = firstLogin;
    }


    public static User makeFromJson(JSONObject juser){
        User user = new User();
        try {
            user.setUserId(juser.getInt(ID));
            user.setOrgId(juser.getInt(ORG_ID));
            user.setName(juser.getString(NAME));
            user.setMobileNumber(juser.getString(MOBILE_NUM));
            user.setDeviceId(juser.getInt(DEVICE_ID));
            user.setFirstLogin(juser.getBoolean(FIRST_LOGIN));
        }catch (JSONException e){
            if(doLog) Log.e(TAG, CTAG + e.toString());
        }
        return user;
    }

    public JSONObject toJson(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(User.ID, getUserId());
            jsonObject.put(User.NAME, getName());
            jsonObject.put(User.ORG_ID, getOrgId());
            jsonObject.put(User.DEVICE_ID, getDeviceId());
            jsonObject.put(User.MOBILE_NUM, getMobileNumber());
            jsonObject.put(User.FIRST_LOGIN, isFirstLogin());
        } catch (JSONException e) {
            if(doLog) Log.e(TAG, CTAG + e.toString());
        }


        return jsonObject;
    }


    public static ArrayList<User> makeArrayList(JSONArray jarray){
        ArrayList<User> users =  new ArrayList<User>();
        for(int i = 0; i < jarray.length(); i++ ){
            try{
                users.add(makeFromJson(jarray.getJSONObject(i)));
            }catch (JSONException e){
                if(doLog) Log.e(TAG, CTAG + e.toString());
            }
        }
        return users;
    }


}
