package com.dewcis.mdss;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dewcis.mdss.SharedPreferences.SharedPreferences514;
import com.dewcis.mdss.model.Area;
import com.dewcis.mdss.model.DraftList;
import com.dewcis.mdss.model.MSession;
import com.dewcis.mdss.SharedPreferences.SharedPreference;
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
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


/**
 * Created by henriquedn on 4/14/15.
 * henrydkm@gmail.com
 * henry.maina@dewcis.com
 */

public class HouseHoldActivity extends ActionBarActivity implements ExpandablePanel.OnExpandListener, View.OnClickListener, ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    MSession session;
    String TAG = MApplication.TAG;
    static boolean doLog = MApplication.LOGDEBUG;
    static String CTAG = MainActivity.class.getName() + " : " ;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    boolean isEdit = false;
    int editSurveyId = -1;
    private Location mLastLocation;
    String houseHoldNum, householdMember, remarks, householdNumber, childNum, third, second;
    int postpartum = 0, pregnant = 0, infants = 0, children = 0, other_mothers = 0, other_members = 0;

    public static final String AREA_SUB_LOCATION = "getSublocations", AREA_VILLAGE = "getVillages";
    Integer subLocationId = null, villageId = null;
    String house_id;


    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    private SharedPreference sharedPreference;
    SharedPreferences514 prefs;

    double uLatitude, uLongitude;

    ExpandablePanel panelMembers, panelBasicInfo, panelDeathInfo, panelOthersInfo,panelHouseholdInfo, panelChronic;
    public MaterialEditText  txtOtherPeople, txtWomen, txtRemarks, txtChildrenNo, txtMotherPm, txtMotherPg, txtMotherInf,
            txtHouseMobile, txtHouseholdMember, txtDeathInfoOther, txtReferalIllnessOther, txtSecond, txtThird, txtNickname,
            txtLandmark;

    TextView lblDisplayUser; Button btnSubmit;
    String select = "";
    int survey = 0;
    int survey_status = 0;

    public static final String HOUSEHOLD_INFORMATION = "house_hold";

    //Basic Information
    Spinner spnSublocation, spnVillage;//spnCounty,spnSubCounty;

    Spinner spnZ, spnAA, spnAB, spnAC, spnAL;
    //death information
    Spinner spnAD_a, spnAD_b, spnAD_c, spnAD_d, spnY_a, spnY_b, spnY_c, spnY_d;
    //household information
    Spinner spnAI, spnAJ, spnAK, household;

    CheckBox radioVisit;

    String members;

    String searchJson;

    JSONObject jBasicInfo;

    JSONObject jDeathInfo;
    JSONObject jHouseholdInfo;
    JSONObject jSurvey;
    JSONObject householdIllness;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_hold);
        setContentView(R.layout.activity_house_hold);


        session = new MSession(getApplicationContext());

        if(!session.isLoggedIn()){
            session.logout(this);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));
        actionBar.setSubtitle("MOH 514 Household");
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setHomeButtonEnabled(false);

        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        createLocationRequest();

        GPSTracker gps = new GPSTracker(this);
        if(gps.canGetLocation()){
            Toast.makeText(getApplicationContext(), gps.getLatitude() + " , " + gps.getLongitude(), Toast.LENGTH_LONG).show();
        }

        lblDisplayUser = (TextView) findViewById(R.id.lblDisplayUser);

        lblDisplayUser.setText("General Household Information");

        panelBasicInfo = (ExpandablePanel) findViewById(R.id.panelBasicInfo);
        panelMembers = (ExpandablePanel) findViewById(R.id.panelMemberCount);
        panelDeathInfo = (ExpandablePanel) findViewById(R.id.panelDeathInfo);
        panelOthersInfo = (ExpandablePanel) findViewById(R.id.panelOthersInfo);
        panelHouseholdInfo = (ExpandablePanel) findViewById(R.id.panelHouseholdInfo);
        panelOthersInfo = (ExpandablePanel) findViewById(R.id.panelOthersInfo);
        panelChronic = (ExpandablePanel) findViewById(R.id.panelChronic);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        radioVisit = (CheckBox) findViewById(R.id.visit);

        final LinearLayout lnNew = (LinearLayout) findViewById(R.id.sectionNew);
        final LinearLayout lnFollow = (LinearLayout) findViewById(R.id.sectionFollow);

        panelMembers.setOnExpandListener(this);
        panelBasicInfo.setOnExpandListener(this);
        panelDeathInfo.setOnExpandListener(this);
        panelOthersInfo.setOnExpandListener(this);
        panelChronic.setOnExpandListener(this);
        panelHouseholdInfo.setOnExpandListener(this);

        radioVisit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    lnNew.setVisibility(View.GONE);
                    lnFollow.setVisibility(View.VISIBLE);

                }else{
                    lnNew.setVisibility(View.VISIBLE);
                    lnFollow.setVisibility(View.GONE);
                }
            }
        });

        //basic infor

        spnSublocation  = (Spinner) findViewById(R.id.spnSublocation);
        spnVillage  = (Spinner) findViewById(R.id.spnVillage);

        txtHouseholdMember = (MaterialEditText) findViewById(R.id.txtHouseholdMember);
        txtRemarks = (MaterialEditText) findViewById(R.id.txtRemarks);
        txtSecond = (MaterialEditText) findViewById(R.id.txtSecondName);
        txtThird = (MaterialEditText) findViewById(R.id.txtThirdName);
        txtHouseMobile = (MaterialEditText) findViewById(R.id.txtHouseholdMobile);
        txtChildrenNo = (MaterialEditText) findViewById(R.id.txtChildrenNo);
        txtMotherPm = (MaterialEditText) findViewById(R.id.txtMothersNo);
        txtMotherPg = (MaterialEditText) findViewById(R.id.txtMothersNo2);
        txtMotherInf = (MaterialEditText) findViewById(R.id.txtNewBorns);
        txtWomen = (MaterialEditText) findViewById(R.id.txtWomen);
        txtOtherPeople = (MaterialEditText) findViewById(R.id.txtOtherPeople);
        txtNickname = (MaterialEditText) findViewById(R.id.txtNickname);
        txtLandmark = (MaterialEditText) findViewById(R.id.txtLandmark);

        txtLandmark.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtNickname.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtSecond.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtThird.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtRemarks.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        txtHouseholdMember.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        //defaulters info
        spnZ =  (Spinner) findViewById(R.id.spnZ);
        spnAA =  (Spinner) findViewById(R.id.spnAA);
        spnAB =  (Spinner) findViewById(R.id.spnAB);
        spnAC =  (Spinner) findViewById(R.id.spnAC);
        spnAL =  (Spinner) findViewById(R.id.spnAL);

        spnY_a =  (Spinner) findViewById(R.id.spnY_a);
        spnY_b =  (Spinner) findViewById(R.id.spnY_b);
        spnY_c =  (Spinner) findViewById(R.id.spnY_c);
        spnY_d =  (Spinner) findViewById(R.id.spnY_d);
        txtReferalIllnessOther = (MaterialEditText) findViewById(R.id.txtReferalIllnessOther);


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

        btnSubmit.setOnClickListener(this);

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
            case R.id.action_drafts:
                Intent draft = new Intent(this, DraftList.class);
                startActivity(draft);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onExpand(View handle, View content) {
        int val = Integer.parseInt(txtChildrenNo.getText().toString());

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
        new SweetAlertDialog(HouseHoldActivity.this,SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Details")
                .setContentText(v.getContentDescription().toString())
                .show();
    }


    void submit(){
        JSONObject jSurvey = new JSONObject();
        Context context = getApplicationContext();

        householdMember = txtHouseholdMember.getText().toString() + " " +
                txtSecond.getText().toString() + " " + txtThird.getText().toString();
        householdNumber = txtHouseMobile.getText().toString();
        childNum = txtChildrenNo.getText().toString();
        remarks = txtRemarks.getText().toString();
        second = txtSecond.getText().toString();
        third = txtSecond.getText().toString();

        if(villageId == null){
            Toast.makeText(context, "Select A village", Toast.LENGTH_LONG).show();
            return;
        }
        if(TextUtils.isEmpty(householdMember)){
            Toast.makeText(context, "Enter Household Number", Toast.LENGTH_LONG).show();
            txtHouseholdMember.setError("Required"); txtHouseholdMember.requestFocus(); return;
        }
        if(TextUtils.isEmpty(childNum)){
            Toast.makeText(context, "Enter number of children", Toast.LENGTH_LONG).show();
            txtChildrenNo.setError("Required"); txtChildrenNo.requestFocus(); return;
        }
        if(TextUtils.isEmpty(second)){
            Toast.makeText(context, "Enter Household Heads first name", Toast.LENGTH_LONG).show();
            txtSecond.setError("Required"); txtSecond.requestFocus(); return;
        }
        if(TextUtils.isEmpty(third)){
            Toast.makeText(context, "Enter Household Heads first name", Toast.LENGTH_LONG).show();
            txtThird.setError("Required"); txtThird.requestFocus(); return;
        }


        if(spnY_a.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.y_a_diabetes); return;
        }
        if(spnY_b.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.y_b_Cancer); return;
        }
        if(spnY_c.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.y_c_mental_illness); return;
        }
        if(spnY_d.getSelectedItemPosition() == 0){
            showInvalidMessage(R.string.y_d_hypertension); return;
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

        Log.i(TAG, CTAG + "jSurvey : " + jSurvey);

        try {
            householdInfo();
        } catch (Exception e) {
            e.printStackTrace();
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
     *
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }

    public void clearEditTexts(MaterialEditText[] editTexts){
        for(MaterialEditText m : editTexts){
            m.setText("");
        }
    }

    public void save(final Context context, final JSONObject details, final String tag){
        final ProgressDialog pDialog = new ProgressDialog(HouseHoldActivity.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        MUtil.showProgressDialog(pDialog);
        String url = "";

        url = MApplication.url + tag;

        //if(doLog)

        Log.i(MApplication.TAG, MainActivity.class.getName() + " : Details " + details.toString());
        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        if(doLog) Log.i(TAG, CTAG + response.toString());
                        Log.i(TAG, LoginActivity.class.getName() + "Response : " +response.toString() + "\nSuccess : " + success.isSuccess() + "\nMessage : " + success.getMessage());
                        MUtil.hideProgressDialog(pDialog);

                        if(success.isSuccess()){
                            Crouton.makeText(HouseHoldActivity.this , success.getMessage() , Style.CONFIRM).show();
                            if(success.isSuccess()){
                                Crouton.makeText(HouseHoldActivity.this , success.getMessage() , Style.CONFIRM).show();
                                int temp = success.getSurveyId();
                                if(temp > 0){
                                    survey = temp;
                                }else {
                                    survey = editSurveyId;
                                }

                                house_id = "KAM/"+ survey + "/017";

                                lblDisplayUser.setText("Household Survey ID: " + survey);

                                sharedPreference = new SharedPreference();

                                prefs = new SharedPreferences514();

                                prefs.save(getApplicationContext(), "", villageId,"", "", survey);

                                SharedPreferences sharedpreferences = getSharedPreferences(HOUSEHOLD_INFORMATION, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putInt("postpartum", postpartum);
                                editor.putInt("pregnant", pregnant);
                                editor.putInt("children", children);
                                editor.putInt("infants", infants);
                                editor.putInt("other_mothers", other_mothers);
                                editor.putInt("other_members", other_members);
                                editor.putInt("village", villageId);
                                editor.putInt("survey_id", survey);
                                editor.putInt("survey_status", survey_status);

                                sharedPreference.save(getApplicationContext(), postpartum, pregnant, children, infants, other_mothers, other_members,
                                        survey);

                                editor.apply();

                                Intent intent = new Intent(getApplicationContext(), HouseholdMembersActivity.class);
                                intent.putExtra("survey_id", survey);
                                intent.putExtra("house_id", house_id);
                                startActivity(intent);
                                finish();


                            }else {

                            }


                        }else{
                            Crouton.makeText(HouseHoldActivity.this , success.getMessage() , Style.ALERT).show();
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Crouton.makeText(HouseHoldActivity.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "survey");
    }


    public void showInvalidMessage(int message){
        new SweetAlertDialog(HouseHoldActivity.this,SweetAlertDialog.WARNING_TYPE)
                .setContentText("Select If " + getResources().getString(message)).show();
    }


    public void setSpinnerSelectedIndex(int index, Spinner[] spinners){
        for(Spinner s : spinners){
            s.setSelection(index);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HouseHoldActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Are you sure you want to exit?");
        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HouseHoldActivity.this.finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public void getArea(final Context context, final JSONObject details, final String whichArea){
        final ProgressDialog pDialog = new ProgressDialog(HouseHoldActivity.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);
        final Activity activity = HouseHoldActivity.this;
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
                        ArrayList<HashMap<String,String>> arrayListAreas;
                        if(success.isSuccess()){
                            Crouton.makeText(HouseHoldActivity.this , success.getMessage() , Style.CONFIRM).show();
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
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
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
                            Crouton.makeText(HouseHoldActivity.this , success.getMessage() , Style.ALERT).show();
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Crouton.makeText(HouseHoldActivity.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "getArea");
    }


    public void householdInfo() throws Exception{

        jBasicInfo = new JSONObject();

        jDeathInfo = new JSONObject();
        jHouseholdInfo = new JSONObject();
        jSurvey = new JSONObject();
        householdIllness = new JSONObject();

        postpartum = Integer.parseInt(txtMotherPm.getText().toString());
        pregnant = Integer.parseInt(txtMotherPg.getText().toString());
        children = Integer.parseInt(txtChildrenNo.getText().toString());
        infants = Integer.parseInt(txtMotherInf.getText().toString());
        other_mothers = Integer.parseInt(txtWomen.getText().toString());
        other_members = Integer.parseInt(txtOtherPeople.getText().toString());

        String nick = txtNickname.getText().toString();
        String land = txtLandmark.getText().toString();

        String members = postpartum + "-" + pregnant + "-" + children + "-"+ infants + "-" +
                other_mothers + "-" + other_members;

        String decTo = pregnant + "" + postpartum;
        int test = 0;
        String tag = null;
        house_id = "KAM/"+ editSurveyId + "/017";

        jBasicInfo.put("health_worker_id", session.getUser().getUserId());
        jBasicInfo.put("org_id", session.getUser().getOrgId());
        jBasicInfo.put("survey_id", editSurveyId);
        jBasicInfo.put("villageId", villageId);
        jBasicInfo.put("houseHoldNum", house_id);
        jBasicInfo.put("householdMember", householdMember);
        jBasicInfo.put("latitude", uLatitude);
        jBasicInfo.put("longitude", uLongitude);
        jBasicInfo.put("remarks", remarks);
        jBasicInfo.put("dssSelect", test);
        jBasicInfo.put("household_number", house_id);
        jBasicInfo.put("mobile", "");
        jBasicInfo.put("survey_status", survey_status);
        jBasicInfo.put("members", members);
        jBasicInfo.put("pre", decTo);
        jBasicInfo.put("nickname", nick);
        jBasicInfo.put("landmark", land);

        //death information
        jDeathInfo.put("1", ((Spinner) findViewById(R.id.spnAD_a)).getSelectedItemPosition() );
        jDeathInfo.put("2", ((Spinner) findViewById(R.id.spnAD_b)).getSelectedItemPosition() );
        jDeathInfo.put("3", ((Spinner) findViewById(R.id.spnAD_c)).getSelectedItemPosition() );
        jDeathInfo.put("4", ((Spinner) findViewById(R.id.spnAD_d)).getSelectedItemPosition() );
        jDeathInfo.put("5", ((EditText) findViewById(R.id.txtDeathInfoOther)).getText().toString());

        //household information
        jHouseholdInfo.put("1", ((Spinner) findViewById(R.id.spnAI)).getSelectedItemPosition());
        jHouseholdInfo.put("2", ((Spinner) findViewById(R.id.spnAJ)).getSelectedItemPosition());
        jHouseholdInfo.put("3", ((Spinner) findViewById(R.id.spnAK)).getSelectedItemPosition());

        householdIllness.put("10", ((Spinner) findViewById(R.id.spnY_a)).getSelectedItemPosition()-1 );
        householdIllness.put("11", ((Spinner) findViewById(R.id.spnY_b)).getSelectedItemPosition()-1 );
        householdIllness.put("12", ((Spinner) findViewById(R.id.spnY_c)).getSelectedItemPosition()-1 );
        householdIllness.put("13", ((Spinner) findViewById(R.id.spnY_d)).getSelectedItemPosition()-1 );
        householdIllness.put("14", (txtReferalIllnessOther.getText().toString()));

        jSurvey.put("basicInfo", jBasicInfo);
        jSurvey.put("deathInfo", jDeathInfo);
        jSurvey.put("householdIllness", householdIllness);
        jSurvey.put("householdInfo", jHouseholdInfo);

        if(searchJson != null){
            tag = "followUpUpdate";
        }else{
            tag = "survey";
        }

        int total = postpartum +pregnant + children + infants + other_members + other_mothers;

        if(total <= 0){

            Toast.makeText(getApplicationContext(), "The house must have at least one member", Toast.LENGTH_LONG).show();

        }else{
            save(getApplicationContext(), jSurvey, tag);
        }


    }

    public void search(View view) throws JSONException {

        int search = 0;
        MaterialEditText txtHouseRegId = (MaterialEditText) findViewById(R.id.txtHouseRegId);

        String searchVal = txtHouseRegId.getText().toString();

        if(searchVal != null){
            search = Integer.parseInt(searchVal);
        }

        getSearchResult(getApplicationContext(), search);
    }

    public void getSearchResult(final Context context, int search){

        JSONObject details = new JSONObject();

        try {
            details.put(User.ID, session.getUser().getUserId());
            details.put("search", search);
            details.put("app_version", MUtil.getAppVersionName(getApplicationContext()));

        } catch (JSONException e) {
            Log.e(TAG, LoginActivity.class.getName() + " : " + e.toString());
        }
        final ProgressDialog pDialog = new ProgressDialog(HouseHoldActivity.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        MUtil.showProgressDialog(pDialog);
        String url = MApplication.url + "getSearch";

        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        if(doLog) Log.i(TAG, CTAG + response.toString());
                        Log.i(TAG, LoginActivity.class.getName() + "Response : " +response.toString() + "\nSuccess : " + success.isSuccess() + "\nMessage : " + success.getMessage());
                        MUtil.hideProgressDialog(pDialog);

                        if(success.isSuccess()){
                            Crouton.makeText(HouseHoldActivity.this , success.getMessage() , Style.ALERT).show();
                            try{

                                JSONArray jsonSearch = response.getJSONArray("surveys");

                                searchJson = jsonSearch.toString();

                                //basic information
                                JSONObject basicInfo = jsonSearch.getJSONObject(0).getJSONObject("basicInfo");

                                survey_status = 4;

                                    villageId = basicInfo.getInt("village_id");
                                    editSurveyId = basicInfo.getInt("survey_id");
                                    members = basicInfo.getString("members");

                                    if (members.contains("-")) {

                                        String[] parts = members.split("-");
                                        postpartum = Integer.parseInt(parts[0]);
                                        pregnant = Integer.parseInt(parts[1]);
                                        children = Integer.parseInt(parts[2]);
                                        infants = Integer.parseInt(parts[3]);
                                        other_mothers = Integer.parseInt(parts[4]);
                                        other_members = Integer.parseInt(parts[5]);

                                        txtMotherPm.setText(postpartum + "");
                                        txtMotherPg.setText(pregnant + "");
                                        txtChildrenNo.setText(children + "");
                                        txtMotherInf.setText(infants + "");
                                        txtWomen.setText(other_mothers + "");
                                        txtOtherPeople.setText(other_members + "");

                                    } else {
                                        throw new IllegalArgumentException("Household " + members + " is empty");
                                    }

                                    spnSublocation.setVisibility(View.GONE);
                                    spnVillage.setVisibility(View.GONE);


                                radioVisit.setChecked(false);


                                //Toast.makeText(context, val, Toast.LENGTH_SHORT).show();

                            }catch (JSONException e){
                                Crouton.makeText(HouseHoldActivity.this, "Sorry, Could Not Create Session", Style.ALERT).show();
                            }

                        }else {
                            Crouton.makeText(HouseHoldActivity.this , success.getMessage() , Style.ALERT).show();
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Log.e(TAG , CTAG + error.getMessage());
                        Crouton.makeText(HouseHoldActivity.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                return super.getHeaders();
            }
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "getSearch");
    }
}



