package com.dewcis.mdss.model;


import android.util.Log;

import com.dewcis.mdss.MApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by henriquedn on 4/29/15.
 * henrydkm@gmail.com
 * henry.maina@dewcis.com
 */
public class Survey {
    static final String TAG = MApplication.TAG;
    static boolean doLog = MApplication.LOGDEBUG;
    static String CTAG = Survey.class.getName() + " : " ;

    public static final String SURVEY = "survey", VILLAGE_ID = "village_id", SUB_LOCATION_ID = "sub_location_id";

    public JSONObject getJsurvey() {
        return jsurvey;
    }

    public void setJsurvey(JSONObject jsurvey) {
        this.jsurvey = jsurvey;
    }

    JSONObject jsurvey ;

    public int getSurvey_id() {
        return survey_id;
    }

    public void setSurvey_id(int survey_id) {
        this.survey_id = survey_id;
    }

    public int getvillageID() {
        return villageID;
    }

    public void setvillageID(int villageID) {
        this.villageID = villageID;
    }

    int survey_id, villageID;

    //data for one submission

    String txtVillageName;
    String txtHouseholdNum;
    String txtHouseholdMember;
    String txtRemarks;
    String returnReason;
    String txtReferalIllnessOther;
    String landmark;
    String nickname;
    String txtDeathInfoOther;
    int surveyStatus;

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    //mother information
    int spnPregnant , spnF, spnG, spnH, spnI, spnJ, spnK;
    //child information
    int spnChildGender, spnL, spnM, spnN, spnO;
    //referral info
    int spnP, spnQ, spnR, spnS, spnT, spnU, spnV, spnW, spnX, spnY_a, spnY_b, spnY_c, spnY_d ;
    //defaulters info
    int spnZ, spnAA, spnAB, spnAC;
    //death information
    int spnAD_a, spnAD_b, spnAD_c, spnAD_d;
    //household information
    int spnAI, spnAJ, spnAK;


    public Survey(){

    }

    public static ArrayList<Survey> makeArraylist(JSONArray jsonArray){
        ArrayList<Survey> surveys = new ArrayList<Survey>();
        try{
            for(int i = 0; i < jsonArray.length(); i++){
                surveys.add(Survey.makeFromJSON(jsonArray.getJSONObject(i)));
            }
        }catch (JSONException e){
            if(doLog) Log.e(TAG, CTAG  + e.toString());
        }

        return surveys;
    }

    public static Survey makeFromJSON(JSONObject jsonObject){
        Survey s = new Survey();
            s.setJsurvey(jsonObject);
        try{
            JSONObject basicInfo = jsonObject.getJSONObject("basicInfo");

            String house = "KAM/"+ basicInfo.getInt("survey_id") + "/017";

            s.setSurvey_id(basicInfo.getInt("survey_id"));
            s.setvillageID(basicInfo.getInt(VILLAGE_ID));
            s.setTxtVillageName(basicInfo.getString("village_name"));
            s.setTxtHouseholdNum(house);
            s.setTxtHouseholdMember(basicInfo.getString("household_member"));
            s.setTxtRemarks(basicInfo.getString("remarks"));
            s.setReturnReason(basicInfo.getString("returnReason"));
            s.setSurveyStatus(basicInfo.getInt("survey_status"));
            s.setNickname(basicInfo.getString("nickname"));
            s.setLandmark(basicInfo.getString("landmark"));
        }catch (JSONException e){
            if(doLog) Log.e(TAG, CTAG  + e.toString());
        }
        return s;
    }

    public String getTxtVillageName() {
        return txtVillageName;
    }

    public void setTxtVillageName(String txtVillageName) {
        this.txtVillageName = txtVillageName;
    }

    public String getTxtHouseholdNum() {
        return txtHouseholdNum;
    }

    public void setTxtHouseholdNum(String txtHouseholdNum) {
        this.txtHouseholdNum = txtHouseholdNum;
    }

    public String getTxtHouseholdMember() {
        return txtHouseholdMember;
    }

    public void setTxtHouseholdMember(String txtHouseholdMember) {
        this.txtHouseholdMember = txtHouseholdMember;
    }

    public String getTxtRemarks() {
        return txtRemarks;
    }

    public void setTxtRemarks(String txtRemarks) {
        this.txtRemarks = txtRemarks;
    }

    public String getTxtReferalIllnessOther() {
        return txtReferalIllnessOther;
    }

    public void setTxtReferalIllnessOther(String txtReferalIllnessOther) {
        this.txtReferalIllnessOther = txtReferalIllnessOther;
    }

    public int getSpnPregnant() {
        return spnPregnant;
    }

    public void setSpnPregnant(int spnPregnant) {
        this.spnPregnant = spnPregnant;
    }

    public int getSpnF() {
        return spnF;
    }

    public void setSpnF(int spnF) {
        this.spnF = spnF;
    }

    public int getSpnG() {
        return spnG;
    }

    public void setSpnG(int spnG) {
        this.spnG = spnG;
    }

    public int getSpnH() {
        return spnH;
    }

