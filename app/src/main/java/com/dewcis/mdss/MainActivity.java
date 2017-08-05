package com.dewcis.mdss;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.dewcis.mdss.utils.DraftActivity;
import com.dewcis.mdss.model.MSession;
import com.dewcis.mdss.model.Success;
import com.dewcis.mdss.utils.AboutDialog;
import com.dewcis.mdss.utils.ExpandablePanel;
import com.dewcis.mdss.utils.MUtil;
import com.dewcis.mdss.volley.VolleyErrorHelper;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


/**
 * Created by henriquedn on 4/14/15.
 * henrydkm@gmail.com
 * henry.maina@dewcis.com
 */

public class MainActivity extends ActionBarActivity implements ExpandablePanel.OnExpandListener{
    MSession session;
    String TAG = MApplication.TAG;
    static boolean doLog = MApplication.LOGDEBUG;
    static String CTAG = MainActivity.class.getName() + " : " ;
    boolean isEdit = false;
    int postpartum = 0, pregnant = 0, infants = 0, children = 0, other_women = 0, other_members
            ,var_postpartum, var_pregnant, var_mothers, var_members, var_children, var_infants;

    private SharedPreference sharedPreference;

    ExpandablePanel panelMotherInfo, panelChildInfo, panelReferralsInfo, panelDefaultersInfo;
    public MaterialEditText txtHouseMobile, txtHouseholdNum, txtHouseholdMember, txtRemarks,
            txtName;//txtVillageName
    TextView lblDisplayUser, lblReturnReason, lblReturnTitle;
    Button btnSubmit; int survey = 0;

    public static final String HOUSEHOLD_INFORMATION = "house_hold";

    //Basic Information
    Spinner spnSublocation, spnVillage;

    LinearLayout  mother_ln, child_in;
    SharedPreferences514 shared514;

    //mother information
    Spinner spnAgeGroup, spnPregnant , spnF, spnG, spnH, spnI, spnJ, spnK;
    //child information
    Spinner spnChildGender, spnL, spnM, spnN, spnO;
    //referral info
    Spinner spnP, spnQ, spnR, spnS, spnT, spnU, spnV, spnW, spnX;
    //defaulters info
    Spinner spnZ, spnAA, spnAB, spnAC, spnAL;

    int category;
    int survey_status = 0;

    JSONObject jMotherInfo = new JSONObject();
    JSONObject jReferralInfo = new JSONObject();
    JSONObject jDefaultersInfo = new JSONObject();
    JSONObject jChildInfo = new JSONObject();

    String draft_key = null;
    int draft_index = 0;
    private boolean isDraft;
    private JSONObject draftedJson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new MSession(getApplicationContext());

        if(!session.isLoggedIn()){
            session.logout(this);
        }

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            category = extras.getInt("type");
        }

        lblDisplayUser = (TextView) findViewById(R.id.lblDisplayUser);
        lblReturnReason = (TextView) findViewById(R.id.lblReturnReason);
        lblReturnTitle = (TextView) findViewById(R.id.lblReturnTitle);

        child_in = (LinearLayout) findViewById(R.id.child_info);
        mother_ln = (LinearLayout) findViewById(R.id.mother_info);
        lblReturnTitle.setVisibility(View.GONE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));
        actionBar.setSubtitle("MOH 514");
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setHomeButtonEnabled(false);

        lblReturnReason.setText("");

        panelMotherInfo = (ExpandablePanel) findViewById(R.id.panelMotherInfo);
        panelChildInfo = (ExpandablePanel) findViewById(R.id.panelChildInfo);
        panelReferralsInfo = (ExpandablePanel) findViewById(R.id.panelReferralsInfo);
        panelDefaultersInfo = (ExpandablePanel) findViewById(R.id.panelDefaultersInfo);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        panelMotherInfo.setOnExpandListener(this);
        panelChildInfo.setOnExpandListener(this);
        panelReferralsInfo.setOnExpandListener(this);
        panelDefaultersInfo.setOnExpandListener(this);

        spnSublocation  = (Spinner) findViewById(R.id.spnSublocation);
        spnVillage  = (Spinner) findViewById(R.id.spnVillage);

        txtHouseholdNum = (MaterialEditText) findViewById(R.id.txtHouseholdNum);
        txtHouseholdMember = (MaterialEditText) findViewById(R.id.txtHouseholdMember);
        txtRemarks = (MaterialEditText) findViewById(R.id.txtRemarks);
        txtHouseMobile = (MaterialEditText) findViewById(R.id.txtHouseholdMobile);
        txtName = (MaterialEditText) findViewById(R.id.txtName);

        txtName.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        //mother information
        spnAgeGroup =  (Spinner) findViewById(R.id.spnAgeGroup);
        spnPregnant =  (Spinner) findViewById(R.id.spnPregnant);
        spnF =  ((Spinner) findViewById(R.id.spnF));
        spnG =  (Spinner) findViewById(R.id.spnG);
        spnH =  (Spinner) findViewById(R.id.spnH);
        spnI =  (Spinner) findViewById(R.id.spnI);
        spnJ =  (Spinner) findViewById(R.id.spnJ);
        spnK =  (Spinner) findViewById(R.id.spnK);

        //child information
