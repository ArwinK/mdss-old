package com.dewcis.mdss;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import com.dewcis.mdss.utils.DraftActivity;
import com.dewcis.mdss.SharedPreferences.SharedPreference;
import com.dewcis.mdss.model.Success;
import com.dewcis.mdss.utils.ExpandablePanel;
import com.dewcis.mdss.utils.MUtil;
import com.dewcis.mdss.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class DangerSignsActivity extends ActionBarActivity implements ExpandablePanel.OnExpandListener, RadioGroup.OnCheckedChangeListener {
    String TAG = MApplication.TAG;
    static String CTAG = Form513Activity.class.getName() + " : ";
    static boolean doLog = MApplication.LOGDEBUG;

    ExpandablePanel panelSectionOne, panelSectionTwo, panelSectionThree, panelSectionFour,
            panelSectionFive, panelSectionSix, panelPostpartumHistory, panelPregnancyHistory;
    Spinner b_nS, b_hS, b_vS, e_wS, s_hS, v_S, c_S, c_cS, f_S, d_sS, a_sS, b_bS, u_rS, u_nS,
            p_sP, g_uS, n_mS, a_dX, b_aX, f_cX, f_nX, l_pX, l_cX, h_bX, h_dX, b_vX, d_bX,
            p_uX, p_fX, w_bX, a_lX, p_sX, f_sX, t_bS, f_bS, p_aS, f_sP, m_sP, a_sV, p_fA;
    RadioGroup motherGroup;
    String motherTypeValue = "", mobile100;
    String whereTo = "survey";
    String my514 = "";
    boolean toDss = true;
    LinearLayout ln, ln_a, ln_btn;
    TextView lblDisplay;
    int postpartum, pregnant, number, survey;
    SharedPreference sharedPreference;
    int index, type;
    int survey_status = 0;
    private boolean isDraft;
    private String draft_key;
    private int draft_index;
    private JSONObject draftedJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danger_signs);

        Toast.makeText(getApplicationContext(), "Fill in Dss survey (Mothers)", Toast.LENGTH_LONG).show();

        householdMothersInfo();

        //tracePref();

        b_nS = ((Spinner) findViewById(R.id.bleeding_vagina));
        b_hS = ((Spinner) findViewById(R.id.bad_headache));
        b_vS = ((Spinner) findViewById(R.id.blurred_vision));
        e_wS = ((Spinner) findViewById(R.id.eWeightLoss));
        s_hS = ((Spinner) findViewById(R.id.swelling_hands));
        v_S = ((Spinner) findViewById(R.id.vomiting));
        c_S = ((Spinner) findViewById(R.id.consciousness));
        f_S = ((Spinner) findViewById(R.id.fever));
        d_sS = ((Spinner) findViewById(R.id.discharge));
        c_cS = ((Spinner) findViewById(R.id.convulsion));
        a_sS = ((Spinner) findViewById(R.id.abdominal));
        b_bS = ((Spinner) findViewById(R.id.breathing));
        u_rS = ((Spinner) findViewById(R.id.urination));
        u_nS = ((Spinner) findViewById(R.id.urinate));
        p_sP = ((Spinner) findViewById(R.id.pale_pams));
        g_uS = ((Spinner) findViewById(R.id.genital_ulcers));
        n_mS = ((Spinner) findViewById(R.id.not_moving));
        f_sP = ((Spinner) findViewById(R.id.first_pregnancy));
        m_sP = ((Spinner) findViewById(R.id.months_pregnant));
        a_sV = ((Spinner) findViewById(R.id.anc_visit));
        p_fA = ((Spinner) findViewById(R.id.pregnant_first_anc));

        a_dX = ((Spinner) findViewById(R.id.abdominal_discharge));
        b_aX = ((Spinner) findViewById(R.id.bad_abdominal));
        f_cX = ((Spinner) findViewById(R.id.fever_chills));
        f_nX = ((Spinner) findViewById(R.id.fever_nochills));
        h_bX = ((Spinner) findViewById(R.id.high_blood));
        l_pX = ((Spinner) findViewById(R.id.labor_pains));
        l_cX = ((Spinner) findViewById(R.id.loss_consciousness));
        h_dX = ((Spinner) findViewById(R.id.head_dizzy));
        b_vX = ((Spinner) findViewById(R.id.blurred_visionPm));
        d_bX = ((Spinner) findViewById(R.id.diff_breathing));
        p_uX = ((Spinner) findViewById(R.id.pass_urine));
        p_fX = ((Spinner) findViewById(R.id.palm_feet));
        w_bX = ((Spinner) findViewById(R.id.water_break));
        a_lX = ((Spinner) findViewById(R.id.arms_legs));
        p_sX = ((Spinner) findViewById(R.id.placenta));
        f_sX = ((Spinner) findViewById(R.id.foul_smell));
        t_bS = ((Spinner) findViewById(R.id.time_birth));
        f_bS = ((Spinner) findViewById(R.id.first_baby));
        p_aS = ((Spinner) findViewById(R.id.pnc_attended));


        motherGroup = (RadioGroup) findViewById(R.id.postPartumOrPregnant);

        panelSectionOne = (ExpandablePanel) findViewById(R.id.panelSectionOne);
        panelSectionTwo = (ExpandablePanel) findViewById(R.id.panelSectionTwo);
        panelSectionThree = (ExpandablePanel) findViewById(R.id.panelSectionThree);
        panelSectionFour = (ExpandablePanel) findViewById(R.id.panelSectionFour);
        panelSectionFive = (ExpandablePanel) findViewById(R.id.panelSectionFive);
        panelSectionSix = (ExpandablePanel) findViewById(R.id.panelSectionSix);
        panelPostpartumHistory = (ExpandablePanel) findViewById(R.id.panelPostpartumHistory);
        panelPregnancyHistory = (ExpandablePanel) findViewById(R.id.panelPregnancyHistory);
        panelSectionOne.setOnExpandListener(this);
        panelSectionTwo.setOnExpandListener(this);
        panelSectionThree.setOnExpandListener(this);
        panelSectionFour.setOnExpandListener(this);
        panelSectionFive.setOnExpandListener(this);
        panelSectionSix.setOnExpandListener(this);
        panelPregnancyHistory.setOnExpandListener(this);
        panelPostpartumHistory.setOnExpandListener(this);

        motherGroup.setOnCheckedChangeListener(this);

        draft();
    }


    private void draft() {

        draft_key = "DSS" + motherTypeValue + "" + survey + "" + pregnant;

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            draft_key = extras.getString("draft_key");
            draft_index = extras.getInt("draft_index");
        }

        if (draft_key != null) {

            JSONObject logic;
            String trace = getPreferences(Context.MODE_PRIVATE).getString(draft_key, "EMPTY");
            DraftActivity draftActivity = new DraftActivity();

            //clearIndexedDraft(draft_key, draft_index);

            if (!trace.equals("EMPTY")) {
                logic = draftActivity.showDraft(DangerSignsActivity.this, trace);
                Toast.makeText(getApplicationContext(), logic.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), draft_key, Toast.LENGTH_LONG).show();
                if (logic != null) {
                    isDraft = true;

                    try {

                        JSONObject jsonObject = logic.getJSONObject("jsonObject");

                        if (type == 0) {
                            a_dX.setSelection(jsonObject.getInt("1"));
                            b_aX.setSelection(jsonObject.getInt("2"));
                            f_cX.setSelection(jsonObject.getInt("3"));
                            f_nX.setSelection(jsonObject.getInt("4"));
                            h_bX.setSelection(jsonObject.getInt("5"));
                            l_pX.setSelection(jsonObject.getInt("6"));
                            l_cX.setSelection(jsonObject.getInt("7"));
                            h_dX.setSelection(jsonObject.getInt("8"));
                            b_vX.setSelection(jsonObject.getInt("9"));
                            d_bX.setSelection(jsonObject.getInt("10"));
                            p_uX.setSelection(jsonObject.getInt("11"));
                            p_fX.setSelection(jsonObject.getInt("12"));
                            w_bX.setSelection(jsonObject.getInt("13"));
                            a_lX.setSelection(jsonObject.getInt("14"));
                            p_sX.setSelection(jsonObject.getInt("15"));
                            f_sX.setSelection(jsonObject.getInt("16"));
                        } else {

                            b_nS.setId(jsonObject.getInt("17"));
                            b_hS.setId(jsonObject.getInt("18"));
                            b_vS.setId(jsonObject.getInt("19"));
                            e_wS.setId(jsonObject.getInt("20"));
                            s_hS.setId(jsonObject.getInt("21"));
                            v_S.setId(jsonObject.getInt("22"));
                            c_S.setId(jsonObject.getInt("23"));
                            f_S.setId(jsonObject.getInt("24"));
                            d_sS.setId(jsonObject.getInt("25"));
                            c_cS.setId(jsonObject.getInt("26"));
                            a_sS.setId(jsonObject.getInt("27"));
                            b_bS.setId(jsonObject.getInt("28"));
                            u_rS.setId(jsonObject.getInt("29"));
                            u_nS.setId(jsonObject.getInt("30"));
                            p_sP.setId(jsonObject.getInt("31"));
                            g_uS.setId(jsonObject.getInt("32"));
                            n_mS.setId(jsonObject.getInt("33"));
                        }

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

    private void householdMothersInfo() {

        ln = (LinearLayout) findViewById(R.id.partumMothers);
        ln_a = (LinearLayout) findViewById(R.id.pregnantSection);
        ln_btn = (LinearLayout) findViewById(R.id.radio_buttonz);

        lblDisplay = (TextView) findViewById(R.id.lblDisplay);

        sharedPreference = new SharedPreference();

        survey = sharedPreference.getValue(this, "survey_id");

        SharedPreferences sharedpreferences = getSharedPreferences(HouseHoldActivity.HOUSEHOLD_INFORMATION, Context.MODE_PRIVATE);
        postpartum = sharedpreferences.getInt("postpartum", 0);
        pregnant = sharedpreferences.getInt("pregnant", 0);
        survey_status = sharedpreferences.getInt("survey_status", 0);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            type = extras.getInt("type");
        }

        int var_postpartum = sharedPreference.getValue(getApplicationContext(), "postpartum");
        int var_pregnant = sharedPreference.getValue(getApplicationContext(), "pregnant");

        postpartum = postpartum - var_postpartum;
        pregnant = pregnant - var_pregnant;

        switch (type) {
            case 0:
                ActionBar actionBar = getSupportActionBar();
                actionBar.setTitle(getResources().getString(R.string.app_name));
                actionBar.setSubtitle("MOH 514 DSS");
                actionBar.setIcon(R.mipmap.ic_launcher);
                actionBar.setHomeButtonEnabled(false);

                lblDisplay.setText("Survey ID: " + survey + "       Postpartum Mother No: " + postpartum);
                number = postpartum;
                motherTypeValue = "post-partum";
                ln_a.setVisibility(View.GONE);
                ln_btn.setVisibility(View.GONE);
                my514 = "514";
                break;
            case 1:
                ActionBar actionBar2 = getSupportActionBar();
                actionBar2.setTitle(getResources().getString(R.string.app_name));
                actionBar2.setSubtitle("MOH 514 DSS");
                actionBar2.setIcon(R.mipmap.ic_launcher);
                actionBar2.setHomeButtonEnabled(false);

                lblDisplay.setText("Survey ID: " + survey + "       Pregnant Mother No: " + pregnant);
                number = pregnant;
                motherTypeValue = "pregnant";
                ln.setVisibility(View.GONE);
                ln_btn.setVisibility(View.GONE);
                my514 = "514";
                break;
            case 2:
                my514 = null;
                break;
        }
    }

    public void showDescription(View v) {
        new SweetAlertDialog(DangerSignsActivity.this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("Details")
                .setContentText(v.getContentDescription().toString())
                .show();
    }

    public void save(final Context context, final JSONObject details, String whereTo, final int index) {
        final ProgressDialog pDialog = new ProgressDialog(DangerSignsActivity.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        MUtil.showProgressDialog(pDialog);
        String url = "";
        url = MApplication.url + whereTo;

        Log.i(MApplication.TAG, MainActivity.class.getName() + " : Details " + details.toString());
        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        if (doLog) Log.i(TAG, CTAG + response.toString());
                        MUtil.hideProgressDialog(pDialog);

                        if (success.isSuccess()) {

                            Crouton.makeText(DangerSignsActivity.this, success.getMessage(), Style.CONFIRM).show();

                            if (toDss) {
                                Intent intent = new Intent(getApplicationContext(), Form100Activity.class);
                                intent.putExtra("mobile", mobile100);
                                intent.putExtra("pos", number); //mothers number
                                intent.putExtra("trace", String.valueOf(survey));
                                intent.putExtra("index", index); //dss option
                                intent.putExtra("mobile514", my514);
                                startActivity(intent);
                                finish();

                            } else if (!toDss) {
                                eventSelector();
                            }

                        } else {
                            Crouton.makeText(DangerSignsActivity.this, success.getMessage(), Style.ALERT).show();
                        }
                    }

                    ;
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Crouton.makeText(DangerSignsActivity.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }) {
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "survey");
    }

    public void nextForm(View view) {

        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            String refValue = extra.getString("pre");
            if (refValue == null) {

                try {
                    dssToggle();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

                try {
                    emptyFieldCheck();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public void emptyFieldCheck() throws JSONException {

        whereTo = "survey_category";
        boolean okay = true;

        JSONObject jsonObject = new JSONObject();
        JSONObject jSurvey = new JSONObject();
        JSONObject categoryInfo = new JSONObject();

        if (motherTypeValue.equals("pregnant")) {

            long tre = b_nS.getSelectedItemId() + b_hS.getSelectedItemId() + b_vS.getSelectedItemId() + e_wS.getSelectedItemId() + s_hS.getSelectedItemId() + v_S.getSelectedItemId() +
                    c_S.getSelectedItemId() + f_S.getSelectedItemId() + d_sS.getSelectedItemId() + c_cS.getSelectedItemId() + a_sS.getSelectedItemId() + b_bS.getSelectedItemId() +
                    u_rS.getSelectedItemId() + u_nS.getSelectedItemId() + p_sP.getSelectedItemId() + g_uS.getSelectedItemId() + n_mS.getSelectedItemId();

            if (tre >= 34) {
                toDss = false;
                okay = false;
            }

            index = 12;

            jsonObject.put("17", b_nS.getSelectedItemId());
            jsonObject.put("18", b_hS.getSelectedItemId());
            jsonObject.put("19", b_vS.getSelectedItemId());
            jsonObject.put("20", e_wS.getSelectedItemId());
            jsonObject.put("21", s_hS.getSelectedItemId());
            jsonObject.put("22", v_S.getSelectedItemId());
            jsonObject.put("23", c_S.getSelectedItemId());
            jsonObject.put("24", f_S.getSelectedItemId());
            jsonObject.put("25", d_sS.getSelectedItemId());
            jsonObject.put("26", c_cS.getSelectedItemId());
            jsonObject.put("27", a_sS.getSelectedItemId());
            jsonObject.put("28", b_bS.getSelectedItemId());
            jsonObject.put("29", u_rS.getSelectedItemId());
            jsonObject.put("30", u_nS.getSelectedItemId());
            jsonObject.put("31", p_sP.getSelectedItemId());
            jsonObject.put("32", g_uS.getSelectedItemId());
            jsonObject.put("33", n_mS.getSelectedItemId());
            jsonObject.put("53", f_sP.getSelectedItemId());
            jsonObject.put("54", m_sP.getSelectedItemId());
            jsonObject.put("55", a_sV.getSelectedItemId());
            jsonObject.put("56", p_fA.getSelectedItemId());

            jsonObject.put("select", index);

            categoryInfo.put("survey_status", survey_status);
            categoryInfo.put("type", 7);
            categoryInfo.put("number", pregnant);
            categoryInfo.put("survey_id", survey);
            jSurvey.put("jsonObject", jsonObject);
            jSurvey.put("categoryInfo", categoryInfo);


            if (b_nS.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.Any_bleeding);
                return;
            }
            if (b_hS.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.Bad_headache);
                return;
            }
            if (b_vS.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.Blurred_vision);
                return;
            }
            if (e_wS.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.extreme);
                return;
            }
            if (s_hS.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.Swelling_hands);
                return;
            }
            if (v_S.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.vomiting);
                return;
            }
            if (c_S.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.Consiousness);
                return;
            }
            if (f_S.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.fever);
                return;
            }
            if (d_sS.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.dischage);
                return;
            }
            if (c_cS.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.convulsions);
                return;
            }
            if (a_sS.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.abnominal);
                return;
            }
            if (b_bS.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.Breathing);
                return;
            }
            if (u_rS.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.Urination);
                return;
            }
            if (u_nS.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.urinate_alot);
                return;
            }
            if (p_sP.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.pale_pams);
                return;
            }
            if (g_uS.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.genital_ulcers);
                return;
            }
            if (n_mS.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.not_moving);
                return;
            }

            if (f_sP.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.first_pregnancy);
                return;
            }
            if (m_sP.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.months_pregnant);
                return;
            }
            if (a_sV.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.anc_visit);
                return;
            }
            if (p_fA.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.pregnant_first_anc);
                return;
            }

            if (MUtil.isNetworkConnected(getApplicationContext())) {
                if (!isDraft) {
                    save(getApplicationContext(), jSurvey, whereTo, index);
                } else {
                    save(getApplicationContext(), getDraftedJson(), whereTo, index);
                }
            } else {
                DraftActivity.makeDraft(DangerSignsActivity.this, jSurvey, "DSS" + motherTypeValue + "" + survey + "" + pregnant);
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }


        } else if (motherTypeValue.equals("post-partum")) {

            long sum = a_dX.getSelectedItemId() + b_aX.getSelectedItemId() + f_nX.getSelectedItemId() + f_cX.getSelectedItemId() + h_bX.getSelectedItemId() +
                    l_pX.getSelectedItemId() + l_cX.getSelectedItemId() + h_dX.getSelectedItemId() + b_vX.getSelectedItemId() + d_bX.getSelectedItemId() +
                    p_uX.getSelectedItemId() + p_fX.getSelectedItemId() + w_bX.getSelectedItemId() + a_lX.getSelectedItemId() + p_sX.getSelectedItemId() + f_sX.getSelectedItemId();

            if (sum >= 32) {
                toDss = false;
            }
            index = 11;


            jsonObject.put("1", a_dX.getSelectedItemId());
            jsonObject.put("2", b_aX.getSelectedItemId());
            jsonObject.put("3", f_cX.getSelectedItemId());
            jsonObject.put("4", f_cX.getSelectedItemId());
            jsonObject.put("5", h_bX.getSelectedItemId());
            jsonObject.put("6", l_pX.getSelectedItemId());
            jsonObject.put("7", l_cX.getSelectedItemId());
            jsonObject.put("8", h_dX.getSelectedItemId());
            jsonObject.put("9", b_vX.getSelectedItemId());
            jsonObject.put("10", d_bX.getSelectedItemId());
            jsonObject.put("11", p_uX.getSelectedItemId());
            jsonObject.put("12", p_fX.getSelectedItemId());
            jsonObject.put("13", w_bX.getSelectedItemId());
            jsonObject.put("14", a_lX.getSelectedItemId());
            jsonObject.put("15", p_sX.getSelectedItemId());
            jsonObject.put("16", f_sX.getSelectedItemId());
            jsonObject.put("56", t_bS.getSelectedItemId());
            jsonObject.put("57", f_bS.getSelectedItemId());
            jsonObject.put("58", p_aS.getSelectedItemId());
            jsonObject.put("59", p_aS.getSelectedItemId());

            jsonObject.put("select", index);

            categoryInfo.put("survey_status", survey_status);
            categoryInfo.put("survey_id", survey);
            categoryInfo.put("number", postpartum);
            categoryInfo.put("type", 7);
            jSurvey.put("jsonObject", jsonObject);
            jSurvey.put("categoryInfo", categoryInfo);


            if (a_dX.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.abdominal_discharge);
                return;
            }
            if (b_aX.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.bad_abdominal);
                return;
            }
            if (f_nX.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.fever_chills);
                return;
            }
            if (f_cX.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.fever_nochills);
                return;
            }
            if (h_bX.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.high_blood);
                return;
            }
            if (l_pX.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.labour_pain);
                return;
            }
            if (l_cX.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.loss_consciousness);
                return;
            }
            if (h_dX.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.head_dizzy);
                return;
            }
            if (b_vX.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.Blurred_vision);
                return;
            }

            if (d_bX.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.diff_breathing);
                return;
            }
            if (p_uX.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.pass_urine);
                return;
            }
            if (p_fX.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.palm_feet);
                return;
            }
            if (w_bX.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.water_break);
                return;
            }
            if (a_lX.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.arm_legs);
                return;
            }
            if (p_sX.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.placenta);
                return;
            }
            if (f_sX.getSelectedItemId() == 0) {
                showInvalidMessage(R.string.foul_smelling);
                return;
            }
            if (t_bS.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.time_birth);
                return;
            }
            if (f_bS.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.first_baby);
                return;
            }
            if (p_aS.getSelectedItemPosition() == 0) {
                showInvalidMessage(R.string.pnc_attended);
                return;
            }

            if (MUtil.isNetworkConnected(getApplicationContext())) {
                if (!isDraft) {
                    save(getApplicationContext(), jSurvey, whereTo, index);
                } else {
                    save(getApplicationContext(), getDraftedJson(), whereTo, index);
                }
            } else {
                DraftActivity.makeDraft(DangerSignsActivity.this, jSurvey, "DSS" + motherTypeValue + "" + survey + "" + postpartum);
                Intent intent = new Intent(getApplicationContext(), Home.class);
                startActivity(intent);
            }


        }


    }

    public void showInvalidMessage(int message) {
        new SweetAlertDialog(DangerSignsActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setContentText("Select If " + getResources().getString(message)).show();
    }

    public void dssToggle() throws JSONException {
        whereTo = "dss";
        boolean okay = true;
        int villageId, age, worker_name;
        String name, householdMobile, gender, guardian;
        double latitude, longitude;

        Bundle extra = getIntent().getExtras();
        JSONObject individual_details = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        JSONObject jSurvey = new JSONObject();

        if (extra != null) {

            villageId = extra.getInt("village_id");
            name = extra.getString("name");
            age = extra.getInt("age");
            gender = extra.getString("gender");
            householdMobile = extra.getString("number");
            worker_name = extra.getInt("health_worker_id");
            latitude = extra.getDouble("uLatitude");
            longitude = extra.getDouble("uLongitude");
            guardian = extra.getString("guardian");

            individual_details.put("village_id", villageId);
            individual_details.put("age", age);
            individual_details.put("name", name);
            individual_details.put("gender", gender);
            individual_details.put("guardian", guardian);
            individual_details.put("mobile", householdMobile);
            individual_details.put("health_worker_id", worker_name);
            individual_details.put("latitude", latitude);
            individual_details.put("longitude", longitude);
            individual_details.put("org_id", 0);
            individual_details.put("weight", 0);
            individual_details.put("age_type", "YEARS");

            mobile100 = householdMobile;

            if (motherTypeValue.equals("pregnant")) {

                long tre = b_nS.getSelectedItemId() + b_hS.getSelectedItemId() + b_vS.getSelectedItemId() + e_wS.getSelectedItemId() + s_hS.getSelectedItemId() + v_S.getSelectedItemId() +
                        c_S.getSelectedItemId() + f_S.getSelectedItemId() + d_sS.getSelectedItemId() + c_cS.getSelectedItemId() + a_sS.getSelectedItemId() + b_bS.getSelectedItemId() +
                        u_rS.getSelectedItemId() + u_nS.getSelectedItemId() + p_sP.getSelectedItemId() + g_uS.getSelectedItemId() + n_mS.getSelectedItemId();

                if (tre >= 34) {
                    toDss = false;
                    okay = false;
                }

                int index = 12;

                jsonObject.put("17", b_nS.getSelectedItemId());
                jsonObject.put("18", b_hS.getSelectedItemId());
                jsonObject.put("19", b_vS.getSelectedItemId());
                jsonObject.put("20", e_wS.getSelectedItemId());
                jsonObject.put("21", s_hS.getSelectedItemId());
                jsonObject.put("22", v_S.getSelectedItemId());
                jsonObject.put("23", c_S.getSelectedItemId());
                jsonObject.put("24", f_S.getSelectedItemId());
                jsonObject.put("25", d_sS.getSelectedItemId());
                jsonObject.put("26", c_cS.getSelectedItemId());
                jsonObject.put("27", a_sS.getSelectedItemId());
                jsonObject.put("28", b_bS.getSelectedItemId());
                jsonObject.put("29", u_rS.getSelectedItemId());
                jsonObject.put("30", u_nS.getSelectedItemId());
                jsonObject.put("31", p_sP.getSelectedItemId());
                jsonObject.put("32", g_uS.getSelectedItemId());
                jsonObject.put("33", n_mS.getSelectedItemId());
                jsonObject.put("53", f_sP.getSelectedItemId());
                jsonObject.put("54", m_sP.getSelectedItemId());
                jsonObject.put("55", a_sV.getSelectedItemId());
                jsonObject.put("56", p_fA.getSelectedItemId());


                individual_details.put("dsselection", index);

                jSurvey.put("individual_details", individual_details);
                jSurvey.put("danger_signs", jsonObject);


                if (b_nS.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.Any_bleeding);
                    return;
                }
                if (b_hS.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.Bad_headache);
                    return;
                }
                if (b_vS.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.Blurred_vision);
                    return;
                }
                if (e_wS.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.extreme);
                    return;
                }
                if (s_hS.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.Swelling_hands);
                    return;
                }
                if (v_S.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.vomiting);
                    return;
                }
                if (c_S.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.Consiousness);
                    return;
                }
                if (f_S.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.fever);
                    return;
                }
                if (d_sS.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.dischage);
                    return;
                }
                if (c_cS.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.convulsions);
                    return;
                }
                if (a_sS.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.abnominal);
                    return;
                }
                if (b_bS.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.Breathing);
                    return;
                }
                if (u_rS.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.Urination);
                    return;
                }
                if (u_nS.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.urinate_alot);
                    return;
                }
                if (p_sP.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.pale_pams);
                    return;
                }
                if (g_uS.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.genital_ulcers);
                    return;
                }
                if (n_mS.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.not_moving);
                    return;
                }
                if (f_sP.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.first_pregnancy);
                    return;
                }
                if (m_sP.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.months_pregnant);
                    return;
                }
                if (a_sV.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.anc_visit);
                    return;
                }
                if (p_fA.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.anc_visit);
                    return;
                }

                if (MUtil.isNetworkConnected(getApplicationContext())) {
                    if (!isDraft) {
                        save(getApplicationContext(), jSurvey, whereTo, index);
                    } else {
                        save(getApplicationContext(), getDraftedJson(), whereTo, index);
                    }
                } else {
                    DraftActivity.makeDraft(DangerSignsActivity.this, jSurvey, "DSS" + motherTypeValue + "" + survey + "" + postpartum);
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                }


            } else if (motherTypeValue.equals("post-partum")) {

                long sum = a_dX.getSelectedItemId() + b_aX.getSelectedItemId() + f_nX.getSelectedItemId() + f_cX.getSelectedItemId() + h_bX.getSelectedItemId() +
                        l_pX.getSelectedItemId() + l_cX.getSelectedItemId() + h_dX.getSelectedItemId() + b_vX.getSelectedItemId() + d_bX.getSelectedItemId() +
                        p_uX.getSelectedItemId() + p_fX.getSelectedItemId() + w_bX.getSelectedItemId() + a_lX.getSelectedItemId() + p_sX.getSelectedItemId() + f_sX.getSelectedItemId();

                if (sum >= 32) {
                    toDss = false;
                }

                int index = 11;

                jsonObject.put("1", a_dX.getSelectedItemId());
                jsonObject.put("2", b_aX.getSelectedItemId());
                jsonObject.put("3", f_cX.getSelectedItemId());
                jsonObject.put("4", f_cX.getSelectedItemId());
                jsonObject.put("5", h_bX.getSelectedItemId());
                jsonObject.put("6", l_pX.getSelectedItemId());
                jsonObject.put("7", l_cX.getSelectedItemId());
                jsonObject.put("8", h_dX.getSelectedItemId());
                jsonObject.put("9", b_vX.getSelectedItemId());
                jsonObject.put("10", d_bX.getSelectedItemId());
                jsonObject.put("11", p_uX.getSelectedItemId());
                jsonObject.put("12", p_fX.getSelectedItemId());
                jsonObject.put("13", w_bX.getSelectedItemId());
                jsonObject.put("14", a_lX.getSelectedItemId());
                jsonObject.put("15", p_sX.getSelectedItemId());
                jsonObject.put("16", f_sX.getSelectedItemId());
                jsonObject.put("56", t_bS.getSelectedItemId());
                jsonObject.put("57", f_bS.getSelectedItemId());
                jsonObject.put("58", p_aS.getSelectedItemId());
                jsonObject.put("59", p_aS.getSelectedItemId());

                individual_details.put("dsselection", index);

                jSurvey.put("individual_details", individual_details);
                jSurvey.put("danger_signs", jsonObject);

                if (a_dX.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.abdominal_discharge);
                    return;
                }
                if (b_aX.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.bad_abdominal);
                    return;
                }
                if (f_nX.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.fever_chills);
                    return;
                }
                if (f_cX.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.fever_nochills);
                    return;
                }
                if (h_bX.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.high_blood);
                    return;
                }
                if (l_pX.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.labour_pain);
                    return;
                }
                if (l_cX.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.loss_consciousness);
                    return;
                }
                if (h_dX.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.head_dizzy);
                    return;
                }
                if (b_vX.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.Blurred_vision);
                    return;
                }
                if (d_bX.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.diff_breathing);
                    return;
                }
                if (p_uX.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.pass_urine);
                    return;
                }
                if (p_fX.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.palm_feet);
                    return;
                }
                if (w_bX.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.water_break);
                    return;
                }
                if (a_lX.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.arm_legs);
                    return;
                }
                if (p_sX.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.placenta);
                    return;
                }
                if (f_sX.getSelectedItemId() == 0) {
                    showInvalidMessage(R.string.foul_smelling);
                    return;
                }
                if (t_bS.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.time_birth);
                    return;
                }
                if (f_bS.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.first_baby);
                    return;
                }
                if (p_aS.getSelectedItemPosition() == 0) {
                    showInvalidMessage(R.string.pnc_attended);
                    return;
                }

                if (MUtil.isNetworkConnected(getApplicationContext())) {
                    if (!isDraft) {
                        save(getApplicationContext(), jSurvey, whereTo, index);
                    } else {
                        save(getApplicationContext(), getDraftedJson(), whereTo, index);
                    }
                } else {
                    DraftActivity.makeDraft(DangerSignsActivity.this, jSurvey, "DSS" + motherTypeValue + "" + survey + "" + postpartum);
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                }

            }
        }

    }

    @Override
    public void onExpand(View handle, View content) {
        TextView header = (TextView) handle;
        header.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_less_white_18dp, 0);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onCollapse(View handle, View content) {
        TextView header = (TextView) handle;
        header.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_expand_more_white_18dp, 0);

    }

    public void eventSelector() {
        final CharSequence[] items = {"No need to refer this patient"};
        AlertDialog.Builder builder = new AlertDialog.Builder(DangerSignsActivity.this);
        builder.setCancelable(false);
        builder.setTitle("Referral");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent;
                if (my514 != null) {
                    intent = new Intent(getApplicationContext(), HouseholdMembersActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    intent = new Intent(getApplicationContext(), Home.class);
                    startActivity(intent);
                    finish();
                }

            }
        });


        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.radio_pregnant:
                motherTypeValue = "pregnant";
                ln_a.setVisibility(View.VISIBLE);
                ln.setVisibility(View.GONE);

                break;
            case R.id.radio_post_partum:
                motherTypeValue = "post-partum";
                ln.setVisibility(View.VISIBLE);
                ln_a.setVisibility(View.GONE);

                break;
        }
    }

    public JSONObject getDraftedJson() {
        return draftedJson;
    }
}
