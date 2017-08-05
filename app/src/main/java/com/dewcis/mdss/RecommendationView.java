package com.dewcis.mdss;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
import java.util.Iterator;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class RecommendationView extends Activity {

    String TAG = MApplication.TAG;
    static boolean doLog = MApplication.LOGDEBUG;
    static String CTAG = MainActivity.class.getName() + " : " ;
    MaterialEditText community, health_facility, patient, phone, other_facility, message_clinician,
            age, message_back, patient_status;
    TextView actions;
    int survey_id;
    String val, active = "";
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_view);

        community = (MaterialEditText) findViewById(R.id.community);
        health_facility = (MaterialEditText) findViewById(R.id.message_clinician);
        patient = (MaterialEditText) findViewById(R.id.patient_name);
        phone = (MaterialEditText) findViewById(R.id.patient_number);
        age = (MaterialEditText) findViewById(R.id.patient_age);
        other_facility = (MaterialEditText) findViewById(R.id.other_facility);
        message_back = (MaterialEditText) findViewById(R.id.message_back);
        message_clinician = (MaterialEditText) findViewById(R.id.message_clinician);
        patient_status = (MaterialEditText) findViewById(R.id.patient_status);
        actions = (TextView) findViewById(R.id.actions);

        title = (TextView) findViewById(R.id.myTitle);

        precedingMessage();

        age.setFocusable(false);
        patient_status.setFocusable(false);
        community.setFocusable(false);
        health_facility.setFocusable(false);
        patient.setFocusable(false);
        phone.setFocusable(false);
        other_facility.setFocusable(false);
        message_clinician.setFocusable(false);

    }

    private void precedingMessage() {

        Bundle extra = getIntent().getExtras();

        JSONObject jsonSend = new JSONObject();

        SharedPreferences sharedpreferences = getSharedPreferences(Home.MESSAGE_PREFERENCE, Context.MODE_PRIVATE);

        String adapt = sharedpreferences.getString("json", null);
        survey_id = Integer.parseInt(sharedpreferences.getString("survey_id", null));

        try {
            JSONObject jsonObject = new JSONObject(adapt);
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key2 = iter.next();
                try {
                    Object value2 = jsonObject.get(key2);
                    jsonSend.put(key2, value2);

                } catch (JSONException e) {
                    // Something went wrong
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            String type = jsonSend.getString("patient_age_type");

            if(type.equals("Y")){
                type = "Years";
            }else if(type.equals("M")){
                type = "Months";
            }else{
                type = "Days";
            }

            age.setText(jsonSend.getString("patient_age") + " " + type);
            community.setText(jsonSend.getString("village_name"));
            patient_status.setText(jsonSend.getString("category"));

            patient.setText(jsonSend.getString("patient_name"));
            phone.setText(jsonSend.getString("form_serial"));
            other_facility.setText(jsonSend.getString("other_facility"));

            if(extra != null){
                int comm = extra.getInt("activity_type");

                if(comm == 1) {
                    actions.setText("Health Workers Instructions");
                    health_facility.setText(jsonSend.getString("instructions"));
                }
                if(comm == 2) {
                    title.setText("System Alert");
                    actions.setText("System Alert");
                    active = "alert";
                    health_facility.setText(jsonSend.getString("alerts"));
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("json");
        editor.remove("survey_id");
        editor.commit();


    }
    public void showDescription(View v){
        //Toast.makeText(getApplicationContext(), v.getContentDescription().toString(), Toast.LENGTH_SHORT).show();
        new SweetAlertDialog(RecommendationView.this,SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Details")
                .setContentText(v.getContentDescription().toString())
                .show();
    }

    public void getName(final Context context, final String registration){
        final ProgressDialog pDialog = new ProgressDialog(RecommendationView.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);
        MUtil.showProgressDialog(pDialog);
        String url = MApplication.url + "getRegID";
        String response = message_back.getText().toString();

        JSONObject details = new JSONObject();

        try {
            details.put("registration_no", registration);
            details.put("survey_id", survey_id);
            details.put("response", response);
            details.put("activity", active);
            details.put("class_name", "recommendation");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(doLog) Log.i(MApplication.TAG, RecommendationView.class.getName() + " : Details " + details.toString());
        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        if(doLog) Log.i(TAG, CTAG + response.toString());
                        Log.i(TAG, LoginActivity.class.getName() + "Response : " +response.toString() + "\nSuccess : " + success.isSuccess() + "\nMessage : " + success.getMessage());
                        MUtil.hideProgressDialog(pDialog);
                        ArrayList<HashMap<String,String>> arrayListAreas;
                        if(success.isSuccess()){
                            Toast.makeText(context , "Response sent successfully" , Toast.LENGTH_SHORT).show();

                            RecommendationView.this.finish();
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Crouton.makeText(RecommendationView.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "getRegID");
    }

    public void sendStat(View view) {

        String message = message_back.getText().toString();
        if(message.equals("")){
            showInvalidMessage(R.string.needAction); return;
        }

        getName(getApplicationContext(), 2 + "");

    }
    public void showInvalidMessage(int message){
        new SweetAlertDialog(RecommendationView.this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("" + getResources().getString(message)).show();
    }
}