//        spnAgeDays =  (Spinner) findViewById(R.id.spnAgeDays);
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

        if(category == 0){
            lblDisplayUser.setText("Category: Postpartum Mother");
            child_in.setVisibility(View.GONE);
            autoSelectionPostpartum();
        }else if(category == 1){
            lblDisplayUser.setText("Category: Pregnant Mother");
            child_in.setVisibility(View.GONE);
            autoSelectionPregnant();
        }else if(category == 2){
            lblDisplayUser.setText("Category: Child Below 5 years");
            mother_ln.setVisibility(View.GONE);
            autoSelectionChild();
        }else if(category == 3){
            lblDisplayUser.setText("Category: New born");
            mother_ln.setVisibility(View.GONE);
            autoSelectionInfant();
        }else if(category == 4){
            lblDisplayUser.setText("Category: Other mothers");
            child_in.setVisibility(View.GONE);
            autoSelectionOtherWomen();
        }else{
            lblDisplayUser.setText("Category: Other Members");
            child_in.setVisibility(View.GONE);
            mother_ln.setVisibility(View.GONE);
            autoSelectionOther();
        }

        SharedPreferences sharedpreferences = getSharedPreferences(DssActivity.DSS_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove("age");
        editor.remove("mobile");
        editor.apply();

        draft();

    }

    private void draft() {

        Bundle extras = getIntent().getExtras();

        if(extras != null){
            draft_key = extras.getString("draft_key");
            draft_index = extras.getInt("draft_index");
        }

        if(draft_key != null){

            JSONObject logic;
            String trace = getPreferences(Context.MODE_PRIVATE).getString(draft_key, "EMPTY");
            DraftActivity draftActivity = new DraftActivity();

            //clearIndexedDraft(draft_key, draft_index);

            if(!trace.equals("EMPTY")) {
                logic = draftActivity.showDraft(MainActivity.this, trace);
                //Toast.makeText(getApplicationContext(), logic.toString(), Toast.LENGTH_LONG).show();
                if(logic != null ){
                    isDraft = true;

                    try {

                        if(category == 2 || category == 3){

                            JSONObject childInfo = logic.getJSONObject("childInfo");

                            if(!childInfo.toString().equals("{}")){

                                spnChildGender.setSelection(childInfo.getInt("1") - 3);
                                spnL.setSelection(childInfo.getInt("2"));
                                spnM.setSelection(childInfo.getInt("3"));
                                spnN.setSelection(childInfo.getInt("4"));
                                spnO.setSelection(childInfo.getInt("5"));
                                txtName.setText(childInfo.getString("6"));

                            }
                        }
                        JSONObject referralInfo = logic.getJSONObject("referralInfo");
                        JSONObject defaultersInfo = logic.getJSONObject("defaultersInfo");

                        if(category == 0 || category == 1 || category == 4){
                            JSONObject motherInfo = logic.getJSONObject("motherInfo");

                            if(!motherInfo.toString().equals("{}")){

                                spnPregnant.setSelection(motherInfo.getInt("1"));
                                spnF.setSelection(motherInfo.getInt("2"));
                                spnG.setSelection(motherInfo.getInt("3"));
                                spnH.setSelection(motherInfo.getInt("4"));
                                spnI.setSelection(motherInfo.getInt("5"));
                                spnJ.setSelection(motherInfo.getInt("6"));
                                spnK.setSelection(motherInfo.getInt("7"));
                                spnAgeGroup.setSelection(motherInfo.getInt("8"));
                                txtName.setText(motherInfo.getString("9"));

                            }
                        }


                        spnP.setSelection(referralInfo.getInt("1"));
                        spnQ.setSelection(referralInfo.getInt("2"));
                        spnR.setSelection(referralInfo.getInt("3"));
                        spnS.setSelection(referralInfo.getInt("4"));
                        spnT.setSelection(referralInfo.getInt("5"));
                        spnU.setSelection(referralInfo.getInt("6"));
                        spnV.setSelection(referralInfo.getInt("7"));
                        spnW.setSelection(referralInfo.getInt("8"));
                        spnX.setSelection(referralInfo.getInt("9"));

                        spnZ.setSelection(defaultersInfo.getInt("1"));
                        spnAA.setSelection(defaultersInfo.getInt("2"));
                        spnAB.setSelection(defaultersInfo.getInt("3"));
                        spnAC.setSelection(defaultersInfo.getInt("4"));
                        spnAL.setSelection(defaultersInfo.getInt("5"));

                        setDraftedJson(logic);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        }


    }

    private void setDraftedJson(JSONObject draftedJson) {
        this.draftedJson = draftedJson;
    }


    @Override
    protected void onResume() {
        super.onResume();
        //this.doubleBackToExitPressedOnce = false;

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

    public void showDescription(View v) {
        //Toast.makeText(getApplicationContext(), v.getContentDescription().toString(), Toast.LENGTH_SHORT).show();
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Details")
                .setContentText(v.getContentDescription().toString())
                .show();
    }

    private void clearIndexedDraft(String key, int index){
        DraftActivity.removeDraft(MainActivity.this, index);
        SharedPreference.removeValue(getApplicationContext(), key);
    }

    public void clearEditTexts(MaterialEditText[] editTexts) {
        for(MaterialEditText m : editTexts){
            m.setText("");
        }
    }

    public void save(final Context context, final JSONObject details){

        String url = "";
        if(isEdit){
            url = MApplication.url + "editSurvey";
        }else{
            url = MApplication.url + "survey_category";
        }

        Log.i(MApplication.TAG, MainActivity.class.getName() + " : Details " + details.toString());
        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        if(doLog) Log.i(TAG, CTAG + response.toString());
                        Log.i(TAG, LoginActivity.class.getName() + "Response : " + response.toString() + "\nSuccess : " + success.isSuccess() + "\nMessage : " + success.getMessage());
                        Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                        if(success.isSuccess()){

                            if(isDraft){
                                clearIndexedDraft(draft_key ,draft_index);
                            }
                            Crouton.makeText(MainActivity.this , success.getMessage() , Style.CONFIRM).show();
                            if((category == 2 && children !=0 ) || (category == 5 && other_members != 0) ||
                                    (category == 4 && other_women != 0)){
                                restart();
                            }
                            finish();

                        }else{
                            Crouton.makeText(MainActivity.this , success.getMessage() , Style.ALERT).show();
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        Crouton.makeText(MainActivity.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "survey");
    }

    public void showInvalidMessage(int message){
        new SweetAlertDialog(MainActivity.this,SweetAlertDialog.WARNING_TYPE)
                .setContentText("Select If " + getResources().getString(message)).show();
    }


    public void setSpinnerSelectedIndex(int index, Spinner[] spinners){
        for(Spinner s : spinners){
            s.setSelection(index);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Finish collecting data for all children?");
        builder.setNegativeButton("Okay", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void mothersInformation() throws JSONException {

        final JSONObject jSurveyMother = new JSONObject();
        final JSONObject categoryInfo = new JSONObject();

        if (isDraft) {
            Intent intent = new Intent();
            if(category == 0){
                postpartum ++;
                intent = new Intent(getApplicationContext(), DangerSignsActivity.class);
            }else if(category == 1){
                pregnant ++;
                intent = new Intent(getApplicationContext(), DangerSignsActivity.class);
            }else if(category == 2){
                children++;
                intent = new Intent(getApplicationContext(), Home.class);
            }else if(category == 3){
                infants++;
                intent = new Intent(getApplicationContext(), DangerSignsBaby.class);
            }else if(category == 4){
                other_members++;
                intent = new Intent(getApplicationContext(), Home.class);
            }

            if (MUtil.isNetworkConnected(getApplicationContext())) {
                save(getApplicationContext(), getDraftedJson());
                intent.putExtra("type", category);
                intent.putExtra("pre", "514");
            } else {
                //Change to updateDraft
                DraftActivity.makeDraft(MainActivity.this, jSurveyMother, draft_key);
                intent = new Intent(getApplicationContext(), HouseholdMembersActivity.class);
            }
            startActivity(intent);

        } else {

            loadSharedPrefs();

            int number = 0;

            try {
                prefill();

                jMotherInfo.put("1", spnPregnant.getSelectedItemId());
                jMotherInfo.put("2", spnF.getSelectedItemId());
                jMotherInfo.put("3", spnG.getSelectedItemId());
                jMotherInfo.put("4", spnH.getSelectedItemId());
                jMotherInfo.put("5", spnI.getSelectedItemId());
                jMotherInfo.put("6", spnJ.getSelectedItemId());
                jMotherInfo.put("7", spnK.getSelectedItemId());
                jMotherInfo.put("8", spnAgeGroup.getSelectedItemId());
                jMotherInfo.put("9", txtName.getText().toString());

                jChildInfo.put("1", ((Spinner) findViewById(R.id.spnChildGender)).getSelectedItemPosition() + 3);
                jChildInfo.put("2", ((Spinner) findViewById(R.id.spnL)).getSelectedItemId());
                jChildInfo.put("3", ((Spinner) findViewById(R.id.spnM)).getSelectedItemPosition());
                jChildInfo.put("4", ((Spinner) findViewById(R.id.spnN)).getSelectedItemPosition());
                jChildInfo.put("5", ((Spinner) findViewById(R.id.spnO)).getSelectedItemPosition());
                jChildInfo.put("6", txtName.getText().toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (category == 0 && postpartum != 0) {

                postpartum = postpartum - 1;

                number = var_postpartum - postpartum;
                try {
                    categoryInfo.put("type", category);
                    categoryInfo.put("number", number);
                    categoryInfo.put("survey_id", survey);
                    categoryInfo.put("survey_status", survey_status);
                    jSurveyMother.put("categoryInfo", categoryInfo);
                    jSurveyMother.put("motherInfo", jMotherInfo);
                    jSurveyMother.put("referralInfo", jReferralInfo);
                    jSurveyMother.put("defaultersInfo", jDefaultersInfo);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                save(getApplicationContext(), jSurveyMother);

                Intent intent;

                if (MUtil.isNetworkConnected(getApplicationContext())) {
                    save(getApplicationContext(), jSurveyMother);
                    intent = new Intent(getApplicationContext(), DangerSignsActivity.class);
                } else {
//                       postpartum = postpartum + 1;
                    DraftActivity.makeDraft(MainActivity.this, jSurveyMother, "postpartum" + "" + survey + "" + number);
                    intent = new Intent(getApplicationContext(), HouseholdMembersActivity.class);
                }
                intent.putExtra("type", 0);
                intent.putExtra("pre", "514");
                startActivity(intent);

                if (!isDraft) {
                    sharedPreference.save(getApplicationContext(), postpartum, pregnant, children,
                            infants, other_women, other_members, survey);
                }

            } else if (category == 1 && pregnant != 0) {

                pregnant = pregnant - 1;

                number = var_pregnant - pregnant;

                try {
                    categoryInfo.put("type", category);
                    categoryInfo.put("number", number);
                    categoryInfo.put("survey_id", survey);
                    categoryInfo.put("survey_status", survey_status);
                    jSurveyMother.put("categoryInfo", categoryInfo);
                    jSurveyMother.put("motherInfo", jMotherInfo);
                    jSurveyMother.put("referralInfo", jReferralInfo);
                    jSurveyMother.put("defaultersInfo", jDefaultersInfo);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent;

                if (MUtil.isNetworkConnected(getApplicationContext())) {
                    save(getApplicationContext(), jSurveyMother);
                    intent = new Intent(getApplicationContext(), DangerSignsActivity.class);
                } else {
                    DraftActivity.makeDraft(MainActivity.this, jSurveyMother, "pregnant" + "" + survey + "" + number);
                    intent = new Intent(getApplicationContext(), HouseholdMembersActivity.class);
                }
                intent.putExtra("type", 1);
                intent.putExtra("pre", "514");
                startActivity(intent);

                if (!isDraft) {
                    sharedPreference.save(getApplicationContext(), postpartum, pregnant, children,
                            infants, other_women, other_members, survey);
                }

            } else if (category == 4 && other_women != 0) {

                other_women = other_women - 1;

                number = var_mothers - other_women;

                try {
                    categoryInfo.put("type", category);
                    categoryInfo.put("number", number);
                    categoryInfo.put("survey_id", survey);
                    categoryInfo.put("survey_status", survey_status);
                    jSurveyMother.put("categoryInfo", categoryInfo);
                    jSurveyMother.put("motherInfo", jMotherInfo);
                    jSurveyMother.put("referralInfo", jReferralInfo);
                    jSurveyMother.put("defaultersInfo", jDefaultersInfo);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (MUtil.isNetworkConnected(getApplicationContext())) {
                    save(getApplicationContext(), jSurveyMother);
                } else {
                    DraftActivity.makeDraft(MainActivity.this, jSurveyMother, "other_mothers" + "" + survey + "" + number);
                    Intent intent = new Intent(getApplicationContext(), HouseholdMembersActivity.class);
                    startActivity(intent);
                }

                if (other_women == 0) {

                    btnSubmit.setText("Continue");
                    sharedPreference.save(getApplicationContext(), postpartum, pregnant, children,
                            infants, other_women, other_members, survey);
                    Intent intent = new Intent(getApplicationContext(), HouseholdMembersActivity.class);
                    startActivity(intent);
                }

                if (!isDraft) {
                    sharedPreference.save(getApplicationContext(), postpartum, pregnant, children,
                            infants, other_women, other_members, survey);
                }

            } else if (category == 5 && other_members != 0) {

                other_members = other_members - 1;

                number = var_members - other_members;

                try {
                    categoryInfo.put("type", category);
                    categoryInfo.put("number", number);
                    categoryInfo.put("survey_id", survey);
                    categoryInfo.put("survey_status", survey_status);
                    jSurveyMother.put("categoryInfo", categoryInfo);
                    jSurveyMother.put("referralInfo", jReferralInfo);
                    jSurveyMother.put("defaultersInfo", jDefaultersInfo);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (MUtil.isNetworkConnected(getApplicationContext())) {
                    save(getApplicationContext(), jSurveyMother);
                } else {
                    DraftActivity.makeDraft(MainActivity.this, jSurveyMother, "other_members" + "" + survey + "" + number);
                    Intent intent = new Intent(getApplicationContext(), HouseholdMembersActivity.class);
                    startActivity(intent);
                }

                if (other_members == 0) {
                    btnSubmit.setText("Continue");
                    sharedPreference.save(getApplicationContext(), postpartum, pregnant, children,
                            infants, other_women, other_members, survey);
                    Intent intent = new Intent(getApplicationContext(), HouseholdMembersActivity.class);
                    startActivity(intent);
                }

                if (!isDraft) {
                    sharedPreference.save(getApplicationContext(), postpartum, pregnant, children,
                            infants, other_women, other_members, survey);
                }


            } else if (category == 2 && children != 0) {

                children = children - 1;

                number = var_children - children;

                try {
                    prefill();

                    categoryInfo.put("survey_id", survey);
                    categoryInfo.put("type", 2);
                    categoryInfo.put("number", number);
                    categoryInfo.put("survey_status", survey_status);
                    jSurveyMother.put("categoryInfo", categoryInfo);
                    jSurveyMother.put("childInfo", jChildInfo);
                    jSurveyMother.put("referralInfo", jReferralInfo);
                    jSurveyMother.put("defaultersInfo", jDefaultersInfo);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (MUtil.isNetworkConnected(getApplicationContext())) {
                    save(getApplicationContext(), jSurveyMother);
                } else {
                    DraftActivity.makeDraft(MainActivity.this, jSurveyMother, "children" + "" + survey + "" + number);
                    Intent intent = new Intent(getApplicationContext(), HouseholdMembersActivity.class);
                    startActivity(intent);
                }

                if (children == 0) {
                    btnSubmit.setText("Continue");
                    sharedPreference.save(getApplicationContext(), postpartum, pregnant, children,
                            infants, other_women, other_members, survey);
                    Intent intent = new Intent(getApplicationContext(), HouseholdMembersActivity.class);
                    startActivity(intent);
                }

                if (!isDraft) {
                    sharedPreference.save(getApplicationContext(), postpartum, pregnant, children,
                            infants, other_women, other_members, survey);
                }

            } else if (category == 3 && infants != 0) {

                infants = infants - 1;

                number = var_infants - infants;

                try {
                    categoryInfo.put("type", category);
                    categoryInfo.put("number", number);
                    categoryInfo.put("survey_id", survey);
                    categoryInfo.put("survey_status", survey_status);
                    jSurveyMother.put("categoryInfo", categoryInfo);
                    jSurveyMother.put("childInfo", jChildInfo);
                    jSurveyMother.put("referralInfo", jReferralInfo);
                    jSurveyMother.put("defaultersInfo", jDefaultersInfo);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent intent;

                if (MUtil.isNetworkConnected(getApplicationContext())) {
                    save(getApplicationContext(), jSurveyMother);
                    intent = new Intent(getApplicationContext(), DangerSignsBaby.class);
                } else {
                    DraftActivity.makeDraft(MainActivity.this, jSurveyMother, "newborns" + "" + survey + "" + number);
                    Toast.makeText(getApplicationContext(), jChildInfo.toString(), Toast.LENGTH_LONG).show();
                    intent = new Intent(getApplicationContext(), HouseholdMembersActivity.class);
                }

                if (!isDraft) {
                    sharedPreference.save(getApplicationContext(), postpartum, pregnant, children,
                            infants, other_women, other_members, survey);
                }

                intent.putExtra("type", 3);
                intent.putExtra("pre", "514");
                startActivity(intent);
            }


        }


    }

    private void loadSharedPrefs() {

        sharedPreference = new SharedPreference();
        shared514 = new SharedPreferences514();

        int village = shared514.getValue(getApplicationContext(), "village");
        shared514.save(getApplicationContext(), txtName.getText().toString(), village,"", "", survey);

        survey = sharedPreference.getValue(getApplicationContext(), "survey_id");
        postpartum = sharedPreference.getValue(getApplicationContext(), "postpartum");
        pregnant = sharedPreference.getValue(getApplicationContext(), "pregnant");
        other_women = sharedPreference.getValue(getApplicationContext(), "other_mothers");
        other_members = sharedPreference.getValue(getApplicationContext(), "other_members");
        children = sharedPreference.getValue(getApplicationContext(), "children");
        infants = sharedPreference.getValue(getApplicationContext(), "infant");

        final SharedPreferences sharedpreferences = getSharedPreferences(HouseHoldActivity.HOUSEHOLD_INFORMATION , Context.MODE_PRIVATE);
        var_postpartum = sharedpreferences.getInt("postpartum", 0);
        var_pregnant = sharedpreferences.getInt("pregnant", 0);
        var_mothers = sharedpreferences.getInt("other_mothers", 0);
        var_members = sharedpreferences.getInt("other_members", 0);
        var_children = sharedpreferences.getInt("children", 0);
        var_infants = sharedpreferences.getInt("infants", 0);
        survey_status = sharedpreferences.getInt("survey_status", 0);

    }

    public void newClick(View view) {

        if(TextUtils.isEmpty(txtName.getText().toString())){
            Toast.makeText(getApplicationContext(), "Enter members name", Toast.LENGTH_LONG).show();
            txtName.setError("Required"); txtName.requestFocus(); return;
        }

        if(category == 0 || category == 1 || category == 4){

            //mother information
            //spnPregnant , spnF, spnG, spnH, spnI, spnJ, spnK;

            if(spnPregnant.getSelectedItemPosition() == 0){
                showInvalidMessage(R.string.pregnant); return;
            }
            if(spnF.getSelectedItemPosition() == 0){
                showInvalidMessage(R.string.f_name); return;
            }
            if(spnG.getSelectedItemPosition() == 0){
                showInvalidMessage(R.string.g_name); return;
            }
            if(spnH.getSelectedItemPosition() == 0){
                showInvalidMessage(R.string.h_name); return;
            }
            if(spnI.getSelectedItemPosition() == 0){
                showInvalidMessage(R.string.i_name); return;
            }
            if(spnJ.getSelectedItemPosition() == 0){
                showInvalidMessage(R.string.j_name); return;
            }
            if(spnK.getSelectedItemPosition() == 0){
                showInvalidMessage(R.string.k_name); return;
            }
        }

        if(category == 2 || category == 3){

            //child information
            //spnChildGender, spnL, spnM, spnN, spnO;
            if(spnChildGender.getSelectedItemPosition() == 0){
                showInvalidMessage(R.string.gender); return;
            }
            if(spnL.getSelectedItemPosition() == 0){
                showInvalidMessage(R.string.l_name); return;
            }
            if(spnM.getSelectedItemPosition() == 0){
                showInvalidMessage(R.string.m_name); return;
            }
            if(spnN.getSelectedItemPosition() == 0){
                showInvalidMessage(R.string.n_name); return;
            }
            if(spnO.getSelectedItemPosition() == 0){
                showInvalidMessage(R.string.o_name); return;
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

        try {
            mothersInformation();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void autoSelectionInfant(){

        spnM.setVisibility(View.GONE);spnM.setSelection(3);spnN.setVisibility(View.GONE);
        spnN.setSelection(3);spnO.setVisibility(View.GONE);spnO.setSelection(3);spnP.setVisibility(View.GONE);
        spnP.setSelection(3);spnQ.setVisibility(View.GONE);spnQ.setSelection(3);spnR.setVisibility(View.GONE);
        spnR.setSelection(3);spnT.setVisibility(View.GONE);spnT.setSelection(3);spnU.setVisibility(View.GONE);
        spnU.setSelection(3);spnZ.setVisibility(View.GONE);spnZ.setSelection(3);spnAA.setVisibility(View.GONE);
        spnAA.setSelection(3);spnX.setVisibility(View.GONE);spnX.setSelection(3);
    }

    public void autoSelectionChild(){

        spnP.setVisibility(View.GONE);spnP.setSelection(3);spnQ.setVisibility(View.GONE);
        spnQ.setSelection(3);spnR.setVisibility(View.GONE);spnR.setSelection(3);spnS.setVisibility(View.GONE);
        spnS.setSelection(3);spnV.setVisibility(View.GONE);spnV.setSelection(3);spnW.setVisibility(View.GONE);
        spnW.setSelection(3);spnX.setVisibility(View.GONE);spnX.setSelection(3);spnZ.setVisibility(View.GONE);
        spnZ.setSelection(3);
    }
    public void autoSelectionPregnant(){
        spnG.setVisibility(View.GONE);spnG.setSelection(3);spnH.setVisibility(View.GONE);
        spnH.setSelection(3);spnI.setVisibility(View.GONE);spnI.setSelection(3);spnJ.setVisibility(View.GONE);
        spnJ.setSelection(3);spnK.setVisibility(View.GONE);spnK.setSelection(3);spnR.setVisibility(View.GONE);
        spnR.setSelection(3);spnS.setVisibility(View.GONE);spnS.setSelection(3);spnT.setVisibility(View.GONE);
        spnT.setSelection(3);spnU.setVisibility(View.GONE);spnU.setSelection(3);spnX.setVisibility(View.GONE);
        spnX.setSelection(3);spnAA.setVisibility(View.GONE);spnAA.setSelection(3);spnAL.setVisibility(View.GONE);
        spnAL.setSelection(3);
    }

    public void autoSelectionPostpartum(){
        spnP.setVisibility(View.GONE);spnP.setSelection(3);spnQ.setVisibility(View.GONE);
        spnQ.setSelection(3);spnU.setVisibility(View.GONE);spnU.setSelection(3);spnT.setVisibility(View.GONE);
        spnT.setSelection(3);spnPregnant.setVisibility(View.GONE);spnPregnant.setSelection(3);spnF.setVisibility(View.GONE);
        spnF.setSelection(3);spnZ.setVisibility(View.GONE);spnZ.setSelection(3);spnAA.setVisibility(View.GONE);
        spnAA.setSelection(3);spnAL.setVisibility(View.GONE);spnAL.setSelection(3);
    }

    private void autoSelectionOtherWomen() {
        spnPregnant.setVisibility(View.GONE);spnPregnant.setSelection(3);spnF.setVisibility(View.GONE);
        spnF.setSelection(3);spnG.setVisibility(View.GONE);spnG.setSelection(3);

        spnH.setVisibility(View.GONE);spnH.setSelection(3);spnI.setVisibility(View.GONE);spnI.setSelection(3);
        spnJ.setVisibility(View.GONE);spnJ.setSelection(3);spnP.setVisibility(View.GONE);spnP.setSelection(3);

        spnQ.setVisibility(View.GONE);spnQ.setSelection(3);spnS.setVisibility(View.GONE);spnS.setSelection(3);
        spnT.setVisibility(View.GONE);spnT.setSelection(3);spnU.setVisibility(View.GONE);spnU.setSelection(3);

        spnZ.setVisibility(View.GONE);spnZ.setSelection(3);spnAA.setVisibility(View.GONE);spnAA.setSelection(3);
        spnAL.setVisibility(View.GONE);spnAL.setSelection(3);
    }

    private void autoSelectionOther() {
        spnP.setVisibility(View.GONE);
        spnP.setSelection(3);

        spnQ.setVisibility(View.GONE);
        spnQ.setSelection(3);

        spnS.setVisibility(View.GONE);
        spnS.setSelection(3);

        spnT.setVisibility(View.GONE);
        spnT.setSelection(3);

        spnU.setVisibility(View.GONE);
        spnU.setSelection(3);

        spnV.setVisibility(View.GONE);
        spnV.setSelection(3);

        spnZ.setVisibility(View.GONE);
        spnZ.setSelection(3);

        spnAA.setVisibility(View.GONE);
        spnAA.setSelection(3);

        spnAL.setVisibility(View.GONE);
        spnAL.setSelection(3);
    }

    private void prefill() throws JSONException {

        jReferralInfo.put("1", ((Spinner) findViewById(R.id.spnP)).getSelectedItemId());
        jReferralInfo.put("2", ((Spinner) findViewById(R.id.spnQ)).getSelectedItemPosition());
        jReferralInfo.put("3", ((Spinner) findViewById(R.id.spnR)).getSelectedItemPosition());
        jReferralInfo.put("4", ((Spinner) findViewById(R.id.spnS)).getSelectedItemPosition());
        jReferralInfo.put("5", ((Spinner) findViewById(R.id.spnT)).getSelectedItemPosition());
        jReferralInfo.put("6", ((Spinner) findViewById(R.id.spnU)).getSelectedItemPosition());
        jReferralInfo.put("7", ((Spinner) findViewById(R.id.spnV)).getSelectedItemPosition());
        jReferralInfo.put("8", ((Spinner) findViewById(R.id.spnW)).getSelectedItemPosition());
        jReferralInfo.put("9", ((Spinner) findViewById(R.id.spnX)).getSelectedItemPosition());

        jDefaultersInfo.put("1", ((Spinner) findViewById(R.id.spnZ)).getSelectedItemPosition());
        jDefaultersInfo.put("2", ((Spinner) findViewById(R.id.spnAA)).getSelectedItemPosition());
        jDefaultersInfo.put("3", ((Spinner) findViewById(R.id.spnAB)).getSelectedItemPosition());
        jDefaultersInfo.put("4", ((Spinner) findViewById(R.id.spnAC)).getSelectedItemPosition());
        jDefaultersInfo.put("5", ((Spinner) findViewById(R.id.spnAL)).getSelectedItemPosition());
    }

    private void restart(){
        Intent intent = getIntent();
        finish();
        startActivity(intent);

        Toast.makeText(getApplicationContext(), "Enter next members information", Toast.LENGTH_LONG).show();
    }

    public JSONObject getDraftedJson() {
        return draftedJson;
    }
}
