package com.dewcis.mdss.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dewcis.mdss.LoginActivity;
import com.dewcis.mdss.MApplication;
import com.dewcis.mdss.utils.MUtil;
import com.dewcis.mdss.utils.SecurePreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by henriquedn on 4/14/15.
 * henrydkm@gmail.com
 * henry.maina@dewcis.com
 */

public class MSession {

    static final String USER_ID = User.ID, AUTH = "auth";
    String TAG = MApplication.TAG;
    static boolean doLog = MApplication.LOGDEBUG;
    static String CTAG = MSession.class.getName() + " : " ;
    User user;
    SecurePreferences pref;
    Context context;
    String ENCRYPT_KEY = "";
    int PRIVATE_MODE = 0;
    // Sharedpref file name
    private static final String PREF_NAME = "crhpa";
    private static final String KEY_SECURE_KEY = "4o[88a��m|v3ia[[@]23c,f,a7{�b|}t8ii][[5ww4qd1kbsk1@.m<[[pd~qc3<84k3e�xoc,8qy[{y6hamk�>>ryisnjocj8mrl";
    private static final String IS_LOGIN = "IsLoggedIn";

    public User getUser() {
        User user = new User();
        try{
            user.setUserId(Integer.parseInt(pref.getString(User.ID)));
            user.setName(pref.getString(User.NAME));
            user.setOrgId(Integer.parseInt(pref.getString(User.ORG_ID)));
            user.setDeviceId(Integer.parseInt(pref.getString(User.DEVICE_ID)));
            user.setMobileNumber(pref.getString(User.MOBILE_NUM));
            user.setFirstLogin(pref.getBoolean(User.FIRST_LOGIN));
        }catch (NumberFormatException nfe){
            if(doLog) Log.e(TAG, CTAG + nfe.toString());
        }

        return user;
    }

    public void setUser(User user) {
        this.user = user;
        this.ENCRYPT_KEY = MUtil.MD5Encrypt(user.getMobileNumber() + user.getUserId());
    }

    public MSession(Context context){
        this.context = context;
        pref = new SecurePreferences(context, PREF_NAME, KEY_SECURE_KEY, true);// _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
    }

    public Map<String, Integer> getHeaderParams(){
        Map<String, Integer> params = new HashMap<String, Integer>();
        params.put(USER_ID, getUser().getUserId());
        params.put(AUTH, getUser().getDeviceId());
        return params;
    }

    public void save(){
        pref.put(IS_LOGIN, true);
        pref.put(User.ID, String.valueOf(user.getUserId()));
        pref.put(User.NAME, user.getName());
        pref.put(User.ORG_ID, String.valueOf(user.getOrgId()));
        pref.put(User.DEVICE_ID, String.valueOf(user.getDeviceId()));
        pref.put(User.MOBILE_NUM, user.getMobileNumber());
        pref.put(User.FIRST_LOGIN, user.isFirstLogin());
    }

    public void logout(Activity activity){
        pref.removeValue(IS_LOGIN);
        pref.removeValue(User.ID);
        pref.removeValue(User.NAME);
        pref.removeValue(User.ORG_ID);
        pref.removeValue(User.DEVICE_ID);
        pref.removeValue(User.MOBILE_NUM);

        Intent i = new Intent(context, LoginActivity.class);

        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        context.startActivity(i);
        activity.finish();
    }





    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            context.startActivity(i);
        }

    }
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN);
    }
}
