package com.dewcis.mdss;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.dewcis.mdss.model.User;
import com.dewcis.mdss.utils.AboutDialog;
import com.dewcis.mdss.utils.ExpandablePanel;
import com.dewcis.mdss.utils.GPSTracker;
import com.dewcis.mdss.utils.MUtil;
import com.dewcis.mdss.volley.VolleyErrorHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


/**
 * Created by henriquedn on 4/14/15.
 * henrydkm@gmail.com
 * henry.maina@dewcis.com
 */

public class MainActivityOr extends ActionBarActivity implements ExpandablePanel.OnExpandListener, View.OnClickListener, ConnectionCallbacks, OnConnectionFailedListener, LocationListener{
    MSession session;
    String TAG = MApplication.TAG;
    static boolean doLog = MApplication.LOGDEBUG;
    static String CTAG = MainActivityOr.class.getName() + " : " ;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    boolean isEdit = false;
    int editSurveyId = -1;
    boolean hasMultipleChildren = false;
    private Location mLastLocation;
    private boolean doubleBackToExitPressedOnce = false;
    int survey_status = 3;

    public static final String AREA_SUB_LOCATION = "getSublocations", AREA_VILLAGE = "getVillages";
    Integer subLocationId = null, villageId = null;


    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    double uLatitude,uLongitude;

    ExpandablePanel panelChronic, panelBasicInfo, panelMotherInfo, panelChildInfo, panelReferralsInfo,panelDefaultersInfo, panelDeathInfo, panelOthersInfo,panelHouseholdInfo;
    public MaterialEditText  txtNameIndividual, txtHouseholdNum, txtHouseholdMember, txtNickname, txtLandmark, txtRemarks, txtReferalIllnessOther, txtDeathInfoOther;//txtVillageName

    TextView lblDisplayUser, lblReturnReason,lblReturnTitle;
    CheckBox chkMultipleChildren;
    Button btnSubmit;

    //Basic Information
    Spinner spnSublocation,spnVillage;//spnCounty,spnSubCounty;



    //mother information
    Spinner spnPregnant , spnF, spnG, spnH, spnI, spnJ, spnK;
    //child information
    Spinner spnChildGender, spnL, spnM, spnN, spnO;
    //referral info
    Spinner spnP, spnQ, spnR, spnS, spnT, spnU, spnV, spnW, spnX;
    //defaulters info
    Spinner spnZ, spnAA, spnAB, spnAC, spnAL;
    //death information
    Spinner spnAD_a, spnAD_b, spnAD_c, spnAD_d;
    //household information
    Spinner spnAI, spnAJ, spnAK;

    Spinner spnY_a, spnY_b, spnY_c, spnY_d;

