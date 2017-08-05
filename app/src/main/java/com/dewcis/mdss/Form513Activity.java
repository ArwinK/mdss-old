package com.dewcis.mdss;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
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
import com.dewcis.mdss.model.Area;
import com.dewcis.mdss.model.MSession;
import com.dewcis.mdss.model.Success;
import com.dewcis.mdss.model.Survey;
import com.dewcis.mdss.utils.ExpandablePanel;
import com.dewcis.mdss.utils.MUtil;
import com.dewcis.mdss.volley.VolleyErrorHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class Form513Activity extends ActionBarActivity implements RadioGroup.OnCheckedChangeListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ExpandablePanel.OnExpandListener {

    String TAG = MApplication.TAG;
    static String CTAG = Form513Activity.class.getName() + " : " ;
    static boolean doLog = MApplication.LOGDEBUG;
    public static final String AREA_SUB_LOCATION = "getSublocations", AREA_VILLAGE = "getVillages";
    Integer subLocationId = null;
    Integer villageId = null;
    TextView lblDisplayUser;

    MSession session;
    public static Context context;
    private Calendar calendar;
    private int year, month, day;
    double uLatitude,uLongitude;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    boolean okay = true;
    int num = 1;
    boolean proceed = false;
    int section = 1, survey_id = 0, members = 0, members_death = 0;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;


    MaterialEditText member_name, individual_code, txtPatientMobile, age, date_death, individualCodeDt,
            nameOfMemberDt, age_dt, commentsDeath, txtNoOfMembers,txtHouseholdNum, txtNoDeaths;
    Spinner spnSf,spnTw ,spnWf,spnD ,spnL,spnLm,spnSo,spnSc,spnSs,spnSp,spnSmH
            ,spnSa,spnSb,spnSe,spnSup,spnPg,spnPg3 ,spnSmg ,spnFi,spnVg ,spnTg,spnSlM,spnMlM ,spnLli,spnSi,spnSgh,spnSHi, spnDly, spnCohort;

    RadioGroup gender, ageValue, genderDeath;
    String genderType = "", number;
    String ageTypeValue = "", memberNo, code;
    Spinner spnSublocation, spnVillage;
    ExpandablePanel panelBackground, panelHousehold, panelIndividual, panelDeath, panelhousehold_indicators;
    LinearLayout lnDeaths, secOne, secTwo, secThree, secFour, secFive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form513);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));
        actionBar.setSubtitle("MOH 513");
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setHomeButtonEnabled(true);

        session = new MSession(getApplicationContext());

        if(!session.isLoggedIn()){
            session.logout(this);
        }

        householdHasDeaths();

        lblDisplayUser = (TextView) findViewById(R.id.lblDisplayUser);

        lblDisplayUser.setText("Perform New Report");

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        txtNoDeaths = (MaterialEditText) findViewById(R.id.txtNoDeaths);
        date_death = (MaterialEditText) findViewById(R.id.date_death);

        //Layouts
        secOne = (LinearLayout) findViewById(R.id.secOne);
        secTwo = (LinearLayout) findViewById(R.id.secTwo);
        secThree = (LinearLayout) findViewById(R.id.secThree);
        secFour = (LinearLayout) findViewById(R.id.secFour);
        secFive = (LinearLayout) findViewById(R.id.death513);

        //Background Household Information
        individual_code = (MaterialEditText) findViewById(R.id.etFieldCode);
        txtPatientMobile = (MaterialEditText) findViewById(R.id.txtPatientMobile);
        txtNoOfMembers = (MaterialEditText) findViewById(R.id.txtNoOfMembers);
        txtHouseholdNum = (MaterialEditText) findViewById(R.id.txtHouseholdNum);

        spnSublocation  = (Spinner) findViewById(R.id.spnSubLoc);
        spnVillage  = (Spinner) findViewById(R.id.spnVillage);

        //Individual Details
        age = (MaterialEditText) findViewById(R.id.txtPatientAge);
        member_name = (MaterialEditText) findViewById(R.id.etFieldMember);
        ageValue = (RadioGroup) findViewById(R.id.ageValue);
        gender = (RadioGroup) findViewById(R.id.rGender);
        genderDeath = (RadioGroup) findViewById(R.id.genderValue);

        ageValue.setOnCheckedChangeListener(this);
        gender.setOnCheckedChangeListener(this);

        //Individual Level Indicators
        spnCohort = (Spinner) findViewById(R.id.spnCohort);
        spnLm = (Spinner) findViewById(R.id.link_member);
        spnSo = (Spinner) findViewById(R.id.spinner_orphan);
        spnSc = (Spinner) findViewById(R.id.spinner_certificate);
        spnSs = (Spinner) findViewById(R.id.spinner_school);
        spnSp = (Spinner) findViewById(R.id.spinner_pregnant);
        spnSmH = (Spinner) findViewById(R.id.spinner_motherChildHealth);
        spnSa = (Spinner) findViewById(R.id.spinner_ANC);
        spnSb = (Spinner) findViewById(R.id.spinner_skilledBirthAT);
        spnSe = (Spinner) findViewById(R.id.spinner_exclusiveBreastFeeding);
        spnSup = (Spinner) findViewById(R.id.spinner_usingPlanning);

        spnPg =(Spinner) findViewById(R.id.spinner_penta1Given);
        spnPg3 =(Spinner) findViewById(R.id.spinner_penta3Given);
        spnSmg =(Spinner) findViewById(R.id.spinner_measlesGiven);
        spnFi =(Spinner) findViewById(R.id.spinner_fullyImmunized);
        spnVg =(Spinner) findViewById(R.id.spinner_vitaminAGiven);
        spnTg =(Spinner) findViewById(R.id.spinner_threeGroups);
        spnSlM =(Spinner) findViewById(R.id.spinner_severelyMalnourished);
        spnMlM =(Spinner) findViewById(R.id.spinner_moderatelyMalnourished);
        spnLli =(Spinner) findViewById(R.id.spinner_llin);
        spnSi =(Spinner) findViewById(R.id.spinner_chronic);
        spnSgh =(Spinner) findViewById(R.id.spinner_cough);
        spnSHi =(Spinner) findViewById(R.id.spinner_knownHIVStatus);
        spnDly =(Spinner) findViewById(R.id.spinner_disability);

        //Household Indicators
        spnSf = (Spinner) findViewById(R.id.safeWater);
        spnTw = (Spinner) findViewById(R.id.treatedWater);
        spnWf = (Spinner) findViewById(R.id.washingFacilities);
        spnD = (Spinner) findViewById(R.id.disposal);
        spnL = (Spinner) findViewById(R.id.latrine);

        panelIndividual = (ExpandablePanel) findViewById(R.id.individual_indicators);
        panelDeath = (ExpandablePanel) findViewById(R.id.deathIndicators);
        panelBackground = (ExpandablePanel) findViewById(R.id.panelBackgroundIndicators);
        panelhousehold_indicators = (ExpandablePanel) findViewById(R.id.household_indicators);

        panelIndividual.setOnExpandListener(this);
        panelDeath.setOnExpandListener(this);
        panelBackground.setOnExpandListener(this);
        panelhousehold_indicators.setOnExpandListener(this);

        genderDeath.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.radio_male:
                        genderType = "Male";
                        break;
                    case R.id.radio_female:
                        genderType = "Female";
                        break;
                }
            }
        });

        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.radio_male:
                        genderType = "Male";
                        break;
                    case R.id.radio_female:
                        genderType = "Female";
                        break;
                }
            }
        });

    }

    private void deathInformation() {

        if(members_death == 0){
            Toast.makeText(this, "Survey complete", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Home.class);
            startActivity(intent);
            finish();
        }

        secFour.setVisibility(View.GONE);

        //Death Information
        date_death = (MaterialEditText) findViewById(R.id.date_death);
        individualCodeDt = (MaterialEditText) findViewById(R.id.individualCodeDt);
        nameOfMemberDt = (MaterialEditText) findViewById(R.id.nameOfMemberDt);
        age_dt = (MaterialEditText) findViewById(R.id.age_dt);
        commentsDeath = (MaterialEditText) findViewById(R.id.commentsDeath);
        txtNoDeaths = (MaterialEditText) findViewById(R.id.txtNoDeaths);

        String dateDeath = date_death.getText().toString();
        String individualDeath = individualCodeDt.getText().toString();
        String nameOfMember = nameOfMemberDt.getText().toString();
        String ageDeath = age_dt.getText().toString();
        String cmDeath = commentsDeath.getText().toString();

        JSONObject backgroundInfo = new JSONObject();
        JSONObject householdInfo = new JSONObject();

        try {
            int id = survey_id;
            householdInfo.put("age_dt" , ageDeath);
            householdInfo.put("individual_code_dt" , individualDeath);
            householdInfo.put("date_collection_dt", dateDeath);
            householdInfo.put("name_dt", nameOfMember);
            householdInfo.put("sex_dt", genderType);
            householdInfo.put("comments", cmDeath);
            householdInfo.put("survey_id", id);

            householdInfo.put("common", section);
            backgroundInfo.put("section", householdInfo);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(proceed && members_death != 0){
            members_death = members_death - 1;
            save(this, backgroundInfo);
        }


    }

    private void basicInformation(){
        boolean bol;
        boolean okay = true;

        txtNoOfMembers = (MaterialEditText) findViewById(R.id.txtNoOfMembers);
        memberNo = txtNoOfMembers.getText().toString();
        String mobile = txtPatientMobile.getText().toString();

        txtNoDeaths = (MaterialEditText) findViewById(R.id.txtNoDeaths);
        number = txtNoDeaths.getText().toString();

        bol = isMobileValid(mobile);

        members_death = Integer.parseInt(number);

        if(!memberNo.equals("") ){
            members = Integer.parseInt(memberNo);
            members = members - 1;
        }

        JSONObject backgroundInfo = new JSONObject();
        JSONObject householdInfo = new JSONObject();

        if (bol){
            if ((mobile.length() == 10)) {
                okay = true;
            }
            else {
                okay = false;
                Toast.makeText(getApplicationContext(), "Enter a valid Mobile number", Toast.LENGTH_SHORT).show();
            }
        }

        if(villageId == null){
            Toast.makeText(getApplicationContext(), "Select A village", Toast.LENGTH_LONG).show();
            okay = false;
        }
        if(memberNo.equals("")){
            Toast.makeText(getApplicationContext(), "Enter number of members", Toast.LENGTH_LONG).show();
            return;
        }

        if(okay){

            try {
                householdInfo.put("health_worker_id" , session.getUser().getUserId());
                householdInfo.put("org_id" , session.getUser().getOrgId());
                householdInfo.put("uLatitude", uLatitude + "");
                householdInfo.put("uLongitude", uLongitude + "");
                householdInfo.put("village_id", villageId);
                householdInfo.put("member_no", memberNo);
                householdInfo.put("mobile", mobile);
                householdInfo.put("common", section);
                backgroundInfo.put("section", householdInfo);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            save(this, backgroundInfo);

            secOne.setVisibility(View.GONE);
        }


    }

    private void individualInformation() {
        boolean okay = true;

        code = individual_code.getText().toString();
        age = (MaterialEditText) findViewById(R.id.txtPatientAge);
        member_name = (MaterialEditText) findViewById(R.id.etFieldMember);
        gender = (RadioGroup) findViewById(R.id.rGender);

        String ageValue = age.getText().toString();
        String member = member_name.getText().toString();

        JSONObject backgroundInfo = new JSONObject();
        JSONObject householdInfo = new JSONObject();

        if(ageValue.equals("")){
            Toast.makeText(getApplicationContext(), "Enter individuals age", Toast.LENGTH_LONG).show();
            okay = false;
        }

        if(member.equals("")){
            Toast.makeText(getApplicationContext(), "Enter individuals name", Toast.LENGTH_LONG).show();
            okay = false;
        }

        if(okay){

            try {
                int id = survey_id;
                householdInfo.put("survey_id", id);
                householdInfo.put("member_name", member);
                householdInfo.put("age", ageValue);
                householdInfo.put("age_type", ageTypeValue);
                householdInfo.put("gender", genderType);
                householdInfo.put("individual", code);
                householdInfo.put("uid",  "HLD" + id + "N" + num);

                householdInfo.put("1",spnCohort.getSelectedItemId() + 10);
                householdInfo.put("2",spnLm.getSelectedItemId() + 4);
                householdInfo.put("3",spnSo.getSelectedItemId());
                householdInfo.put("4",spnSc.getSelectedItemId());
                householdInfo.put("5",spnSs.getSelectedItemId());
                householdInfo.put("6",spnSp.getSelectedItemId());
                householdInfo.put("7",spnSmH.getSelectedItemId());
                householdInfo.put("8",spnSa.getSelectedItemId());
                householdInfo.put("9",spnSb.getSelectedItemId());
                householdInfo.put("10",spnSe.getSelectedItemId());
                householdInfo.put("11",spnSup.getSelectedItemId() + 19);

                householdInfo.put("12",spnPg.getSelectedItemId());
                householdInfo.put("13",spnPg3.getSelectedItemId());
                householdInfo.put("14",spnSmg.getSelectedItemId());
                householdInfo.put("15",spnFi.getSelectedItemId());
                householdInfo.put("16",spnVg.getSelectedItemId());
                householdInfo.put("17",spnTg.getSelectedItemId());
                householdInfo.put("18",spnSlM.getSelectedItemId());
                householdInfo.put("19",spnMlM.getSelectedItemId());
                householdInfo.put("20",spnLli.getSelectedItemId() + 23);
                householdInfo.put("21",spnSi.getSelectedItemId());
                householdInfo.put("22",spnSgh.getSelectedItemId());
                householdInfo.put("23",spnSHi.getSelectedItemId());
                householdInfo.put("24",spnDly.getSelectedItemId());

                householdInfo.put("common", section);
                backgroundInfo.put("section", householdInfo);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(spnLm.getSelectedItemId() == 0){
                showInvalidMessage(R.string.link_member); return;
            }
            if(spnSo.getSelectedItemId() == 0){
                showInvalidMessage(R.string.extOrphan); return;
            }
            if( spnSs.getSelectedItemId() == 0){
                showInvalidMessage(R.string.extSchool); return;
            }
            if(spnSp.getSelectedItemId() == 0){
                showInvalidMessage(R.string.pregnant); return;
            }
            if(spnSmH.getSelectedItemId() == 0){
                showInvalidMessage(R.string.extMotherChildHealth); return;
            }
            if(spnSa.getSelectedItemId() == 0){
                showInvalidMessage(R.string.extANC); return;
            }
            if(spnSb.getSelectedItemId() == 0){
                showInvalidMessage(R.string.extSkilledBirthAT); return;
            }
            if(spnSe.getSelectedItemId() == 0){
                showInvalidMessage(R.string.extExclusiveBreastFeeding); return;
            }
            if(spnSup.getSelectedItemId() == 0){
                showInvalidMessage(R.string.usingPlanningMethods); return;
            }

            if(spnPg.getSelectedItemId() == 0){
                showInvalidMessage(R.string.penta1Given); return;
            }
            if(spnPg3.getSelectedItemId() == 0){
                showInvalidMessage(R.string.penta3Given); return;
            }
            if(spnSmg.getSelectedItemId() == 0){
                showInvalidMessage(R.string.measlesGiven); return;
            }
            if(spnFi.getSelectedItemId() == 0){
                showInvalidMessage(R.string.fullyImmunized); return;
            }
            if(spnVg.getSelectedItemId() == 0){
                showInvalidMessage(R.string.vitaminAGiven); return;
            }
            if(spnTg.getSelectedItemId() == 0){
                showInvalidMessage(R.string.childrenThreeGroups); return;
            }
            if(spnSlM.getSelectedItemId() == 0){
                showInvalidMessage(R.string.severelyMalnutritioned); return;
            }
            if(spnMlM.getSelectedItemId() == 0){
                showInvalidMessage(R.string.moderatelyMalnourished); return;
            }
            if(spnLli.getSelectedItemId() == 0){
                showInvalidMessage(R.string.LLIN); return;
            }
            if(spnSi.getSelectedItemId() == 0){
                showInvalidMessage(R.string.knownChronic); return;
            }
            if(spnSgh.getSelectedItemId() == 0){
                showInvalidMessage(R.string.cough); return;
            }
            if(spnSHi.getSelectedItemId() == 0){
                showInvalidMessage(R.string.knowsHIVStatus); return;
            }
            if(spnDly.getSelectedItemId() == 0){
                showInvalidMessage(R.string.disability); return;
            }

            save(this, backgroundInfo);

            num = num + 1;
        }


        if(members != -1 && section == 2){
            members = members - 1;
        }
    }

    private void houseHouseholdIndicators() {
        secTwo.setVisibility(View.GONE);
        number = "0";
        JSONObject init_household = new JSONObject();
        JSONObject householdInfo = new JSONObject();

        int id = survey_id;
        try {

            init_household.put("survey_id", id);
            init_household.put("common", section);
            init_household.put("1", spnSf.getSelectedItemId());
            init_household.put("2", spnTw.getSelectedItemId());
            init_household.put("3", spnWf.getSelectedItemId());
            init_household.put("4", spnD.getSelectedItemId());
            init_household.put("5", spnL.getSelectedItemId());

            householdInfo.put("section", init_household);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(spnSf.getSelectedItemId() == 0){
            showInvalidMessage(R.string.safe_water); return;
        }
        if(spnTw.getSelectedItemId() == 0){
            showInvalidMessage(R.string.treated_water); return;
        }
        if(spnWf.getSelectedItemId() == 0){
            showInvalidMessage(R.string.washing_facilities); return;
        }
        if(spnD.getSelectedItemId() == 0){
            showInvalidMessage(R.string.latrine); return;
        }
        if(spnL.getSelectedItemId() == 0) {
            showInvalidMessage(R.string.disposal); return;
        }

        save(this, householdInfo);

        if(!proceed){
            Toast.makeText(this, "Survey complete", Toast.LENGTH_SHORT).show();
        }

    }

    public void showInvalidMessage(int message){
        new SweetAlertDialog(Form513Activity.this,SweetAlertDialog.WARNING_TYPE)
                .setContentText("Select If " + getResources().getString(message)).show();
    }

    private void clearSectionThree() {

        clearEditTexts(new MaterialEditText[] {
                date_death, individualCodeDt, nameOfMemberDt, age_dt, commentsDeath, txtNoDeaths
        });
    }

    public void saveDetails(View view) throws JSONException{

        switch (section){
            case 1:
                basicInformation();
                break;
            case 2:
                individualInformation();
                break;
            case 3:
                houseHouseholdIndicators();
                break;
            case 4:
                deathInformation();
                break;
        }


    }

    public void clearEditTexts(MaterialEditText[] editTexts){
        for(MaterialEditText m : editTexts){
            m.setText("");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, CTAG + "Play Connection Succeeded");

        mLastLocation = LocationServices.FusedLocationApi .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            uLatitude = latitude;
            uLongitude = longitude;
            Toast.makeText(getApplicationContext(),"Location : " + latitude + ", " + longitude, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),"Could Not Get Location", Toast.LENGTH_LONG).show();
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    public void showDescription(View v){
        //Toast.makeText(getApplicationContext(), v.getContentDescription().toString(), Toast.LENGTH_SHORT).show();
        new SweetAlertDialog(Form513Activity.this,SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Details")
                .setContentText(v.getContentDescription().toString())
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    private void showDate(int year, int month, int day) {
        date_death.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(subLocationId == null){
            getArea(getApplicationContext(), new JSONObject(), AREA_SUB_LOCATION);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Form513Activity.this);
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
                Form513Activity.this.finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void householdHasDeaths() {

        lnDeaths = (LinearLayout) findViewById(R.id.death513);

        AlertDialog.Builder builder = new AlertDialog.Builder(Form513Activity.this);
        builder.setCancelable(false);
        builder.setTitle("Any deaths in the household");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                lnDeaths.setVisibility(View.GONE);
                proceed = false;
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                proceed = true;
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public boolean isMobileValid(String text){

        return text.matches("^([0])([7])([0-9])+");
    }

    public void save(final Context context, final JSONObject details){
        final ProgressDialog pDialog = new ProgressDialog(Form513Activity.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        MUtil.showProgressDialog(pDialog);
        String url = MApplication.url + "form513";

        Log.i(MApplication.TAG, MainActivity.class.getName() + " : Details " + details.toString());
        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        MUtil.hideProgressDialog(pDialog);
                        if(doLog) Log.i(TAG, CTAG + response.toString());
                        if(doLog) Log.i(TAG, LoginActivity.class.getName() + "Response : " +response.toString() + "\nSuccess : " + success.isSuccess() + "\nMessage : " + success.getMessage());
                        if(success.isSuccess()){

                            try {

                                if(section == 1){
                                    section = section + 1;
                                    survey_id = response.getInt("survey_id");
                                    secThree.setVisibility(View.VISIBLE);
                                }else if(section == 2 && members == 0){
                                    members = members - 1;
                                    clearSectionTwo();
                                }else if(section == 2 && members == -1){
                                    section = section + 1;
                                    secTwo.setVisibility(View.GONE);
                                }else if(section == 3){
                                    section = section + 1;
                                    secFour.setVisibility(View.GONE);
                                }else{
                                    clearSectionThree();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(getApplicationContext(),"Survey submitted", Toast.LENGTH_SHORT).show();


                        }else{
                            Crouton.makeText(Form513Activity.this , success.getMessage() , Style.ALERT).show();
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Crouton.makeText(Form513Activity.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "form513");
    }

    public void getArea(final Context context, final JSONObject details, final String whichArea){
        final ProgressDialog pDialog = new ProgressDialog(Form513Activity.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);
        final Activity activity = Form513Activity.this;
        MUtil.showProgressDialog(pDialog);
        String url = MApplication.url + whichArea;

        if(doLog) Log.i(MApplication.TAG, MainActivity.class.getName() + " : Details " + details.toString());
        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        if(doLog) Log.i(TAG, CTAG + response.toString());
                        Log.i(TAG, LoginActivity.class.getName() + "Response : " + response.toString() + "\nSuccess : " + success.isSuccess() + "\nMessage : " + success.getMessage());
                        MUtil.hideProgressDialog(pDialog);
                        ArrayList<HashMap<String,String>> arrayListAreas;
                        if(success.isSuccess()){
                            Crouton.makeText(Form513Activity.this , success.getMessage() , Style.CONFIRM).show();
                            try {
                                JSONArray jAreas = response.getJSONArray("areas");

                                if(whichArea.equals(AREA_SUB_LOCATION)){
                                    final ArrayList<Area> areasSubLocation = Area.makeArrayList(response.getJSONArray("areas"));
                                    spnSublocation.setAdapter(Area.getArrayAdapter(activity, areasSubLocation));

                                    spnSublocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            if(position == 0){
                                                Toast.makeText(context, "Select Sub Location", Toast.LENGTH_SHORT).show();
                                                villageId = null;
                                                spnVillage.setVisibility(View.INVISIBLE);
                                            } else {
                                                subLocationId = areasSubLocation.get(position).getId();
                                                spnVillage.setVisibility(View.VISIBLE);

                                                getArea(context, MUtil.simpleJSOnMaker(Survey.SUB_LOCATION_ID, subLocationId), AREA_VILLAGE);
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
                                                //MUtil.setVisibility(View.GONE, new View[]{txtHouseholdNum, txtHouseholdMember});
                                            }else {
                                                villageId = areasVillages.get(position).getId();
                                                //MUtil.setVisibility(View.VISIBLE, new View[]{txtHouseholdNum, txtHouseholdMember});

                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) { }
                                    });
                                }
                            }catch (JSONException je) {
                                Log.e(TAG, CTAG + "Error Retrieving Json Array : " + je.toString());
                            }catch (Exception e) {
                                Log.e(TAG, CTAG + "Error Retrieving Json Array : " + e.toString());
                            }
                            //MainActivity.this.finish();
                        }else{
                            Crouton.makeText(Form513Activity.this , success.getMessage() , Style.ALERT).show();
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Crouton.makeText(Form513Activity.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "getArea");
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId) {
            case R.id.radio_days:
                ageTypeValue = "D";
                break;
            case R.id.radio_months:
                ageTypeValue = "M";
            case R.id.radio_years:
                ageTypeValue = "Y";
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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

    private void clearSectionTwo() {

        clearEditTexts(new MaterialEditText[] {
                                            age, member_name
                                    });

        setSpinnerSelectedIndex(0, new Spinner[]{spnLm,spnSo,spnSc,spnSs,spnSp,spnSmH,spnSa,spnSb,
                spnSe,spnSup,spnPg,spnPg3,spnSmg,spnFi,spnVg,spnTg,spnSlM,spnMlM,spnLli,spnSi,spnSgh
                ,spnSHi,spnDly, spnCohort});


        ageValue.clearCheck();
        gender.clearCheck();
        individual_code.setText("");

        if(section == 2){
            Toast.makeText(getApplicationContext(), "Enter next member's details", Toast.LENGTH_SHORT).show();
        }

    }

    public void setSpinnerSelectedIndex(int index, Spinner[] spinners){
        for(Spinner s : spinners){
            s.setSelection(index);
        }
    }
}



