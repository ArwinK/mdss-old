package com.dewcis.mdss;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dewcis.mdss.adapters.AlertAdapter;
import com.dewcis.mdss.adapters.MessageAdapter;
import com.dewcis.mdss.adapters.SurveyAdapter;
import com.dewcis.mdss.model.Alerts;
import com.dewcis.mdss.model.MSession;
import com.dewcis.mdss.model.Message;
import com.dewcis.mdss.model.Success;
import com.dewcis.mdss.model.Survey;
import com.dewcis.mdss.model.User;
import com.dewcis.mdss.utils.AboutDialog;
import com.dewcis.mdss.utils.ExpandablePanel;
import com.dewcis.mdss.utils.MUtil;
import com.dewcis.mdss.volley.VolleyErrorHelper;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


/**
 * Created by henriquedn on 4/14/15.
 * henrydkm@gmail.com
 * henry.maina@dewcis.com
 */

public class Home extends ActionBarActivity implements View.OnClickListener, ExpandablePanel.OnExpandListener{

    public static final String MESSAGE_PREFERENCE = "MessagePref";
    public static final String ALERT_PREFERENCE = "AlertPrefs";
    MSession session;
    String TAG = MApplication.TAG;
    static boolean doLog = MApplication.LOGDEBUG;
    static String CTAG = Home.class.getName() + " : " ;
    Button btnNewSurvey;
    Button btnNew100;
    ListView mlist, elist, alist;
    ArrayList<Message> mMessages; ArrayList<Alerts> mAlerts; ArrayList<Survey> mSurveys;
    MessageAdapter mMessageAdapter;
    AlertAdapter mAlertAdapter;
    SurveyAdapter mSurveyAdapter;
    ExpandablePanel panelMessages, panelReturned;
    MaterialEditText txtRegId;
    String mobile_num= "";
    String name = "", age = "", guardian = "";
    String json, jsonOne;
    String officer_name, instructions_alert, patient_alert, form_serial_alert,
            health_facility_name_alert, other_facility_alert;
    int survy_id_alert;
    String survey_id, survey_id_alerts;

    Bundle mBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        session = new MSession(getApplicationContext());