    String validate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_or);

        session = new MSession(getApplicationContext());

        if(!session.isLoggedIn()){
            session.logout(this);
        }

        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        createLocationRequest();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));
        actionBar.setSubtitle("MOH 514");
        actionBar.setIcon(R.mipmap.ic_launcher);

         lblDisplayUser = (TextView) findViewById(R.id.lblDisplayUser);
         lblReturnReason = (TextView) findViewById(R.id.lblReturnReason);
         lblReturnTitle = (TextView) findViewById(R.id.lblReturnTitle);
        lblReturnTitle.setVisibility(View.GONE);

        lblDisplayUser.setText("Perform New Report");
        lblReturnReason.setText("");
        chkMultipleChildren = (CheckBox) findViewById(R.id.chkMultipleChildren);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        //setSupportActionBar(toolbar);

        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){
            Toast.makeText(getApplicationContext(), gps.getLatitude() + " , " + gps.getLongitude(), Toast.LENGTH_LONG).show();
        }

        panelBasicInfo = (ExpandablePanel) findViewById(R.id.panelBasicInfo);
        panelMotherInfo = (ExpandablePanel) findViewById(R.id.panelMotherInfo);
        panelChildInfo = (ExpandablePanel) findViewById(R.id.panelChildInfo);
        panelReferralsInfo = (ExpandablePanel) findViewById(R.id.panelReferralsInfo);

        panelDefaultersInfo = (ExpandablePanel) findViewById(R.id.panelDefaultersInfo);
        panelDeathInfo = (ExpandablePanel) findViewById(R.id.panelDeathInfo);
        panelOthersInfo = (ExpandablePanel) findViewById(R.id.panelOthersInfo);
        panelHouseholdInfo = (ExpandablePanel) findViewById(R.id.panelHouseholdInfo);
        panelChronic = (ExpandablePanel) findViewById(R.id.panelChronic);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        panelBasicInfo.setOnExpandListener(this);
        panelMotherInfo.setOnExpandListener(this);
        panelChildInfo.setOnExpandListener(this);
        panelReferralsInfo.setOnExpandListener(this);
        panelDefaultersInfo.setOnExpandListener(this);
        panelDeathInfo.setOnExpandListener(this);
        panelOthersInfo.setOnExpandListener(this);
        panelHouseholdInfo.setOnExpandListener(this);
        panelChronic.setOnExpandListener(this);

        //basic infor
        //spnCounty = (Spinner) findViewById(R.id.spnCounty);
        //spnSubCounty  = (Spinner) findViewById(R.id.spnSubCounty);

        spnSublocation  = (Spinner) findViewById(R.id.spnSublocation);
        spnVillage  = (Spinner) findViewById(R.id.spnVillage);

        //txtVillageName = (MaterialEditText) findViewById(R.id.txtVillageName);
        txtHouseholdNum = (MaterialEditText) findViewById(R.id.txtHouseholdNum);
        txtHouseholdMember = (MaterialEditText) findViewById(R.id.txtHouseholdMember);
        txtNickname = (MaterialEditText) findViewById(R.id.txtNickname);
        txtLandmark = (MaterialEditText) findViewById(R.id.txtLandmark);
        txtRemarks = (MaterialEditText) findViewById(R.id.txtRemarks);

        //mother information
        spnPregnant =  (Spinner) findViewById(R.id.spnPregnant);
        spnF =  (Spinner) findViewById(R.id.spnF);
        spnG =  (Spinner) findViewById(R.id.spnG);
        spnH =  (Spinner) findViewById(R.id.spnH);
        spnI =  (Spinner) findViewById(R.id.spnI);
        spnJ =  (Spinner) findViewById(R.id.spnJ);
        spnK =  (Spinner) findViewById(R.id.spnK);

        //child information
        spnChildGender =  (Spinner) findViewById(R.id.spnChildGender);
        spnL =  (Spinner) findViewById(R.id.spnL);
        spnM =  (Spinner) findViewById(R.id.spnM);
        spnN =  (Spinner) findViewById(R.id.spnN);
        spnO =  (Spinner) findViewById(R.id.spnO);

        //referal info
        spnP =  (Spinner) findViewById(R.id.spnP);
        spnQ =  (Spinner) findViewById(R.id.spnQ);
        spnR =  (Spinner) findViewById(R.id.spnR);
        spnS =  (Spinner) findViewById(R.id.spnS);
        spnT =  (Spinner) findViewById(R.id.spnT);
        spnU =  (Spinner) findViewById(R.id.spnU);
        spnV =  (Spinner) findViewById(R.id.spnV);
        spnW =  (Spinner) findViewById(R.id.spnW);
        spnX =  (Spinner) findViewById(R.id.spnX);

        //defaulters info
        spnZ =  (Spinner) findViewById(R.id.spnZ);
        spnAA =  (Spinner) findViewById(R.id.spnAA);
        spnAB =  (Spinner) findViewById(R.id.spnAB);
        spnAC =  (Spinner) findViewById(R.id.spnAC);
        spnAL =  (Spinner) findViewById(R.id.spnAL);

        //death information
        spnAD_a =  (Spinner) findViewById(R.id.spnAD_a);
        spnAD_b =  (Spinner) findViewById(R.id.spnAD_b);
        spnAD_c  =  (Spinner) findViewById(R.id.spnAD_c);
        spnAD_d =  (Spinner) findViewById(R.id.spnAD_d);
        txtDeathInfoOther = (MaterialEditText) findViewById(R.id.txtDeathInfoOther);

        //household information
        spnAI =  (Spinner) findViewById(R.id.spnAI);
        spnAJ =  (Spinner) findViewById(R.id.spnAJ);
        spnAK =  (Spinner) findViewById(R.id.spnAK);

        spnY_a =  (Spinner) findViewById(R.id.spnY_a);
        spnY_b =  (Spinner) findViewById(R.id.spnY_b);
        spnY_c =  (Spinner) findViewById(R.id.spnY_c);
        spnY_d =  (Spinner) findViewById(R.id.spnY_d);
        txtReferalIllnessOther = (MaterialEditText) findViewById(R.id.txtReferalIllnessOther);

        btnSubmit.setOnClickListener(this);

        chkMultipleChildren.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                hasMultipleChildren = isChecked;
            }
        });


        //CHECK if bundle has survey
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString(Survey.SURVEY);
            if(doLog) Log.i(TAG, CTAG + "Has Survey " +  value);
            chkMultipleChildren.setVisibility(View.GONE);
            try {
                Survey editSurvey = Survey.makeFromJSON((new JSONObject(value)));

                JSONObject basic = editSurvey.getJsurvey();

                JSONObject basicInfo = basic.getJSONObject("basicInfo");

                isEdit = true;
                btnSubmit.setText("Update Report");
                editSurveyId = editSurvey.getSurvey_id();
                lblDisplayUser.setText("Edit Report : " + editSurvey.getSurvey_id());
                lblReturnTitle.setVisibility(View.VISIBLE);
                lblReturnReason.setText(basicInfo.getString("returnReason"));
                survey_status = editSurvey.getSurveyStatus();

                spnSublocation.setVisibility(View.GONE);
                spnVillage.setVisibility(View.GONE);

                //spnCounty.setSelection(editSurvey.getSub -1);
                //spnSubCounty.setSelection(editSurvey.getSub_county_id());
                //txtVillageName.setText(editSurvey.getTxtVillageName());

                String house ="KAM/"+ editSurveyId + "/017";
                villageId = editSurvey.getvillageID();
                txtHouseholdNum.setText(house);
                txtHouseholdMember.setText(basicInfo.getString("household_member"));
                txtNickname.setText(basicInfo.getString("nickname"));
                txtLandmark.setText(basicInfo.getString("landmark"));
                txtRemarks.setText(basicInfo.getString("remarks"));

                LinearLayout lnChildInformation = (LinearLayout) findViewById(R.id.child_info);
                LinearLayout lnMotherInformation = (LinearLayout) findViewById(R.id.mother_info);

                JSONObject jSurvey = editSurvey.getJsurvey();

                //mother information
                JSONObject motherInfo = jSurvey.getJSONObject("motherInfo");

                if(!motherInfo.toString().equals("{}")){

                    lnChildInformation.setVisibility(View.GONE);

                    spnPregnant.setSelection(motherInfo.getInt("1"));
                    spnF.setSelection(motherInfo.getInt("2"));
                    spnG.setSelection(motherInfo.getInt("3"));
                    spnH.setSelection(motherInfo.getInt("4"));
                    spnI.setSelection(motherInfo.getInt("5"));
                    spnJ.setSelection(motherInfo.getInt("6"));
                    spnK.setSelection(motherInfo.getInt("7"));

                }else{
                    validate = "mother_empty";
                }

                //child information
                JSONObject childInfo = jSurvey.getJSONObject("childInfo");

                if(!childInfo.toString().equals("{}")){

                    lnMotherInformation.setVisibility(View.GONE);

                    spnChildGender.setSelection(childInfo.getInt("1") - 3);
                    spnL.setSelection(childInfo.getInt("2"));
                    spnM.setSelection(childInfo.getInt("3"));
                    spnN.setSelection(childInfo.getInt("4"));
                    spnO.setSelection(childInfo.getInt("5"));

                }else{
                    validate = "child_empty";
                }

                //referal info
                JSONObject referralInfo = jSurvey.getJSONObject("referralInfo");

                spnP.setSelection(referralInfo.getInt("1"));
                spnQ.setSelection(referralInfo.getInt("2"));
                spnR.setSelection(referralInfo.getInt("3"));
                spnS.setSelection(referralInfo.getInt("4"));
                spnT.setSelection(referralInfo.getInt("5"));
                spnU.setSelection(referralInfo.getInt("6"));
                spnV.setSelection(referralInfo.getInt("7"));
                spnW.setSelection(referralInfo.getInt("8"));
                spnX.setSelection(referralInfo.getInt("9"));

                //defaulters info
                JSONObject defaultersInfo = jSurvey.getJSONObject("defaultersInfo");

                spnZ.setSelection(defaultersInfo.getInt("1"));
                spnAA.setSelection(defaultersInfo.getInt("2"));
                spnAB.setSelection(defaultersInfo.getInt("3"));
                spnAC.setSelection(defaultersInfo.getInt("4"));
                spnAL.setSelection(defaultersInfo.getInt("5"));

                //death information
                JSONObject deathInfo = jSurvey.getJSONObject("deathInfo");

                spnAD_a.setSelection(deathInfo.getInt("1") + 1);
                spnAD_b.setSelection(deathInfo.getInt("2") + 1);
                spnAD_c.setSelection(deathInfo.getInt("3") + 1);
                spnAD_d.setSelection(deathInfo.getInt("4") + 1);
                txtDeathInfoOther.setText(deathInfo.getString("5"));

                //illness information
                JSONObject illnessInfo = jSurvey.getJSONObject("illnessInfo");

                spnY_a.setSelection(illnessInfo.getInt("10") + 1);
                spnY_b.setSelection(illnessInfo.getInt("11") + 1);
                spnY_c.setSelection(illnessInfo.getInt("12") + 1);
                spnY_d.setSelection(illnessInfo.getInt("13") + 1);
                txtReferalIllnessOther.setText(illnessInfo.getString("14"));

                //household information
                JSONObject householdInfo = jSurvey.getJSONObject("householdInfo");

                spnAI.setSelection(householdInfo.getInt("1"));
                spnAJ.setSelection(householdInfo.getInt("2"));
                spnAK.setSelection(householdInfo.getInt("3"));


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        if(subLocationId == null){
            getArea(getApplicationContext(), new JSONObject(), AREA_SUB_LOCATION);
        }


        checkPlayServices();
        // Resuming the periodic location updates
        try{
            if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }catch(NullPointerException e){
            if(doLog) Log.e(TAG, CTAG + "> NullPointerException : " + e.toString());
        }

        //this.doubleBackToExitPressedOnce = false;


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSubmit:
                submit();
                break;
        }
    }

    public void showDescription(View v){
        //Toast.makeText(getApplicationContext(), v.getContentDescription().toString(), Toast.LENGTH_SHORT).show();
        new SweetAlertDialog(MainActivityOr.this,SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Details").setContentText(v.getContentDescription().toString())
                .show();
    }

    void submit(){
        JSONObject jSurvey = new JSONObject();
        Context context = getApplicationContext();

        String village = "";//txtVillageName.getText().toString();
        String houseHoldNum = txtHouseholdNum.getText().toString();
        String householdMember = txtHouseholdMember.getText().toString();
        String remarks = txtRemarks.getText().toString();

        /*if(spnSubCounty.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.sub_county); return;
        }*/


        if(villageId == null){
            Toast.makeText(context, "Select A village", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(houseHoldNum)){
            Toast.makeText(context, "Enter Household Name", Toast.LENGTH_LONG).show();
            txtHouseholdNum.setError("Required"); txtHouseholdNum.requestFocus(); return;
        }
        if(TextUtils.isEmpty(householdMember)){
            Toast.makeText(context, "Enter Household Number", Toast.LENGTH_LONG).show();
            txtHouseholdMember.setError("Required"); txtHouseholdMember.requestFocus(); return;
        }

        if(!validate.equals("mother_empty")) {
            //mother information
            //spnPregnant , spnF, spnG, spnH, spnI, spnJ, spnK;
            if (spnPregnant.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.pregnant);
                return;
            }
            if (spnF.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.f_name);
                return;
            }
            if (spnG.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.g_name);
                return;
            }
            if (spnH.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.h_name);
                return;
            }
            if (spnI.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.i_name);
                return;
            }
            if (spnJ.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.j_name);
                return;
            }
            if (spnK.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.k_name);
                return;
            }
        }

        if(!validate.equals("child_empty")) {
            //child information
            //spnChildGender, spnL, spnM, spnN, spnO;

            if (spnChildGender.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.gender);
                return;
            }
            if (spnL.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.l_name);
                return;
            }
            if (spnM.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.m_name);
                return;
            }
            if (spnN.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.n_name);
                return;
            }
            if (spnO.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.o_name);
                return;
            }

        }
        //referal info
        //spnP, spnQ, spnR, spnS, spnT, spnU, spnV, spnW, spnX, spnY_a, spnY_b, spnY_c, spnY_d ;

        if(spnP.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.p_name); return;
        }
        if(spnQ.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.q_name); return;
        }
        if(spnR.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.r_name); return;
        }
        if(spnS.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.s_name); return;
        }
        if(spnT.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.t_name); return;
        }
        if(spnU.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.u_name); return;
        }
        if(spnV.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.v_name); return;
        }
        if(spnW.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.w_name); return;
        }
        if(spnX.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.x_name); return;
        }

        //defaulters info
        // spnZ, spnAA, spnAB, spnAC;
        if(spnZ.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.z_name); return;
        }
        if(spnAA.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.aa_name); return;
        }
        if(spnAB.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.ab_name); return;
        }
        if(spnAC.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.ac_name); return;
        }
        if(spnAL.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.al_name); return;
        }


        //death information
        //spnAD_a, spnAD_b, spnAD_c, spnAD_d;
        if(spnAD_a.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.ad_a); return;
        }
        if(spnAD_b.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.ad_b); return;
        }
        if(spnAD_c.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.ad_c); return;
        }
        if(spnAD_d.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.ad_d); return;
        }

        //household information
         //spnAI, spnAJ, spnAK;
        if(spnAI.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.ai_name); return;
        }
        if(spnAJ.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.aj_name); return;
        }
        if(spnAK.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.ak_name); return;
        }


        JSONObject jBasicInfo = new JSONObject();
        JSONObject jMotherInfo = new JSONObject();
        JSONObject jChildInfo = new JSONObject();
        JSONObject jReferralInfo = new JSONObject();
        JSONObject jDefaultersInfo = new JSONObject();
        JSONObject jDeathInfo = new JSONObject();

        JSONObject jHouseholdInfo = new JSONObject();
        JSONObject jIllnessInfo = new JSONObject();
        try {
            jBasicInfo.put(User.ID , session.getUser().getUserId());
            jBasicInfo.put(User.ORG_ID , session.getUser().getOrgId());
            jBasicInfo.put("survey_id", editSurveyId);
            jBasicInfo.put("villageId", villageId);
            jBasicInfo.put("houseHoldNum", houseHoldNum);
            jBasicInfo.put("householdMember", householdMember);
            jBasicInfo.put("latitude", uLatitude);
            jBasicInfo.put("longitude", uLongitude);
            jBasicInfo.put("remarks", remarks);


            if(!validate.equals("mother_empty")) {

                //mother information
                jMotherInfo.put("1", ((Spinner) findViewById(R.id.spnPregnant)).getSelectedItemPosition());
                jMotherInfo.put("2", ((Spinner) findViewById(R.id.spnF)).getSelectedItemPosition() );
                jMotherInfo.put("3", ((Spinner) findViewById(R.id.spnG)).getSelectedItemPosition() );
                jMotherInfo.put("4", ((Spinner) findViewById(R.id.spnH)).getSelectedItemPosition() );
                jMotherInfo.put("5", ((Spinner) findViewById(R.id.spnI)).getSelectedItemPosition() );
                jMotherInfo.put("6", ((Spinner) findViewById(R.id.spnJ)).getSelectedItemPosition() );
                jMotherInfo.put("7", ((Spinner) findViewById(R.id.spnK)).getSelectedItemPosition() );
            }


            if(!validate.equals("child_empty")) {

                //child information
                jChildInfo.put("1", ((Spinner) findViewById(R.id.spnChildGender)).getSelectedItemPosition() + 3);
                jChildInfo.put("2", ((Spinner) findViewById(R.id.spnL)).getSelectedItemPosition() );
                jChildInfo.put("3", ((Spinner) findViewById(R.id.spnM)).getSelectedItemPosition() );
                jChildInfo.put("4", ((Spinner) findViewById(R.id.spnN)).getSelectedItemPosition() );
                jChildInfo.put("5", ((Spinner) findViewById(R.id.spnO)).getSelectedItemPosition() );
            }


            //referal info
            jReferralInfo.put("1", ((Spinner) findViewById(R.id.spnP)).getSelectedItemPosition() );
            jReferralInfo.put("2", ((Spinner) findViewById(R.id.spnQ)).getSelectedItemPosition() );
            jReferralInfo.put("3", ((Spinner) findViewById(R.id.spnR)).getSelectedItemPosition() );
            jReferralInfo.put("4", ((Spinner) findViewById(R.id.spnS)).getSelectedItemPosition() );
            jReferralInfo.put("5", ((Spinner) findViewById(R.id.spnT)).getSelectedItemPosition() );
            jReferralInfo.put("6", ((Spinner) findViewById(R.id.spnU)).getSelectedItemPosition() );
            jReferralInfo.put("7", ((Spinner) findViewById(R.id.spnV)).getSelectedItemPosition() );
            jReferralInfo.put("8", ((Spinner) findViewById(R.id.spnW)).getSelectedItemPosition() );
            jReferralInfo.put("9", ((Spinner) findViewById(R.id.spnX)).getSelectedItemPosition() );


            //defaulters info
            jDefaultersInfo.put("1", ((Spinner) findViewById(R.id.spnZ)).getSelectedItemPosition());
            jDefaultersInfo.put("2", ((Spinner) findViewById(R.id.spnAA)).getSelectedItemPosition());
            jDefaultersInfo.put("3", ((Spinner) findViewById(R.id.spnAB)).getSelectedItemPosition());
            jDefaultersInfo.put("4", ((Spinner) findViewById(R.id.spnAC)).getSelectedItemPosition());
            jDefaultersInfo.put("5", ((Spinner) findViewById(R.id.spnAL)).getSelectedItemPosition());

            //death information
            jDeathInfo.put("1", ((Spinner) findViewById(R.id.spnAD_a)).getSelectedItemPosition() );
            jDeathInfo.put("2", ((Spinner) findViewById(R.id.spnAD_b)).getSelectedItemPosition() );
            jDeathInfo.put("3", ((Spinner) findViewById(R.id.spnAD_c)).getSelectedItemPosition() );
            jDeathInfo.put("4", ((Spinner) findViewById(R.id.spnAD_d)).getSelectedItemPosition() );
            jDeathInfo.put("5", ((EditText) findViewById(R.id.txtDeathInfoOther)).getText().toString());

            //illness information
            jIllnessInfo.put("10", ((Spinner) findViewById(R.id.spnY_a)).getSelectedItemPosition());
            jIllnessInfo.put("11", ((Spinner) findViewById(R.id.spnY_b)).getSelectedItemPosition());
            jIllnessInfo.put("12", ((Spinner) findViewById(R.id.spnY_c)).getSelectedItemPosition());
            jIllnessInfo.put("13", ((Spinner) findViewById(R.id.spnY_d)).getSelectedItemPosition());
            jIllnessInfo.put("14", (txtReferalIllnessOther.getText().toString()));

            //household information
            jHouseholdInfo.put("1", ((Spinner) findViewById(R.id.spnAI)).getSelectedItemPosition() );
            jHouseholdInfo.put("2", ((Spinner) findViewById(R.id.spnAJ)).getSelectedItemPosition() );
            jHouseholdInfo.put("3", ((Spinner) findViewById(R.id.spnAK)).getSelectedItemPosition() );


            jSurvey.put("basicInfo", jBasicInfo);
            jSurvey.put("motherInfo", jMotherInfo);
            jSurvey.put("childInfo", jChildInfo);
            jSurvey.put("referralInfo", jReferralInfo);
            jSurvey.put("defaultersInfo", jDefaultersInfo);
            jSurvey.put("deathInfo", jDeathInfo);
            jSurvey.put("householdInfo", jHouseholdInfo);
            jSurvey.put("illnessInfo", jIllnessInfo);

        } catch (JSONException e) {
            Log.e(TAG, CTAG + e.toString());
        }catch (NullPointerException e) {
            Log.e(TAG, CTAG + "NullPointerException : " + e.toString());
        }

        Log.i(TAG, CTAG + "jSurvey : " + jSurvey);

        save(context, jSurvey);



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

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        Log.i(TAG, CTAG + "Play Connection Suspended : " + i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, CTAG + "Play Connection failed : " + connectionResult.getErrorCode());
    }
    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }
    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        Toast.makeText(getApplicationContext(), "Location changed!",Toast.LENGTH_SHORT).show();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }



    public void save(final Context context, final JSONObject details){
        final ProgressDialog pDialog = new ProgressDialog(MainActivityOr.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        MUtil.showProgressDialog(pDialog);
        String url = "";
        if(isEdit){
            int status = 3;

            if(survey_status == 2){
                status =  3;
            }else{
                status = 7;
            }
            url = MApplication.url + "editSurvey";

            try {
                details.put("survey_status", status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            url = MApplication.url + "survey";
        }
        //if(doLog)

        Log.i(MApplication.TAG, MainActivityOr.class.getName() + " : Details " + details.toString());
        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        if(doLog) Log.i(TAG, CTAG + response.toString());
                        Log.i(TAG, LoginActivity.class.getName() + "Response : " +response.toString() + "\nSuccess : " + success.isSuccess() + "\nMessage : " + success.getMessage());
                        MUtil.hideProgressDialog(pDialog);
                        if(success.isSuccess()){
                            Crouton.makeText(MainActivityOr.this , success.getMessage() , Style.CONFIRM).show();

                            Intent intent = new Intent(context, Home.class);
                            startActivity(intent);
                            //spnSubCounty.setSelection(0);

                            //MainActivityOr.this.finish();
                        }else{
                            Crouton.makeText(MainActivityOr.this , success.getMessage() , Style.ALERT).show();
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Crouton.makeText(MainActivityOr.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "survey");
    }


    public void showInvalidMessage(int message){
        new SweetAlertDialog(MainActivityOr.this,SweetAlertDialog.WARNING_TYPE)
                .setContentText("Select If " + getResources().getString(message)).show();
    }


    public void setSpinnerSelectedIndex(int index, Spinner[] spinners){
        for(Spinner s : spinners){
            s.setSelection(index);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivityOr.this);
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
                   MainActivityOr.this.finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();


    }

    public void getArea(final Context context, final JSONObject details, final String whichArea){
        final ProgressDialog pDialog = new ProgressDialog(MainActivityOr.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);
        final Activity activity = MainActivityOr.this;
        MUtil.showProgressDialog(pDialog);
        String url = MApplication.url + whichArea;

        if(doLog) Log.i(MApplication.TAG, MainActivityOr.class.getName() + " : Details " + details.toString());
        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        if(doLog) Log.i(TAG, CTAG + response.toString());
                        Log.i(TAG, LoginActivity.class.getName() + "Response : " +response.toString() + "\nSuccess : " + success.isSuccess() + "\nMessage : " + success.getMessage());
                        MUtil.hideProgressDialog(pDialog);
                        ArrayList<HashMap<String,String>> arrayListAreas;
                        if(success.isSuccess()){
                            Crouton.makeText(MainActivityOr.this , success.getMessage() , Style.CONFIRM).show();
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
                                            }else{
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
                            //MainActivityOr.this.finish();
                        }else{
                            Crouton.makeText(MainActivityOr.this , success.getMessage() , Style.ALERT).show();
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Crouton.makeText(MainActivityOr.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "getArea");
    }



}