    public void setSpnH(int spnH) {
        this.spnH = spnH;
    }

    public int getSpnI() {
        return spnI;
    }

    public void setSpnI(int spnI) {
        this.spnI = spnI;
    }

    public int getSpnJ() {
        return spnJ;
    }

    public void setSpnJ(int spnJ) {
        this.spnJ = spnJ;
    }

    public int getSpnK() {
        return spnK;
    }

    public void setSpnK(int spnK) {
        this.spnK = spnK;
    }

    public int getSpnChildGender() {
        return spnChildGender;
    }

    public void setSpnChildGender(int spnChildGender) {
        this.spnChildGender = spnChildGender;
    }

    public int getSpnL() {
        return spnL;
    }

    public void setSpnL(int spnL) {
        this.spnL = spnL;
    }

    public int getSpnM() {
        return spnM;
    }

    public void setSpnM(int spnM) {
        this.spnM = spnM;
    }

    public int getSpnN() {
        return spnN;
    }

    public void setSpnN(int spnN) {
        this.spnN = spnN;
    }

    public int getSpnO() {
        return spnO;
    }

    public void setSpnO(int spnO) {
        this.spnO = spnO;
    }

    public int getSpnP() {
        return spnP;
    }

    public void setSpnP(int spnP) {
        this.spnP = spnP;
    }

    public int getSpnQ() {
        return spnQ;
    }

    public void setSpnQ(int spnQ) {
        this.spnQ = spnQ;
    }

    public int getSpnR() {
        return spnR;
    }

    public void setSpnR(int spnR) {
        this.spnR = spnR;
    }

    public int getSpnS() {
        return spnS;
    }

    public void setSpnS(int spnS) {
        this.spnS = spnS;
    }

    public int getSpnT() {
        return spnT;
    }

    public void setSpnT(int spnT) {
        this.spnT = spnT;
    }

    public int getSpnU() {
        return spnU;
    }

    public void setSpnU(int spnU) {
        this.spnU = spnU;
    }

    public int getSpnV() {
        return spnV;
    }

    public void setSpnV(int spnV) {
        this.spnV = spnV;
    }

    public int getSpnW() {
        return spnW;
    }

    public void setSpnW(int spnW) {
        this.spnW = spnW;
    }

    public int getSpnX() {
        return spnX;
    }

    public void setSpnX(int spnX) {
        this.spnX = spnX;
    }

    public int getSpnY_a() {
        return spnY_a;
    }

    public void setSpnY_a(int spnY_a) {
        this.spnY_a = spnY_a;
    }

    public int getSpnY_b() {
        return spnY_b;
    }

    public void setSpnY_b(int spnY_b) {
        this.spnY_b = spnY_b;
    }

    public int getSpnY_c() {
        return spnY_c;
    }

    public void setSpnY_c(int spnY_c) {
        this.spnY_c = spnY_c;
    }

    public int getSpnY_d() {
        return spnY_d;
    }

    public void setSpnY_d(int spnY_d) {
        this.spnY_d = spnY_d;
    }

    public int getSpnZ() {
        return spnZ;
    }

    public void setSpnZ(int spnZ) {
        this.spnZ = spnZ;
    }

    public int getSpnAA() {
        return spnAA;
    }

    public void setSpnAA(int spnAA) {
        this.spnAA = spnAA;
    }

    public int getSpnAB() {
        return spnAB;
    }

    public void setSpnAB(int spnAB) {
        this.spnAB = spnAB;
    }

    public int getSpnAC() {
        return spnAC;
    }

    public void setSpnAC(int spnAC) {
        this.spnAC = spnAC;
    }

    public int getSpnAD_a() {
        return spnAD_a;
    }

    public void setSpnAD_a(int spnAD_a) {
        this.spnAD_a = spnAD_a;
    }

    public int getSpnAD_b() {
        return spnAD_b;
    }

    public void setSpnAD_b(int spnAD_b) {
        this.spnAD_b = spnAD_b;
    }

    public int getSpnAD_c() {
        return spnAD_c;
    }

    public void setSpnAD_c(int spnAD_c) {
        this.spnAD_c = spnAD_c;
    }

    public int getSpnAD_d() {
        return spnAD_d;
    }

    public void setSpnAD_d(int spnAD_d) {
        this.spnAD_d = spnAD_d;
    }

    public int getSpnAI() {
        return spnAI;
    }

    public void setSpnAI(int spnAI) {
        this.spnAI = spnAI;
    }

    public int getSpnAJ() {
        return spnAJ;
    }

    public void setSpnAJ(int spnAJ) {
        this.spnAJ = spnAJ;
    }

    public int getSpnAK() {
        return spnAK;
    }

    public void setSpnAK(int spnAK) {
        this.spnAK = spnAK;
    }


    public void setSurveyStatus(int surveyStatus) {
        this.surveyStatus = surveyStatus;
    }
    public int getSurveyStatus() {
        return surveyStatus;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getNickname() {
        return nickname;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }
    public String getLandmark() {
        return landmark;
    }
}