        if(!session.isLoggedIn()){
            session.logout(this);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));
        actionBar.setSubtitle("Home");
        actionBar.setIcon(R.drawable.aphrc);
        actionBar.setHomeButtonEnabled(true);

        TextView lblDisplayUser = (TextView) findViewById(R.id.lblDisplayUser);
        TextView lblRefresh = (TextView) findViewById(R.id.lblRefresh);
        lblDisplayUser.setText("Welcome " + session.getUser().getName());

        mlist = (ListView) findViewById(R.id.mlist);
        elist = (ListView) findViewById(R.id.elist);
        alist = (ListView) findViewById(R.id.alist);

        txtRegId = (MaterialEditText) findViewById(R.id.txtRegId);

        btnNewSurvey = (Button) findViewById(R.id.btnNewSurvey);
        btnNew100 = (Button) findViewById(R.id.btnNew100);
        btnNewSurvey.setOnClickListener(this);
        btnNew100.setOnClickListener(this);

        panelMessages = (ExpandablePanel) findViewById(R.id.panelMessages);
        panelReturned = (ExpandablePanel) findViewById(R.id.panelReturned);
        panelReturned = (ExpandablePanel) findViewById(R.id.panelClinicianAlerts);

        panelMessages.setOnExpandListener(this);
        panelReturned.setOnExpandListener(this);
        panelReturned.setOnExpandListener(this);

        mMessages = new ArrayList<>();
        mSurveys = new ArrayList<>();
        mAlerts = new ArrayList<>();

        mSurveyAdapter = new SurveyAdapter(getApplicationContext(), mSurveys);
        mMessageAdapter = new MessageAdapter(getApplicationContext(), mMessages);
        mAlertAdapter = new AlertAdapter(getApplicationContext(), mAlerts);

        mlist.setAdapter(mMessageAdapter);
        elist.setAdapter(mSurveyAdapter);
        alist.setAdapter(mAlertAdapter);

        lblRefresh.setOnClickListener(this);

        getSurveys(getApplicationContext());
        getAlerts(getApplicationContext());
        getComments(getApplicationContext());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_logout:
                session.logout(this);

                break;
            case R.id.action_about:
                new AboutDialog(this).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setCancelable(false);
        builder.setTitle("Are you sure you want to quit?");
        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Home.this.finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        getSurveys(getApplicationContext());
        getAlerts(getApplicationContext());
        getComments(getApplicationContext());
        super.onResume();
    }

    public void getComments(final Context context){
        mMessages.clear();
        mMessageAdapter = new MessageAdapter(context, mMessages);
        mlist.setAdapter(mMessageAdapter);


        JSONObject details = new JSONObject();
        try {
            details.put(User.ID, session.getUser().getUserId());
            details.put("app_version", MUtil.getAppVersionName(getApplicationContext()));

        } catch (JSONException e) {
            Log.e(TAG, LoginActivity.class.getName() + " : " + e.toString());
        }
        final ProgressDialog pDialog = new ProgressDialog(Home.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        MUtil.showProgressDialog(pDialog);
        String url = MApplication.url + "getComments";

        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(final JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        if(doLog) Log.i(TAG, CTAG + response.toString());
                        Log.i(TAG, LoginActivity.class.getName() + "Response : " +response.toString() + "\nSuccess : " + success.isSuccess() + "\nMessage : " + success.getMessage());
                        MUtil.hideProgressDialog(pDialog);
                        JSONArray toIntent = null;
                        try {
                            toIntent = response.getJSONArray("surveys");
                            JSONObject jsonObject = toIntent.getJSONObject(0).getJSONObject("basicInfo");

                            survey_id = jsonObject.getString("survey_id");

                            json = jsonObject.toString();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        if(success.isSuccess()){

                            try{

                                JSONArray surveys = response.getJSONArray("surveys");

                                mMessages.clear();
                                mMessages = Message.makeArraylist(surveys);

                                mMessageAdapter = new MessageAdapter(context, mMessages);

                                mlist.setAdapter(mMessageAdapter);
                                mMessageAdapter.notifyDataSetChanged();

                                mlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Message survey = mMessages.get(position);
                                        Intent intent = new Intent(Home.this, RecommendationView.class);
                                        intent.putExtra("activity_type", 1);

                                        mAlerts.clear();

                                        SharedPreferences sharedPreferences = getSharedPreferences(MESSAGE_PREFERENCE, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        editor.putString("json", json);
                                        editor.putString("survey_id", survey_id);
                                        editor.putString(Message.SURVEY, survey.getJsurvey().toString());
                                        editor.apply();

                                        startActivity(intent);

                                    }
                                });



                            }catch (JSONException e){
                                Crouton.makeText(Home.this, "Sorry, Could Not Create Session", Style.ALERT).show();
                            }

                        }else{
                            Crouton.makeText(Home.this , success.getMessage() , Style.ALERT).show();
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Log.e(TAG , CTAG + error.getMessage());
                        Crouton.makeText(Home.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return super.getHeaders();
            }
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "getComments");
    }

    public void getSurveys(final Context context){
        mSurveys.clear();
        mSurveyAdapter = new SurveyAdapter(context, mSurveys);
        elist.setAdapter(mSurveyAdapter);

        JSONObject details = new JSONObject();

        try {
            details.put(User.ID, session.getUser().getUserId());
            details.put("app_version", MUtil.getAppVersionName(getApplicationContext()));

        } catch (JSONException e) {
            Log.e(TAG, LoginActivity.class.getName() + " : " + e.toString());
        }
        final ProgressDialog pDialog = new ProgressDialog(Home.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        MUtil.showProgressDialog(pDialog);
        String url = MApplication.url + "getSurveys";

        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        if(doLog) Log.i(TAG, CTAG + response.toString());
                        Log.i(TAG, LoginActivity.class.getName() + "Response : " +response.toString() + "\nSuccess : " + success.isSuccess() + "\nMessage : " + success.getMessage());
                        MUtil.hideProgressDialog(pDialog);
                        if(success.isSuccess()){
                            try{
                                JSONArray surveys = response.getJSONArray("surveys");
                                mSurveys.clear();
                                mSurveys = Survey.makeArraylist(surveys);

                                mSurveyAdapter = new SurveyAdapter(context, mSurveys);

                                elist.setAdapter(mSurveyAdapter);
                                mSurveyAdapter.notifyDataSetChanged();

                                elist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Survey survey = mSurveys.get(position);
                                        Intent mIntent = new Intent(Home.this, MainActivityOr.class);

                                        mBundle = new Bundle();
                                        mBundle.putString(Survey.SURVEY, survey.getJsurvey().toString());
                                        mIntent.putExtras(mBundle);

                                        startActivity(mIntent);

                                    }
                                });

                            }catch (JSONException e){
                                Crouton.makeText(Home.this, "Sorry, Could Not Create Session", Style.ALERT).show();
                            }

                        }else {
                            Crouton.makeText(Home.this , success.getMessage() , Style.ALERT).show();
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Log.e(TAG , CTAG + error.getMessage());
                        Crouton.makeText(Home.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return super.getHeaders();
            }
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "getSurveys");
    }

    public void getAlerts(final Context context){
        mAlerts.clear();
        mAlertAdapter = new AlertAdapter(context, mAlerts);
        alist.setAdapter(mAlertAdapter);

        JSONObject details = new JSONObject();
        try {
            details.put(User.ID, session.getUser().getUserId());
            details.put("app_version", MUtil.getAppVersionName(getApplicationContext()));

        } catch (JSONException e) {
            Log.e(TAG, LoginActivity.class.getName() + " : " + e.toString());
        }
        final ProgressDialog pDialog = new ProgressDialog(Home.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        MUtil.showProgressDialog(pDialog);
        String url = MApplication.url + "getAlerts";

        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(final JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        if(doLog) Log.i(TAG, CTAG + response.toString());
                        Log.i(TAG, LoginActivity.class.getName() + "Response : " +response.toString() + "\nSuccess : " + success.isSuccess() + "\nMessage : " + success.getMessage());
                        MUtil.hideProgressDialog(pDialog);

                        try {
                            JSONArray toIntent = response.getJSONArray("surveys_alerts");
                            JSONObject jsonObject = toIntent.getJSONObject(0).getJSONObject("Alerts");
                            survey_id_alerts = jsonObject.getString("survey_id");

                            jsonOne = jsonObject.toString();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if(success.isSuccess()){
                            try{

                                JSONArray surveys = response.getJSONArray("surveys_alerts");

                                mAlerts.clear();
                                mAlerts = Alerts.makeArraylist(surveys);

                                mAlertAdapter = new AlertAdapter(context, mAlerts);

                                alist.setAdapter(mAlertAdapter);
                                mAlertAdapter.notifyDataSetChanged();

                                alist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Alerts survey = mAlerts.get(position);
                                        Intent mIntent = new Intent(Home.this, RecommendationView.class);
                                        mIntent.putExtra("activity_type", 2);

                                        mMessages.clear();

                                        SharedPreferences sharedPreferences = getSharedPreferences(MESSAGE_PREFERENCE, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        editor.putString("json", jsonOne);
                                        editor.putString("survey_id", survey_id_alerts);
                                        editor.putString(Alerts.SURVEY, survey.getJsurvey().toString());
                                        editor.apply();

                                        startActivity(mIntent);

                                    }
                                });


                            }catch (JSONException e){
                                Crouton.makeText(Home.this, "Sorry, Could Not Create Session", Style.ALERT).show();
                            }

                        }else{
                            Crouton.makeText(Home.this , success.getMessage() , Style.ALERT).show();
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Log.e(TAG , CTAG + error.getMessage());
                        Crouton.makeText(Home.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return super.getHeaders();
            }
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "getSurveys");
    }


    public void getName(final Context context, final String registration){
        final ProgressDialog pDialog = new ProgressDialog(Home.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);
        MUtil.showProgressDialog(pDialog);
        String url = MApplication.url + "getRegID";

        JSONObject details = new JSONObject();
        try {
            details.put("registration_no", registration);
            details.put("class_name", "home");
            details.put("survey_id", 0);
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
                            Crouton.makeText(Home.this , success.getMessage() , Style.CONFIRM).show();

                            try {

                                JSONArray jsonArray = response.getJSONArray("surveys");

                                JSONObject jsonObject = jsonArray.getJSONObject(0).getJSONObject("basicInfo");
                                name = jsonObject.getString("name");
                                mobile_num = jsonObject.getString("mobile_num");
                                age = jsonObject.getString("age");
                                guardian = jsonObject.getString("guardian");

                                Toast.makeText(getApplicationContext(), "Danger signs have already been collected for this number", Toast.LENGTH_LONG).show();

                                eventSelector(name, mobile_num, age, guardian);
                            }catch (JSONException je) {
                                Log.e(TAG, CTAG + "Error Retrieving Json Array : " + je.toString());
                            }catch (Exception e) {
                                Log.e(TAG, CTAG + "Error Retrieving Json Array : " + e.toString());
                            }

                        }else{
                            eventSelector(name, registration, mobile_num, guardian);
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Crouton.makeText(Home.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "getRegID");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lblRefresh:
                getSurveys(getApplicationContext());
                break;
            case R.id.btnNewSurvey:
                Intent i = new Intent(getApplicationContext(), HouseHoldActivity.class);
                startActivity(i);
                break;
            case R.id.btnNew100:
                Intent fm100 = new Intent(getApplicationContext(), Form100Activity.class);
                startActivity(fm100);

                break;
        }
    }

    public void btn513(View view) {
        Intent fm513 = new Intent(getApplicationContext(), Form513Activity.class);
        startActivity(fm513);
    }

    @Override
    public void onExpand(View handle, View content) {

        TextView header = (TextView) handle;
        header.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_expand_less_white_18dp,0);
    }

    @Override
    public void onCollapse(View handle, View content) {
        TextView header = (TextView) handle;
        header.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.ic_expand_more_white_18dp,0);
    }

    public void setReg(View view) {
        String reg = txtRegId.getText().toString();
        Boolean bol;

        if(reg.equals("")){
            Toast.makeText(getApplicationContext(), "Enter a Registration ID or Mobile number", Toast.LENGTH_SHORT).show();
        }else if(reg.contains("DSS")){
            getName(getApplicationContext(), reg);
        }else{
            bol = isMobileValid(reg);
            if(bol && (reg.length()== 10)){
                getName(getApplicationContext(), reg);

            }else{
                Toast.makeText(getApplicationContext(), "Enter a Registration ID or Mobile number", Toast.LENGTH_SHORT).show();
            }

        }

    }
    public void eventSelector(final String name, final String reg, final String age, final String mother) {


        final CharSequence[] items = {"Mother's Danger Signs", "Newborn's Danger Signs"};
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setCancelable(false);
        builder.setTitle("Select A DSS option ");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0){
                    Intent intent = new Intent(getApplicationContext(), DssActivityMother.class);
                    intent.putExtra("mobile", reg);
                    intent.putExtra("user", session.getUser().getUserId());
                    intent.putExtra("name", name);
                    intent.putExtra("age", age);
                    intent.putExtra("guardian", mother);
                    txtRegId.setText("");
                    startActivity(intent);
                }else if(which == 1) {
                    Intent intent = new Intent(getApplicationContext(), DssActivity.class);
                    intent.putExtra("mobile", reg);
                    intent.putExtra("user", session.getUser().getUserId());
                    intent.putExtra("name", name);
                    intent.putExtra("age", age);
                    intent.putExtra("guardian", mother);
                    txtRegId.setText("");
                    startActivity(intent);
                }


            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
    public boolean isMobileValid(String text){

        return text.matches("^([0])([7])([0-9])+");
    }

}
