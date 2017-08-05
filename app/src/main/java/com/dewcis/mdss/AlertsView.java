package com.dewcis.mdss;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dewcis.mdss.model.Success;
import com.dewcis.mdss.utils.MUtil;
import com.dewcis.mdss.volley.VolleyErrorHelper;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class AlertsView extends ActionBarActivity {

    String TAG = MApplication.TAG;
    static boolean doLog = MApplication.LOGDEBUG;
    static String CTAG = MainActivity.class.getName() + " : " ;
    MaterialEditText message_back, health_facility, patient, phone, other_facility, message_clinician;
    int survey_id = 0;
    Spinner status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerts_view);

        health_facility = (MaterialEditText) findViewById(R.id.message_clinician);
        patient = (MaterialEditText) findViewById(R.id.patient_name);
        phone = (MaterialEditText) findViewById(R.id.patient_number);
        other_facility = (MaterialEditText) findViewById(R.id.other_facility);
        message_clinician = (MaterialEditText) findViewById(R.id.message_clinician);
        message_back = (MaterialEditText) findViewById(R.id.message_response);

        precedingAlerts();

        health_facility.setFocusable(false);
        patient.setFocusable(false);
        phone.setFocusable(false);
        other_facility.setFocusable(false);
        message_clinician.setFocusable(false);

    }
    private void precedingAlerts() {

        SharedPreferences sharedpreferences = getSharedPreferences(Home.ALERT_PREFERENCE, Context.MODE_PRIVATE);

        health_facility.setText(sharedpreferences.getString("health_facility_name1", null));
        patient.setText(sharedpreferences.getString("patient1", null));
        phone.setText(sharedpreferences.getString("form_serial1", null));
        other_facility.setText(sharedpreferences.getString("other_facility1", null));
        message_clinician.setText(sharedpreferences.getString("instructions1", null));
        survey_id = sharedpreferences.getInt("survy_id1", 0);

    }
    public void showDescription(View v){
        //Toast.makeText(getApplicationContext(), v.getContentDescription().toString(), Toast.LENGTH_SHORT).show();
        new SweetAlertDialog(AlertsView.this,SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Details")
                .setContentText(v.getContentDescription().toString())
                .show();
    }

    public void getName(final Context context, final String registration, final String returnMessage){
        final ProgressDialog pDialog = new ProgressDialog(AlertsView.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);
        MUtil.showProgressDialog(pDialog);
        String url = MApplication.url + "getRegID";

        JSONObject details = new JSONObject();

        try {
            details.put("registration_no", registration);
            details.put("returnMessage", returnMessage);
            details.put("survey_id", survey_id);
            details.put("class_name", "recommendation");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(doLog) Log.i(MApplication.TAG, MainActivity.class.getName() + " : Details " + details.toString());
        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        if(doLog) Log.i(TAG, CTAG + response.toString());
                        Log.i(TAG, LoginActivity.class.getName() + "Response : " +response.toString() + "\nSuccess : " + success.isSuccess() + "\nMessage : " + success.getMessage());
                        MUtil.hideProgressDialog(pDialog);
                        ArrayList<HashMap<String,String>> arrayListAreas;
                        if(success.isSuccess()){
                            Crouton.makeText(AlertsView.this , success.getMessage() , Style.CONFIRM).show();

                            //Toast.makeText(getApplicationContext(), name , Toast.LENGTH_LONG).show();

                        }else{

                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Crouton.makeText(AlertsView.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "getRegID");
    }

    public void sendStat(View view) {

        String return_message = message_back.getText().toString();
        if(!return_message.equals("")){
            getName(getApplicationContext(), 3 + "", return_message);
        }else{
            Toast.makeText(getApplicationContext(), "Please ", Toast.LENGTH_LONG).show();
        }


    }
}
