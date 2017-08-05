package com.dewcis.mdss.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import com.dewcis.mdss.MApplication;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by henriquedn on 6/24/15.
 * henrydkm@gmail.com
 * henry.maina@dewcis.com
 */
public class MUtil {
    public static String getAppVersionName(Context context){
        String versionName = "Latest";
        try {
            versionName = context.getPackageManager().getPackageInfo(MApplication.PACKAGE, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return versionName;
    }

    public static void showProgressDialog(ProgressDialog pDialog) {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    public static void hideProgressDialog(ProgressDialog pDialog) {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public static boolean isNetworkConnected(Context context) {
        boolean connected = false;
        boolean con_wifi = false , con_mobile = false;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (mWifi.isConnected()){

            connected = true;
            con_wifi =true;

        }

        ConnectivityManager connManager1 = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mMobile = connManager1.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mMobile.isConnected()) {

            connected = true;
            con_mobile = true;

        }

        if(con_mobile && con_wifi){
            //Toast.makeText(context, "Both Mobile and WIfi on.", Toast.LENGTH_SHORT).show();
        }

        return connected;
        /*ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()&& cm.getActiveNetworkInfo().isConnectedOrConnecting());*/
    }
    public static JSONObject simpleJSOnMaker(String key, Object value){
        JSONObject json = new JSONObject();
        try {
            json.put(key, value);
        } catch (JSONException e) {
            Log.e(MApplication.TAG, MUtil.class.getName() + " : " + e.toString());
        }
        return json;
    }


    public static Object simpleJsonGetter(JSONObject jsonObject, String key){
        Object val = null;
        try {
            val = jsonObject.get(key);
        } catch (JSONException e) {
            Log.e(MApplication.TAG, MUtil.class.getName() + " : " + e.toString());
        }
        return val;
    }
    public static String MD5Encrypt(String pass){
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            //Add password bytes to digest
            md.update(pass.getBytes());
            //Get the hash's bytes
            byte[] bytes = md.digest();
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        //System.out.println(generatedPassword);
        return  generatedPassword;

    }


    public static void setVisibility(int visibility, View[] views){
        for(View v: views){
            v.setVisibility(visibility);
        }
    }


    public void about(Context context){
        String text = "";
        new SweetAlertDialog(context,SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("About")
                .setContentText(text)
                .show();
    }
}
