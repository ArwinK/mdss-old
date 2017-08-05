package com.dewcis.mdss;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dewcis.mdss.SharedPreferences.SharedPreference;
import com.dewcis.mdss.utils.DraftActivity;
import com.dewcis.mdss.model.MSession;
import com.dewcis.mdss.model.Success;
import com.dewcis.mdss.utils.ExpandablePanel;
import com.dewcis.mdss.utils.MUtil;
import com.dewcis.mdss.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class DangerSignsBaby extends ActionBarActivity implements ExpandablePanel.OnExpandListener, RadioGroup.OnCheckedChangeListener{

    String TAG = MApplication.TAG;
    static String CTAG = Form513Activity.class.getName() + " : " ;
    static boolean doLog = MApplication.LOGDEBUG;
    MSession session;
    ExpandablePanel panelSectionOne, panelSectionTwo, panelSectionThree, panelBreastfeedingHistory;
    RadioGroup motherGroup;
    String motherTypeValue = "";
    String mobile100;
    int val;
    boolean toDss = true;
    LinearLayout ln, ln_a, ln_btn;
    int survey, children, var_infants, index, postpartum, pregnant, infant, other_mothers, other_members;
    SharedPreference sharedPreference;
    Spinner f_sB, f_sC, y_sS, b_sD2, a_sA, n_s2, c_sC, u_sR, c_sD,n_bA ,
            y_kA ,b_hA ,b_vA ,t_dA ,s_hA ,f_bA ,c_dA ,l_wA, y_sA,s_fd,f_bs, c_fs;
    private boolean refer = true;
    String my514;
    int survey_status;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danger_signs_baby);

        session = new MSession(getApplicationContext());

        Toast.makeText(getApplicationContext(), "Fill in Dss survey (Children)", Toast.LENGTH_LONG).show();

        SharedPreferences sharedpreferences = getSharedPreferences(DssActivity.DSS_PREFERENCES, Context.MODE_PRIVATE);
        int age = sharedpreferences.getInt("age", 0);


        if(!session.isLoggedIn()){
            session.logout(this);
        }
        //tracePref();

        ln = (LinearLayout) findViewById(R.id.atBirthPanel);
        ln_a = (LinearLayout) findViewById(R.id.atFirst28);
        ln_btn = (LinearLayout) findViewById(R.id.radio_buttons);

        sharedPreference = new SharedPreference();

        SharedPreferences sharedPrefsHousehold = getSharedPreferences(HouseHoldActivity.HOUSEHOLD_INFORMATION , Context.MODE_PRIVATE);
        infant = sharedPrefsHousehold.getInt("infants", 0);
        postpartum = sharedPreference.getValue(getApplicationContext(), "postpartum");
        pregnant = sharedPreference.getValue(getApplicationContext(), "pregnant");
        children = sharedPreference.getValue(getApplicationContext(), "children");
        other_mothers = sharedPreference.getValue(getApplicationContext(), "other_mothers");
        other_members = sharedPreference.getValue(getApplicationContext(), "other_mothers");
        survey_status = sharedpreferences.getInt("survey_status", 0);

        survey = sharedPreference.getValue(this, "survey_id");
        var_infants = sharedPreference.getValue(getApplicationContext(), "infant");

        infant = infant - var_infants;

        children = infant;

        n_bA = ((Spinner) findViewById(R.id.not_breathing));
        y_kA = ((Spinner) findViewById(R.id.yellow_skin));
        b_hA = ((Spinner) findViewById(R.id.feet_blue));
        b_vA = ((Spinner) findViewById(R.id.not_sucking));
        t_dA = ((Spinner) findViewById(R.id.tiredness));
        s_hA = ((Spinner) findViewById(R.id.always_sleepy));
        f_bA = ((Spinner) findViewById(R.id.fast_breathing));
        c_dA = ((Spinner) findViewById(R.id.chest_drawing));
        l_wA = ((Spinner) findViewById(R.id.loose_weight));
        y_sA = ((Spinner) findViewById(R.id.yellow_soles));
        s_fd = ((Spinner) findViewById(R.id.start_feeding));
        f_bs = ((Spinner) findViewById(R.id.fed_birth));
        c_fs = ((Spinner) findViewById(R.id.child_fed));

        f_sB = ((Spinner) findViewById(R.id.feet_blue_2));
        f_sC = ((Spinner) findViewById(R.id.fever_chills));
        y_sS = ((Spinner) findViewById(R.id.yellow_skin_2));
        b_sD2 = ((Spinner) findViewById(R.id.breathing_difficulties));
        a_sA = ((Spinner) findViewById(R.id.abdominal));
        n_s2 = ((Spinner) findViewById(R.id.not_sucking_2));
        c_sC = ((Spinner) findViewById(R.id.constipation));
        u_sR = ((Spinner) findViewById(R.id.urinate));
        c_sD = ((Spinner) findViewById(R.id.cord_discharge));

        panelSectionOne = (ExpandablePanel) findViewById(R.id.panelSectionOne);
        panelSectionTwo = (ExpandablePanel) findViewById(R.id.panelSectionTwo);
        panelBreastfeedingHistory = (ExpandablePanel) findViewById(R.id.panelBreastfeedingHistory);
        panelSectionThree = (ExpandablePanel) findViewById(R.id.panelSectionThree);
        panelSectionOne.setOnExpandListener(this);
        panelSectionTwo.setOnExpandListener(this);
        panelBreastfeedingHistory.setOnExpandListener(this);
        panelSectionThree.setOnExpandListener(this);

        motherGroup = (RadioGroup) findViewById(R.id.atBirthOrSevenDays);

        if(age > 7 || val == 1){

            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getResources().getString(R.string.app_name));
            actionBar.setSubtitle("MOH 514 DSS");
            actionBar.setIcon(R.mipmap.ic_launcher);
            actionBar.setHomeButtonEnabled(false);

            motherTypeValue = "post-partum";
            ln.setVisibility(View.GONE);
            ln_a.setVisibility(View.VISIBLE);
            ln_btn.setVisibility(View.GONE);
        }else if((age < 7 && age != 0) || val == 2){

            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(getResources().getString(R.string.app_name));
            actionBar.setSubtitle("MOH 514 DSS");
            actionBar.setIcon(R.mipmap.ic_launcher);
            actionBar.setHomeButtonEnabled(false);

            motherTypeValue = "pregnant";
            ln_a.setVisibility(View.GONE);
            ln.setVisibility(View.VISIBLE);
            ln_btn.setVisibility(View.GONE);
        }else{
            motherTypeValue = "";
            my514 = "514";
        }

        motherGroup.setOnCheckedChangeListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    public void showDescription(View v){
        //Toast.makeText(getApplicationContext(), v.getContentDescription().toString(), Toast.LENGTH_SHORT).show();
        new SweetAlertDialog(DangerSignsBaby.this,SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Details")
                .setContentText(v.getContentDescription().toString())
                .show();
    }

    public void save(final Context context, final JSONObject details, String where){
        final ProgressDialog pDialog = new ProgressDialog(DangerSignsBaby.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        MUtil.showProgressDialog(pDialog);
        String url = "";
        url = MApplication.url + where;

        Log.i(MApplication.TAG, MainActivity.class.getName() + " : Details " + details.toString());
        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        if(doLog) Log.i(TAG, CTAG + response.toString());
                        Log.i(TAG, LoginActivity.class.getName() + "Response : " +response.toString() + "\nSuccess : " + success.isSuccess() + "\nMessage : " + success.getMessage());
                        MUtil.hideProgressDialog(pDialog);

                        if(success.isSuccess()){
                            Crouton.makeText(DangerSignsBaby.this , success.getMessage() , Style.CONFIRM).show();

                            if(toDss){

                                Intent intent = new Intent(getApplicationContext(), Form100Activity.class);
                                intent.putExtra("mobile", mobile100);
                                intent.putExtra("mobile514", my514);
                                intent.putExtra("pos", children); //mothers number
                                intent.putExtra("trace", String.valueOf(survey));
                                intent.putExtra("index", index); //dss option
                                startActivity(intent);
                                finish();

                            }else {

                                if(infant != 0){
                                    Toast.makeText(getApplicationContext(), "No need to refer this patient", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), HouseholdMembersActivity.class);
                                    startActivity(intent);
                                }else{
                                    eventSelector();
                                }

                            }

                        }else{
                            Crouton.makeText(DangerSignsBaby.this , success.getMessage() , Style.ALERT).show();
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Crouton.makeText(DangerSignsBaby.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "dss");
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(DangerSignsBaby.this);
        builder.setCancelable(false);
        builder.setTitle("Are you sure you want to quit?");
        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DangerSignsBaby.this.finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void emptyFieldCheck() throws JSONException {
        String to = "survey_category";
        boolean okay = true;

        JSONObject jsonObject = new JSONObject();
        JSONObject jSurvey = new JSONObject();
        JSONObject categoryInfo = new JSONObject();

            if (motherTypeValue.equals("pregnant")) {

                index = 21;

                long tre =  n_bA.getSelectedItemId() + y_kA.getSelectedItemId() + b_hA.getSelectedItemId() + b_vA.getSelectedItemId() +
                        t_dA.getSelectedItemId() + s_hA.getSelectedItemId() + f_bA.getSelectedItemId() +
                        c_dA.getSelectedItemId() + l_wA.getSelectedItemId() + y_sA.getSelectedItemId();

                if(tre >= 20){
                    toDss = false;
                    okay = false;
                }

                jsonObject.put("34", n_bA.getSelectedItemId());
                jsonObject.put("35", y_kA.getSelectedItemId());
                jsonObject.put("36", b_hA.getSelectedItemId());
                jsonObject.put("37", b_vA.getSelectedItemId());
                jsonObject.put("38", t_dA.getSelectedItemId());
                jsonObject.put("39", s_hA.getSelectedItemId());
                jsonObject.put("40", f_bA.getSelectedItemId());
                jsonObject.put("41", c_dA.getSelectedItemId());
                jsonObject.put("42", l_wA.getSelectedItemId());
                jsonObject.put("43", y_sA.getSelectedItemId());
                jsonObject.put("59", s_fd.getSelectedItemId());
                jsonObject.put("60", f_bs.getSelectedItemId());
                jsonObject.put("61", c_fs.getSelectedItemId());

                jsonObject.put("select", index);

                categoryInfo.put("survey_status", survey_status);
                categoryInfo.put("type", 7);
                categoryInfo.put("number", children);
                categoryInfo.put("survey_id", survey);
                jSurvey.put("jsonObject", jsonObject);
                jSurvey.put("categoryInfo", categoryInfo);

                if(((Spinner) findViewById(R.id.not_breathing)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.not_breathing); return;
                }
                if(((Spinner) findViewById(R.id.yellow_skin)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.not_breathing); return;
                }
                if(((Spinner) findViewById(R.id.feet_blue)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.feet_blue); return;
                }
                if(((Spinner) findViewById(R.id.tiredness)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.tiredness); return;
                }
                if(((Spinner) findViewById(R.id.always_sleepy)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.always_sleepy); return;
                }
                if(((Spinner) findViewById(R.id.fast_breathing)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.fast_breathing); return;
                }
                if(((Spinner) findViewById(R.id.chest_drawing)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.chest_drawing); return;
                }
                if(((Spinner) findViewById(R.id.loose_weight)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.loosing_weight); return;
                }
                if(((Spinner) findViewById(R.id.yellow_soles)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.yellow_soles); return;
                }
                if(((Spinner) findViewById(R.id.start_feeding)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.start_feeding); return;
                }
                if(((Spinner) findViewById(R.id.fed_birth)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.fed_birth); return;
                }
                if(((Spinner) findViewById(R.id.child_fed)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.child_fed); return;
                }

                save(getApplicationContext(), jSurvey, to);


            } else if(motherTypeValue.equals("post-partum")) {

                index = 22;

                long tre = f_sB.getSelectedItemId() + f_sC.getSelectedItemId() + y_sS.getSelectedItemId() + b_sD2.getSelectedItemId()
                        + a_sA.getSelectedItemId() + n_s2.getSelectedItemId() + c_sC.getSelectedItemId()
                        + u_sR.getSelectedItemId() + c_sD.getSelectedItemId();

                if(tre >= 18){
                    toDss = false;
                    okay = false;
                }

                jsonObject.put("44", f_sB.getSelectedItemId());
                jsonObject.put("45", f_sC.getSelectedItemId());
                jsonObject.put("46", y_sS.getSelectedItemId());
                jsonObject.put("47", b_sD2.getSelectedItemId());
                jsonObject.put("48", a_sA.getSelectedItemId());
                jsonObject.put("49", n_s2.getSelectedItemId());
                jsonObject.put("50", c_sC.getSelectedItemId());
                jsonObject.put("51", u_sR.getSelectedItemId());
                jsonObject.put("52", c_sD.getSelectedItemId());

                jsonObject.put("select", index);

                categoryInfo.put("survey_status", survey_status);
                categoryInfo.put("type", 7);
                categoryInfo.put("number", children);
                categoryInfo.put("survey_id", survey);
                jSurvey.put("jsonObject", jsonObject);
                jSurvey.put("categoryInfo", categoryInfo);

                if(((Spinner) findViewById(R.id.feet_blue_2)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.feet_blue); return;
                }
                if(((Spinner) findViewById(R.id.fever_chills)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.fever_chills); return;
                }
                if(((Spinner) findViewById(R.id.yellow_skin_2)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.yellow_skin); return;
                }
                if(((Spinner) findViewById(R.id.breathing_difficulties)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.Breathing); return;
                }
                if(((Spinner) findViewById(R.id.abdominal)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.convulsions); return;
                }
                if(n_s2.getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.not_suckingOrPoor); return;
                }
                if(((Spinner) findViewById(R.id.constipation)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.constipation); return;
                }
                if(((Spinner) findViewById(R.id.urinate)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.eye_discharge); return;
                }
                if(((Spinner) findViewById(R.id.cord_discharge)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.cord_discharge); return;
                }

                save(getApplicationContext(), jSurvey, to);

            }


    }

    public void showInvalidMessage(int message){
        new SweetAlertDialog(DangerSignsBaby.this,SweetAlertDialog.WARNING_TYPE)
                .setContentText("Select If " + getResources().getString(message)).show();
    }

    public  void dssToggle() throws JSONException {

        int villageId, age, worker_id, weight;
        String name;
        boolean okay = true;
        String householdMobile;
        String gender, guardian;
        double latitude;
        double longitude;
        String to = "dss";

        Bundle extra = getIntent().getExtras();
        JSONObject individual_details = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        JSONObject jSurvey = new JSONObject();

        if(extra != null){

            villageId = extra.getInt("village_id");
            name = extra.getString("name");
            age = extra.getInt("age");
            gender = extra.getString("gender");
            guardian = extra.getString("guardian");
            householdMobile = extra.getString("number");
            latitude = extra.getDouble("uLatitude");
            longitude = extra.getDouble("uLongitude");
            worker_id = extra.getInt("health_worker_id");
            weight = extra.getInt("txtBabyWeight");

            individual_details.put("village_id", villageId);
            individual_details.put("health_worker_id", worker_id);
            individual_details.put("age", age);
            individual_details.put("name", name);
            individual_details.put("guardian", guardian);
            individual_details.put("gender", gender);
            individual_details.put("mobile", householdMobile);
            individual_details.put("latitude", latitude);
            individual_details.put("longitude", longitude);
            individual_details.put("weight", weight);
            individual_details.put("org_id", 0);
            individual_details.put("age_type", "DAYS");

            mobile100 = householdMobile;

            if (motherTypeValue.equals("pregnant")) {

                index = 21;

                long tre =  n_bA.getSelectedItemId() + y_kA.getSelectedItemId() + b_hA.getSelectedItemId() + b_vA.getSelectedItemId() +
                        t_dA.getSelectedItemId() + s_hA.getSelectedItemId() + f_bA.getSelectedItemId() +
                        c_dA.getSelectedItemId() + l_wA.getSelectedItemId() + y_sA.getSelectedItemId();

                if(tre >= 20){
                    toDss = false;
                    okay = false;
                }

                jsonObject.put("34", n_bA.getSelectedItemId());
                jsonObject.put("35", y_kA.getSelectedItemId());
                jsonObject.put("36", b_hA.getSelectedItemId());
                jsonObject.put("37", b_vA.getSelectedItemId());
                jsonObject.put("38", t_dA.getSelectedItemId());
                jsonObject.put("39", s_hA.getSelectedItemId());
                jsonObject.put("40", f_bA.getSelectedItemId());
                jsonObject.put("41", c_dA.getSelectedItemId());
                jsonObject.put("42", l_wA.getSelectedItemId());
                jsonObject.put("43", y_sA.getSelectedItemId());
                jsonObject.put("59", s_fd.getSelectedItemId());
                jsonObject.put("60", f_bs.getSelectedItemId());
                jsonObject.put("61", c_fs.getSelectedItemId());

                individual_details.put("dsselection", index);

                jSurvey.put("individual_details", individual_details);
                jSurvey.put("danger_signs", jsonObject);


                if(((Spinner) findViewById(R.id.not_breathing)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.not_breathing); return;
                }
                if(((Spinner) findViewById(R.id.yellow_skin)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.not_breathing); return;
                }
                if(((Spinner) findViewById(R.id.feet_blue)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.feet_blue); return;
                }
                if(((Spinner) findViewById(R.id.not_sucking)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.not_sucking); return;
                }
                if(((Spinner) findViewById(R.id.tiredness)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.tiredness); return;
                }
                if(((Spinner) findViewById(R.id.always_sleepy)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.always_sleepy); return;
                }
                if(((Spinner) findViewById(R.id.fast_breathing)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.fast_breathing); return;
                }
                if(((Spinner) findViewById(R.id.chest_drawing)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.chest_drawing); return;
                }
                if(((Spinner) findViewById(R.id.loose_weight)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.loosing_weight); return;
                }
                if(((Spinner) findViewById(R.id.yellow_soles)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.yellow_soles); return;
                }
                if(((Spinner) findViewById(R.id.start_feeding)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.start_feeding); return;
                }
                if(((Spinner) findViewById(R.id.fed_birth)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.fed_birth); return;
                }
                if(((Spinner) findViewById(R.id.child_fed)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.child_fed); return;
                }

                save(getApplicationContext(), jSurvey, to);


            } else if(motherTypeValue.equals("post-partum")) {

                index = 22;

                long tre = f_sB.getSelectedItemId() + f_sC.getSelectedItemId() + y_sS.getSelectedItemId() + b_sD2.getSelectedItemId()
                        + a_sA.getSelectedItemId() + n_s2.getSelectedItemId() + c_sC.getSelectedItemId()
                        + u_sR.getSelectedItemId() + c_sD.getSelectedItemId();

                if(tre >= 18){
                    toDss = false;
                    okay = false;
                }

                jsonObject.put("44", f_sB.getSelectedItemId());
                jsonObject.put("45", f_sC.getSelectedItemId());
                jsonObject.put("46", y_sS.getSelectedItemId());
                jsonObject.put("47", b_sD2.getSelectedItemId());
                jsonObject.put("48", a_sA.getSelectedItemId());
                jsonObject.put("49", n_s2.getSelectedItemId());
                jsonObject.put("50", c_sC.getSelectedItemId());
                jsonObject.put("51", u_sR.getSelectedItemId());
                jsonObject.put("52", c_sD.getSelectedItemId());

                individual_details.put("dsselection", index);

                jSurvey.put("individual_details", individual_details);
                jSurvey.put("danger_signs", jsonObject);

                if(((Spinner) findViewById(R.id.feet_blue_2)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.feet_blue); return;
                }
                if(((Spinner) findViewById(R.id.fever_chills)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.fever_chills); return;
                }
                if(((Spinner) findViewById(R.id.yellow_skin_2)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.yellow_skin); return;
                }
                if(((Spinner) findViewById(R.id.breathing_difficulties)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.Breathing); return;
                }
                if(((Spinner) findViewById(R.id.abdominal)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.convulsions); return;
                }
                if(n_s2.getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.not_suckingOrPoor); return;
                }
                if(((Spinner) findViewById(R.id.constipation)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.constipation); return;
                }
                if(((Spinner) findViewById(R.id.urinate)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.eye_discharge); return;
                }
                if(((Spinner) findViewById(R.id.cord_discharge)).getSelectedItemPosition() == 0){
                    showInvalidMessage(R.string.cord_discharge); return;
                }

                save(getApplicationContext(), jSurvey, to);
            }
        }

    }

    public void eventSelector() {
        final CharSequence[] items = {
                "No need to refer this patient"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(DangerSignsBaby.this);
        builder.setCancelable(false);
        builder.setTitle("Referral");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void nextForm(View view){
        Bundle extra = getIntent().getExtras();

        if(extra != null){
            String refValue = extra.getString("pre");
            if(refValue == null) {
                try {
                    dssToggle();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    emptyFieldCheck();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch(checkedId) {
            case R.id.atBirth:
                motherTypeValue = "pregnant";
                ln_a.setVisibility(View.GONE);
                ln.setVisibility(View.VISIBLE);
                break;
            case R.id.sevenDays:
                motherTypeValue = "post-partum";
                ln.setVisibility(View.GONE);
                ln_a.setVisibility(View.VISIBLE);
                break;
        }
        if(motherTypeValue.equals("pregnant")){

        } else {

        }
    }
}
