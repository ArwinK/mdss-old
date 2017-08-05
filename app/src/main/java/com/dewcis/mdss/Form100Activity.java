package com.dewcis.mdss;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.dewcis.mdss.SharedPreferences.SharedPreferences514;
import com.dewcis.mdss.model.Area;
import com.dewcis.mdss.model.MSession;
import com.dewcis.mdss.model.Success;
import com.dewcis.mdss.model.Survey;
import com.dewcis.mdss.model.User;
import com.dewcis.mdss.utils.AboutDialog;
import com.dewcis.mdss.utils.MUtil;
import com.dewcis.mdss.volley.VolleyErrorHelper;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by henriquedn on 4/14/15.
 * henrydkm@gmail.com
 * henry.maina@dewcis.com
 */
public class Form100Activity extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    MSession session;
    String TAG = MApplication.TAG;
    static boolean doLog = MApplication.LOGDEBUG;
    static String CTAG = Form100Activity.class.getName() + " : " ;
    public static final String AREA_SUB_LOCATION = "getSublocations", AREA_VILLAGE = "getVillages", AREA_FACILITY = "getLinkFacilities", AREA_REFERRAL = "getRefFacilities";
    Integer subLocationId = null, linkFacilityId = null, villageId = null;

    String patientGender = "", patientAgeType = "", mobile, dss, refFacility;
    Button btnRefer;
    RadioGroup rgGender, rgAge;
    CheckBox immunize, toStartANC, followUpANC, postpartumFmPlanning, postpartumService,
            growthMonitoring, immunization, familyPlaning, growthMonitor, txtGeneralServices, growthMonitoringOther;
    MaterialEditText txtReferralFacility, txtformSerial, txtPatientID, txtPatientName, txtPatientAge
            , txtReferralReason, txtTreatment, txtComments;
    Spinner spnSublocation2, spnLinkFacility, spnReferralFacility;
    Spinner spnSublocation,spnVillage, spnCat;
    int survey_dss = 0, child_index = 0, index = 0, num = 0, category;
    String dssRem, trace = "0", pos = "", textSerial = "", patientAge = "", my514;
    SharedPreference sharedPreference;
    SharedPreferences514 sharedPrefs514;
    int survey_status = 0;
    Bundle extras;
    LinearLayout linearPg, linearPM, linearNB, linearOM;
    RadioButton rbY, rbM, rbD, rb, rbMale;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form100);

        session = new MSession(getApplicationContext());

        if(!session.isLoggedIn()){
            session.logout(this);
        }

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setTitle(getResources().getString(R.string.app_name));
//        actionBar.setSubtitle("Community Referral Form");
//        actionBar.setIcon(R.mipmap.ic_launcher);
//        actionBar.setHomeButtonEnabled(true);

        spnSublocation2 = (Spinner) findViewById(R.id.spnSublocation2);
        spnLinkFacility = (Spinner) findViewById(R.id.spnLinkFacility);
        spnReferralFacility = (Spinner) findViewById(R.id.spnReferralFacility);
        spnSublocation = (Spinner) findViewById(R.id.spnSublocation);
        spnVillage = (Spinner) findViewById(R.id.spnVillage);
        spnCat = (Spinner) findViewById(R.id.category);

        toStartANC = (CheckBox) findViewById(R.id.toStartANC);
        followUpANC = (CheckBox) findViewById(R.id.followUpANC);
        postpartumFmPlanning = (CheckBox) findViewById(R.id.postpartumFmPlanning);
        postpartumService = (CheckBox) findViewById(R.id.postpartumService);
        growthMonitoring = (CheckBox) findViewById(R.id.growthMonitoring);
        growthMonitoringOther = (CheckBox) findViewById(R.id.growthMonitoringOther);
        immunization = (CheckBox) findViewById(R.id.immunization);
        familyPlaning = (CheckBox) findViewById(R.id.familyPlaning);
        growthMonitor = (CheckBox) findViewById(R.id.growthMonitor);
        immunize = (CheckBox) findViewById(R.id.immunize);

        linearPg = (LinearLayout) findViewById(R.id.pregnant);
        linearPM = (LinearLayout) findViewById(R.id.postpartum);
        linearNB = (LinearLayout) findViewById(R.id.newborn);
        linearOM = (LinearLayout) findViewById(R.id.other_members);

        spnCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


                switch (i){
                    case 0:
                        break;
                    case 1:
                        showAllLayouts();
                        linearPM.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        showAllLayouts();
                        linearPg.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        showAllLayouts();
                        linearOM.setVisibility(View.VISIBLE);
                        familyPlaning.setVisibility(View.GONE);
                        immunize.setVisibility(View.VISIBLE);
                        growthMonitoringOther.setText("Growth monitoring");
                        break;
                    case 4:
                        showAllLayouts();
                        linearNB.setVisibility(View.VISIBLE);
                        break;
                    case 5:
                        showAllLayouts();
                        immunize.setVisibility(View.GONE);
                        linearOM.setVisibility(View.VISIBLE);
                        break;
                    case 6:
                        showAllLayouts();
                        immunize.setVisibility(View.GONE);
                        linearOM.setVisibility(View.VISIBLE);
                        break;
                }

                category = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        facilitySpinner();

        txtformSerial = (MaterialEditText) findViewById(R.id.txtformSerial);
        txtPatientID = (MaterialEditText) findViewById(R.id.txtPatientID);
        txtPatientName = (MaterialEditText) findViewById(R.id.txtPatientName);
        txtPatientAge = (MaterialEditText) findViewById(R.id.txtPatientAge);
        txtTreatment = (MaterialEditText) findViewById(R.id.txtTreatment);
        txtComments = (MaterialEditText) findViewById(R.id.txtComments);
        txtReferralFacility = (MaterialEditText) findViewById(R.id.txtReferralFacility);
        txtGeneralServices = (CheckBox) findViewById(R.id.general_services);

        txtformSerial.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtPatientName.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtReferralFacility.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        rgGender = (RadioGroup) findViewById(R.id.rgGender);
        rgAge = (RadioGroup) findViewById(R.id.rgAge);
        rbD = (RadioButton) findViewById(R.id.radio_days);
        rbM = (RadioButton) findViewById(R.id.radio_months);
        rbY = (RadioButton) findViewById(R.id.radio_years);
        rbMale = (RadioButton) findViewById(R.id.radio_male);

        btnRefer = (Button) findViewById(R.id.btnRefer);
        btnRefer.setOnClickListener(this);

        extras = getIntent().getExtras();
        if(extras != null){
            my514 = extras.getString("mobile514");
            if(my514 != null)
                preceding514Data();
            else precedingDssData();
        }

        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.radio_male:
                        patientGender = "M";
                        break;
                    case R.id.radio_female:
                        patientGender = "F";
                        break;
                }
            }
        });

        rgAge.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.radio_days:
                        patientAgeType = "D";
                        break;
                    case R.id.radio_months:
                        patientAgeType = "M";
                        break;
                    case R.id.radio_years:
                        patientAgeType = "Y";
                        break;
                }
            }
        });

    }

    private void showAllLayouts() {
        linearPM.setVisibility(View.GONE);
        linearNB.setVisibility(View.GONE);
        linearPg.setVisibility(View.GONE);
        linearOM.setVisibility(View.GONE);
    }

    private void facilitySpinner() {

        List<String> list;

        list = new ArrayList<String> ();
        list.add("Select Referral Facility");
        list.add("EASTLEIGH HEALTH CENTRE");
        list.add("OTHERS");
        list.add("PUMWANI DISPENSARY");
        list.add("BIAFRA LIONS CLINIC");
        list.add("BAHATI HEALTH CENTRE");
        list.add("IOM CENTRE");

        ArrayAdapter<String> adp = new ArrayAdapter<String>
                (getApplicationContext(),android.R.layout.simple_list_item_1, list);
        spnReferralFacility.setAdapter(adp);
        spnReferralFacility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) view).setTextColor(Color.BLACK);
                if(position == 0){
                    refFacility = null;
                }else {
                    refFacility = spnReferralFacility.getSelectedItem().toString();
                }

                if(position == 2) {
                    txtReferralFacility.setVisibility(View.VISIBLE);
                } else {
                    txtReferralFacility.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void preceding514Data() {

        Bundle extras = getIntent().getExtras();
        String fm514;

        SharedPreferences514 shared514 = new SharedPreferences514();
        String name = shared514.getValueStrings(getApplicationContext(), "name");

        txtPatientName.setText(name);

        if(extras != null){
            fm514 = extras.getString("mobile514");
            if(fm514 != null) {

                index = extras.getInt("index");
                pos = extras.getString("trace");
                num = extras.getInt("pos");

                if(pos != null){
                    survey_dss = Integer.parseInt(pos);
                }

                if(index != 0){

                    if(index == 21) {
                        textSerial = "DSSXAB" + pos + "N" + num;
                        rbD.setVisibility(View.VISIBLE);
                        rbM.setVisibility(View.GONE);
                        rbY.setVisibility(View.GONE);
                        rgGender.clearCheck();
                    }else if(index == 22) {
                        textSerial = "DSSXOC" + pos + "N" + num;
                        rbD.setVisibility(View.VISIBLE);
                        rbM.setVisibility(View.GONE);
                        rbY.setVisibility(View.GONE);
                        rgGender.clearCheck();
                    }else if(index == 11){
                        patientAgeType = "Y";
                        patientGender = "F";
                        rbMale.setVisibility(View.GONE);
                        rbD.setVisibility(View.GONE);
                        rbM.setVisibility(View.GONE);
                        textSerial = "DSSXPM" + pos + "N" + num;
                    }else if(index == 12){
                        patientAgeType = "Y";
                        patientGender = "F";
                        rbMale.setVisibility(View.GONE);
                        rbD.setVisibility(View.GONE);
                        rbM.setVisibility(View.GONE);
                        textSerial = "DSSXPG" + pos + "N" + num;
                    }

                    txtformSerial.setFocusable(false);
                    txtPatientName.setFocusable(false);

                    txtformSerial.setText(textSerial);
                }
            }

        }

        sharedPrefs514 = new SharedPreferences514();

        villageId = sharedPrefs514.getValue(getApplicationContext(), "village");

        if(villageId != 0){
            LinearLayout locs = (LinearLayout) findViewById(R.id.locations);
            locs.setVisibility(View.GONE);

            Toast.makeText(getApplicationContext(), "Village Auto loaded", Toast.LENGTH_SHORT).show();
        }


    }

    private void precedingDssData() {

        SharedPreferences sharedpreferences = getSharedPreferences(DssActivity.DSS_PREFERENCES, Context.MODE_PRIVATE);
        int village = sharedpreferences.getInt("village_id", 0);
        int age = sharedpreferences.getInt("age", 0);

        String gender  = sharedpreferences.getString("gender", null);
        String mobile_dss  = sharedpreferences.getString("mobile", null);

        villageId = village;
        patientAge = String.valueOf(age);
        patientGender = gender;
        mobile = mobile_dss;
        dss = "home";

            if(villageId != 0){
                LinearLayout locs = (LinearLayout) findViewById(R.id.locations);
                locs.setVisibility(View.GONE);

                Toast.makeText(getApplicationContext(), "Village Auto loaded", Toast.LENGTH_SHORT).show();
            }
        extras = getIntent().getExtras();

        if(extras != null) {
            index = extras.getInt("index");
            if(index != 0){

                if(index == 21) {
                    textSerial = "DSSXAB" + pos + "N" + num;
                    spnCat.setSelection(4);
                    rbM.setVisibility(View.GONE);
                    rbY.setVisibility(View.GONE);
                }else if(index == 22) {
                    textSerial = "DSSXOC" + pos + "N" + num;
                    spnCat.setSelection(4);
                    rbD.setVisibility(View.GONE);
                }else if(index == 11){
                    rbMale.setVisibility(View.GONE);
                    rbD.setVisibility(View.GONE);
                    rbM.setVisibility(View.GONE);
                    spnCat.setSelection(1);
                }else if(index == 12){
                    rbMale.setVisibility(View.GONE);
                    rbD.setVisibility(View.GONE);
                    rbM.setVisibility(View.GONE);
                    spnCat.setSelection(2);
                }
                txtformSerial.setFocusable(false);
                txtformSerial.setText(textSerial);

            }
        }

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("age");
        editor.remove("mobile");
        editor.apply();

    }


    @Override
    protected void onResume() {
        super.onResume();
        getUniqueKey(getApplicationContext(), mobile);
        if(subLocationId == null){
            getArea(getApplicationContext(), new JSONObject(), AREA_SUB_LOCATION);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_form100, menu);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRefer:
                validate();
            break;
        }
    }

    void validate(){

        boolean ok = true;
        String formSerial, patientName, referralReason, treatment, comments,
                communityUnit, referralUnit = null;

        formSerial = txtformSerial.getText().toString();
        dssRem = formSerial;
        patientName = txtPatientName.getText().toString();
        patientAge = txtPatientAge.getText().toString();
        referralReason = populateReasons();
        treatment = txtTreatment.getText().toString();
        comments = txtComments.getText().toString();
        if(refFacility != null){
            if(refFacility.equals("OTHERS")){
                txtReferralFacility.setVisibility(View.VISIBLE);
                referralUnit = txtReferralFacility.getText().toString();
                if(referralUnit.equals("")){
                    Toast.makeText(Form100Activity.this, "Enter a referral Unit", Toast.LENGTH_LONG).show();
                    spnVillage.requestFocus(); ok = false; return;
                }
            }else {
                referralUnit = refFacility;
            }

        } else {
            Toast.makeText(Form100Activity.this, "Select Referral Health Facility", Toast.LENGTH_LONG).show();
            spnLinkFacility.requestFocus(); ok = false; return;
        }

        if(TextUtils.isEmpty(patientName)){
            Toast.makeText(getApplicationContext(), "Select Patient Name", Toast.LENGTH_LONG).show();
            txtPatientName.setError("Required"); txtPatientName.requestFocus(); ok = false; return;
        }

        if(TextUtils.isEmpty(patientGender) || patientGender.equals("")){
            Toast.makeText(getApplicationContext(), "Select Sex", Toast.LENGTH_LONG).show();
            ok = false; return;
        }
        if(TextUtils.isEmpty(patientAgeType) || patientAgeType.equals("")){
            Toast.makeText(getApplicationContext(), "Select Age Type", Toast.LENGTH_LONG).show();
            ok = false; return;
        }

        if(TextUtils.isEmpty(patientAge)){
            Toast.makeText(getApplicationContext(), "Select Patient Age", Toast.LENGTH_LONG).show();
            txtPatientAge.setError("Required"); txtPatientAge.requestFocus(); ok = false; return;
        }
        if(linkFacilityId == null){
            Toast.makeText(Form100Activity.this, "Select Link Health Facility", Toast.LENGTH_LONG).show();
            spnLinkFacility.requestFocus(); ok = false; return;
        }

        if(TextUtils.isEmpty(referralReason)){
            Toast.makeText(getApplicationContext(), "Select Referral reason", Toast.LENGTH_LONG).show();
            txtReferralReason.setError("Required"); txtReferralReason.requestFocus(); ok = false; return;
        }
        if(TextUtils.isEmpty(treatment)){
            txtTreatment.setError("Required"); txtTreatment.requestFocus(); ok = false; return;
        }

        if(villageId == null || villageId < 0){
            Toast.makeText(Form100Activity.this, "Select Community Unit", Toast.LENGTH_LONG).show();
            spnVillage.requestFocus(); ok = false; return;
        }
        if(category == 0){
            Toast.makeText(Form100Activity.this, "Select Patients status", Toast.LENGTH_LONG).show();
            spnCat.requestFocus(); ok = false; return;
        }

        SharedPreferences sharedpreferences = getSharedPreferences(HouseHoldActivity.HOUSEHOLD_INFORMATION, Context.MODE_PRIVATE);
        survey_status = sharedpreferences.getInt("survey_status", 0);

        if(ok){

            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(User.ID , session.getUser().getUserId());
                jsonObject.put(User.ORG_ID , session.getUser().getOrgId());
                jsonObject.put("category", category);
                jsonObject.put("formSerial", formSerial);
                jsonObject.put("patientName", patientName);
                jsonObject.put("patientGender", patientGender);
                jsonObject.put("patientAge", patientAge);
                jsonObject.put("patientAgeType", patientAgeType);
                jsonObject.put("linkHealthFacility", linkFacilityId);
                jsonObject.put("referralReason", referralReason);
                jsonObject.put("treatment", treatment);
                jsonObject.put("comments", comments);
                jsonObject.put("referral_facility", referralUnit);
                jsonObject.put("village", villageId);
                jsonObject.put("survey_dss", survey_dss);
                jsonObject.put("child_index", num);
                jsonObject.put("receivingOfficerName", "");
                jsonObject.put("receivingOfficerProfession", "");
                jsonObject.put("healthFacilityName", "");
                jsonObject.put("cycle_status", 0);
                jsonObject.put("actionTaken", "");
                jsonObject.put("survey_status", survey_status);

                save(getApplicationContext(), jsonObject);

            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    private String populateReasons() {

        List<String> reasons = new ArrayList<String>();

        if(toStartANC.isChecked() ){
            reasons.add("To start ANC");
        }
        if(followUpANC.isChecked()){
            reasons.add("Followup ANC");
        }
        if(postpartumFmPlanning.isChecked()){
            reasons.add("Postpartum family services");
        }
        if(postpartumFmPlanning.isChecked()){
            reasons.add("Postpartum services");
        }
        if(growthMonitoring.isChecked()){
            reasons.add("Growth monitoring for low birth weight");
        }
        if(immunization.isChecked()){
            reasons.add("Immunization");
        }
        if(familyPlaning.isChecked()){
            reasons.add("Family planning");
        }
        if(growthMonitor.isChecked()){
            reasons.add("Growth monitoring");
        }
        if(!txtGeneralServices.getText().toString().equals("")){
            reasons.add(txtGeneralServices.getText().toString());
        }

        StringBuilder stringBuilder = new StringBuilder();

        for(String s : reasons){
            if(!s.equals("")){
                stringBuilder.append(s);
                stringBuilder.append(",");
            }
        }

        return stringBuilder.toString();

    }

    public void getUniqueKey(final Context context, String uniqueKey){
        final ProgressDialog pDialog = new ProgressDialog(Form100Activity.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);
        MUtil.showProgressDialog(pDialog);
        String url = MApplication.url + "getRegID";

        JSONObject details = new JSONObject();
        try {
            details.put("registration_no", uniqueKey);
            details.put("class_name", dss);
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
                            Crouton.makeText(Form100Activity.this , success.getMessage() , Style.CONFIRM).show();

                            String name = "", uniqueId = "", surveyId = "", gender = "", patient_age = "";

                            try {

                              JSONArray jsonArray = response.getJSONArray("surveys");

                              JSONObject jsonObject = jsonArray.getJSONObject(0).getJSONObject("basicInfo");

                              if(dss.equals("home")){
                                  name = jsonObject.getString("name");
                                  uniqueId = jsonObject.getString("u_sid");
                                  surveyId = jsonObject.getString("survey_id");
                                  gender = jsonObject.getString("gender");
                                  patient_age = jsonObject.getString("age");


                                  if(uniqueId.contains("DSSPG") || uniqueId.contains("DSSPM") || uniqueId.contains("DSSXPG") || uniqueId.contains("DSSXPM")){
                                      rbMale.setVisibility(View.GONE);
                                      rbD.setVisibility(View.GONE);
                                      rbM.setVisibility(View.GONE);
                                      rgAge.check(R.id.radio_years);
                                      rgGender.check(R.id.radio_female);
                                  }else if(uniqueId.contains("DSSAB") || uniqueId.contains("DSSXAB")){
                                      rbM.setVisibility(View.GONE);
                                      rbY.setVisibility(View.GONE);
                                      rgAge.check(R.id.radio_days);
                                  }else if(uniqueId.contains("DSSOC") || uniqueId.contains("DSSXOC")){
                                      rbD.setVisibility(View.GONE);
                                      rgAge.check(R.id.radio_months);
                                  }

                                  switch (gender){

                                      case "Female":
                                          rgGender.check(R.id.radio_female);
                                          break;
                                      case "Male":
                                          rgGender.check(R.id.radio_male);
                                          break;
                                  }

                                  txtPatientAge.setText(patient_age);
                                  txtPatientID.setText(surveyId);
                                  txtformSerial.setText(uniqueId);
                                  txtPatientName.setText(name);
                                  txtPatientAge.setFocusable(false);
                                  txtformSerial.setFocusable(false);
                                  txtPatientName.setFocusable(false);

                              } else {

                                  name = jsonObject.getString("name");
                                  uniqueId = jsonObject.getString("u_sid");
                                  surveyId = jsonObject.getString("survey_id");

                                  if(uniqueId.contains("DSSXPG") || uniqueId.contains("DSSXPM")){
                                      rgAge.check(R.id.radio_years);
                                      rgGender.check(R.id.radio_female);
                                  }else if(uniqueId.contains("DSSAB") || uniqueId.contains("DSSXAB")){
                                      rgAge.check(R.id.radio_days);
                                  }else if(uniqueId.contains("DSSOC") || uniqueId.contains("DSSXOC")){
                                      rgAge.check(R.id.radio_days);
                                  }

                                  txtPatientID.setText(surveyId);
                                  txtformSerial.setText(uniqueId);
                                  txtPatientName.setText(name);
                                  txtformSerial.setFocusable(false);
                                  txtPatientName.setFocusable(false);
                              }

                              if(surveyId.equals("")) {
                                  survey_dss = 0;
                              }
                              if(survey_dss == 0 && txtPatientID.getText() != null){
                                  survey_dss = Integer.valueOf(txtPatientID.getText().toString());
                              }

                            } catch (Exception e) {
                                Log.e(TAG, CTAG + "Error Retrieving Json Array : " + e.toString());
                            }
                            //MainActivity.this.finish();
                        }else{

                            Toast.makeText(getApplicationContext(), "Enter an existing registration ID" , Toast.LENGTH_SHORT).show();

                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        //Crouton.makeText(Form100Activity.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "getRegID");
    }

    public void save(final Context context, final JSONObject details) {
        final ProgressDialog pDialog = new ProgressDialog(Form100Activity.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        MUtil.showProgressDialog(pDialog);
        String url = MApplication.url + "form100";

        Log.i(MApplication.TAG, MainActivity.class.getName() + " : Details " + details.toString());
        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        if(doLog) Log.i(TAG, CTAG + response.toString());
                        if(doLog) Log.i(TAG, LoginActivity.class.getName() + "Response : " +response.toString() + "\nSuccess : " + success.isSuccess() + "\nMessage : " + success.getMessage());
                        MUtil.hideProgressDialog(pDialog);
                        if(success.isSuccess()){
                            Crouton.makeText(Form100Activity.this, success.getMessage(), Style.CONFIRM).show();
                            Toast.makeText(context, "Survey Submitted Successfully", Toast.LENGTH_LONG).show();
                            clearEditTexts(new MaterialEditText[]
                                    {
                                            txtformSerial,txtPatientName, txtPatientAge, txtPatientID,
                                            txtTreatment, txtComments, txtReferralFacility
                                    }); //txtReceivingOfficerName, txtReceivingOfficerProfession,txtHealthFacilityName, txtActionTaken,txtLinkHealthFacility, txtSublocation, txtVillage

                            setSpinnerSelectedIndex(0, new Spinner[]{spnSublocation,spnVillage, spnSublocation2, spnLinkFacility, spnCat});
                            rgGender.clearCheck();
                            rgAge.clearCheck();
                            //radioReferral.setChecked(false);

                            sharedPreference = new SharedPreference();

                            int postpartum = sharedPreference.getValue(getApplicationContext(), "postpartum");
                            int pregnant = sharedPreference.getValue(getApplicationContext(), "pregnant");
                            int children = sharedPreference.getValue(getApplicationContext(), "children");
                            int infant = sharedPreference.getValue(getApplicationContext(), "infant");
                            int other_mothers = sharedPreference.getValue(getApplicationContext(), "other_mothers");
                            int other_members = sharedPreference.getValue(getApplicationContext(), "other_mothers");

                            int total = postpartum + pregnant + children + infant + other_members + other_mothers;

                            Intent intent;
                            if(total != 0 && my514 != null){
                                intent = new Intent(getApplicationContext(), HouseholdMembersActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                intent = new Intent(getApplicationContext(), Home.class);
                                startActivity(intent);
                                finish();
                            }

                        }else{
                            Crouton.makeText(Form100Activity.this , success.getMessage() , Style.ALERT).show();
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Crouton.makeText(Form100Activity.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "form100");
    }

    public void clearEditTexts(MaterialEditText[] editTexts){
        for(MaterialEditText m : editTexts){
            m.setText("");
        }
    }

    public void showDescription(View v) {
        //Toast.makeText(getApplicationContext(), v.getContentDescription().toString(), Toast.LENGTH_SHORT).show();
        new SweetAlertDialog(Form100Activity.this,SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Details")
                .setContentText(v.getContentDescription().toString())
                .show();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Form100Activity.this);
        builder.setCancelable(false);
        builder.setTitle("Are you sure you want to quit?");
        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Form100Activity.this.finish();

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }


    public void setSpinnerSelectedIndex(int index, Spinner[] spinners){
        for(Spinner s : spinners){
            s.setSelection(index);
        }
    }

    public void getArea(final Context context, final JSONObject details, final String whichArea){
        final ProgressDialog pDialog = new ProgressDialog(Form100Activity.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);
        final Activity activity = Form100Activity.this;
        MUtil.showProgressDialog(pDialog);
        String url = MApplication.url + whichArea;

        if(doLog) Log.i(MApplication.TAG, MainActivity.class.getName() + " : Details " + details.toString());
        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        if(doLog) Log.i(TAG, CTAG + response.toString());
                        Log.i(TAG, LoginActivity.class.getName() + "Response : " +response.toString() + "\nSuccess : " + success.isSuccess() + "\nMessage : " + success.getMessage());
                        MUtil.hideProgressDialog(pDialog);

                        if(success.isSuccess()){
                            Crouton.makeText(Form100Activity.this , success.getMessage() , Style.CONFIRM).show();
                            try {
                                JSONArray jAreas = response.getJSONArray("areas");

                                if(whichArea.equals(AREA_SUB_LOCATION)){

                                    final ArrayList<Area> areasSubLocation = Area.makeArrayList(response.getJSONArray("areas"));
                                    spnSublocation.setAdapter(Area.getArrayAdapter(activity, areasSubLocation));
                                    spnSublocation2.setAdapter(Area.getArrayAdapter(activity, areasSubLocation));

                                    spnSublocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            if(position == 0){
                                                villageId = null;
                                            }else if(position > 0 && villageId == null){
                                                subLocationId = areasSubLocation.get(position).getId();
                                                getArea(context, MUtil.simpleJSOnMaker(Survey.SUB_LOCATION_ID, subLocationId), AREA_VILLAGE);
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) { }
                                    });
                                    spnSublocation2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            if(position == 0){
                                                linkFacilityId = null;
                                            }else{
                                                subLocationId = areasSubLocation.get(position).getId();
                                                getArea(context, MUtil.simpleJSOnMaker(Survey.SUB_LOCATION_ID, subLocationId), AREA_REFERRAL);
                                                getArea(context, MUtil.simpleJSOnMaker(Survey.SUB_LOCATION_ID, subLocationId), AREA_FACILITY);
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) { }
                                    });
                                }else if(whichArea.equals(AREA_VILLAGE)){
                                    final ArrayList<Area> areasVillages = Area.makeArrayList(response.getJSONArray("areas"));
                                    spnVillage.setAdapter(Area.getArrayAdapter(activity, areasVillages));
                                    spnVillage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            if(position == 0){
                                                villageId = null;
                                            }else {
                                                villageId = areasVillages.get(position).getId();
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) { }
                                    });
                                }else if(whichArea.equals(AREA_FACILITY)){
                                    final ArrayList<Area> areasFacilities = Area.makeArrayList(response.getJSONArray("areas"));
                                    spnLinkFacility.setAdapter(Area.getArrayAdapter(activity, areasFacilities));
                                    spnLinkFacility.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            if(position == 0){
                                                linkFacilityId = null;
                                            }else {
                                                linkFacilityId = areasFacilities.get(position).getId();
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) { }
                                    });

                                } else if(whichArea.equals(AREA_REFERRAL)){
//                                    final ArrayList<Area> areasReferral = Area.makeArrayList(response.getJSONArray("areas"));

                                }

                            }catch (JSONException je) {
                                Log.e(TAG, CTAG + "Error Retrieving Json Array : " + je.toString());
                            }catch (Exception e) {
                                Log.e(TAG, CTAG + "Error Retrieving Json Array : " + e.toString());
                            }

                        }else{
                            Crouton.makeText(Form100Activity.this , success.getMessage() , Style.ALERT).show();
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Crouton.makeText(Form100Activity.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "getArea");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        category = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
