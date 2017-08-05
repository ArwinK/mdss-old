package com.dewcis.aphrc;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Map;
import java.util.HashMap;
import java.util.logging.Logger;

import org.baraza.xml.BXML;
import org.baraza.xml.BElement;
import org.baraza.DB.BDB;
import org.baraza.DB.BQuery;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


@WebServlet("/android")
public class Android extends HttpServlet{
	static Logger log = Logger.getLogger(Android.class.getName());
    static final boolean doLog = true;
	RequestDispatcher dispatcher;
	
	@SuppressWarnings("unchecked")
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		String tag = req.getParameter("tag");
		if(tag.equals("") || tag == null){
			dispatcher = req.getRequestDispatcher("consultant/");
			dispatcher.forward(req, resp);
		}
		
        
        if(tag.equals("authenticate")){
			log.info("Reached Authentication");
			String dbconfig = "java:/comp/env/jdbc/database";
			BDB db = new BDB(dbconfig);log.info("Connection Opened--------------");
			int success = 0; String message = "You Are Not Subscribed to this service.";
			
            try{
                JSONObject jin = getInputStreamAsJson(req);
                
                String mobile = jin.getString("worker_mobile_num");
                String password = jin.getString("password");
                String app_version = jin.getString("app_version");
                
                String chkSql = "SELECT org_id, org_name, health_worker_id, worker_name, worker_mobile_num, is_first_login, date_enrolled,  device_id FROM vw_health_workers WHERE is_active = true AND worker_pass = '" + password + "' AND worker_mobile_num = '" + mobile + "' LIMIT 1;";
                log.info("SQL : " + chkSql);

                ResultSet rs = db.readQuery(chkSql);
                JSONObject user_details = new JSONObject();
                
                while (rs.next()) {
                    user_details.put("health_worker_id", rs.getInt("health_worker_id"));
                    user_details.put("worker_name", rs.getString("worker_name"));
                    user_details.put("org_id", rs.getInt("org_id"));
                    user_details.put("device_id", rs.getInt("device_id"));
                    user_details.put("worker_mobile_num", rs.getString("worker_mobile_num"));
                    user_details.put("is_first_login", rs.getBoolean("is_first_login"));
                    
                    success = 1; message = "Authentication Successfull.";
                }
                
                
			
                log.info("jin : " + jin);

                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                JSONObject jobj = new JSONObject();
                jobj.put("success", success);
                jobj.put("message", message);
                jobj.put("user_details", user_details);
                out.print(jobj);
                log.info("JSON  VALIDATE USER : " + jobj.toString());
                
            }catch(JSONException jsone){
                if(doLog) log.severe("JSONException : " + jsone.toString());
            }catch(SQLException sqle){
                if(doLog) log.severe("SQLException : " + sqle.toString());
            }
			
            
            if(db != null){
				db.close();
				log.info("Closing Connection --------------");
			}
		}// end authenticate
        else if(tag.equals("survey")){
            log.info("Reached post survey");
			String dbconfig = "java:/comp/env/jdbc/database";
			BDB db = new BDB(dbconfig);log.info("Connection Opened--------------");
			int success = 0; String message = "You Are Not Subscribed to this service.";
			
            try{
                JSONObject jSurvey = getInputStreamAsJson(req);
                log.info("jSurvey : " + jSurvey);
                
                JSONObject basicInfo = jSurvey.getJSONObject("basicInfo");
                JSONObject motherInfo = jSurvey.getJSONObject("motherInfo");
                JSONObject childInfo = jSurvey.getJSONObject("childInfo");
                JSONObject referralInfo = jSurvey.getJSONObject("referralInfo");
                JSONObject defaultersInfo = jSurvey.getJSONObject("defaultersInfo");
                JSONObject deathInfo = jSurvey.getJSONObject("deathInfo");
                JSONObject householdInfo = jSurvey.getJSONObject("householdInfo");
                
                String sqlS = "INSERT INTO surveys(org_id, health_worker_id, village_id, household_number, household_member, location_lat, location_lng, remarks) "
                            + " VALUES ( " 
                            + basicInfo.getInt("org_id") + ", " 
                            + basicInfo.getInt("health_worker_id")  + ", " 
                            + basicInfo.getInt("villageId") + " , '" 
                            + basicInfo.getString("houseHoldNum") + "', '" 
                            + basicInfo.getString("householdMember") + "', '" 
                            + basicInfo.getString("latitude") + "',  '" 
                            + basicInfo.getString("longitude") + "', '" 
                            + basicInfo.getString("remarks") + "') RETURNING survey_id";
                

                log.info("INSERT Survey .................... SQl : \n" + sqlS );
                
                ResultSet rs = db.readQuery(sqlS);
                int survey_id = -1;
                
                while(rs.next()) {
                    survey_id = rs.getInt("survey_id") ;
                    log.info("\n\n\nsurvey_id : " + survey_id);
                }
                
                if(survey_id != -1){
                    
                    
                    String slqMother = "INSERT INTO survey_mother(survey_id, mother_info_def_id, response) VALUES"
                        + " ( " + survey_id + ", 1 ,  " + motherInfo.getInt("1") + "),"
                        + " ( " + survey_id + ", 2 ,  " + motherInfo.getInt("2") + "),"
                        + " ( " + survey_id + ", 3 ,  " + motherInfo.getInt("3") + "),"
                        + " ( " + survey_id + ", 4 ,  " + motherInfo.getInt("4") + "),"
                        + " ( " + survey_id + ", 5 ,  " + motherInfo.getInt("5") + "),"
                        + " ( " + survey_id + ", 6 ,  " + motherInfo.getInt("6") + "),"
                        + " ( " + survey_id + ", 7 ,  " + motherInfo.getInt("7") + ")";
                    
                    String slqChild = "INSERT INTO survey_child(survey_id, child_info_def_id, response) VALUES"
                        + " ( " + survey_id + ", 1 ,  " + childInfo.getInt("1") + "),"
                        + " ( " + survey_id + ", 2 ,  " + childInfo.getInt("2") + "),"
                        + " ( " + survey_id + ", 3 ,  " + childInfo.getInt("3") + "),"
                        + " ( " + survey_id + ", 4 ,  " + childInfo.getInt("4") + "),"
                        + " ( " + survey_id + ", 5 ,  " + childInfo.getInt("5") + ")";
                    
                    
                    String slqReferral = "INSERT INTO survey_referrals(survey_id, referral_info_defs_id, response) VALUES "
                        + " ( " + survey_id + ", 1 ,  " + referralInfo.getInt("1") + "),"
                        + " ( " + survey_id + ", 2 ,  " + referralInfo.getInt("2") + "),"
                        + " ( " + survey_id + ", 3 ,  " + referralInfo.getInt("3") + "),"
                        + " ( " + survey_id + ", 4 ,  " + referralInfo.getInt("4") + "),"
                        + " ( " + survey_id + ", 5 ,  " + referralInfo.getInt("5") + "),"
                        + " ( " + survey_id + ", 6 ,  " + referralInfo.getInt("6") + "),"
                        + " ( " + survey_id + ", 7 ,  " + referralInfo.getInt("7") + "),"
                        + " ( " + survey_id + ", 8 ,  " + referralInfo.getInt("8") + "),"
                        + " ( " + survey_id + ", 9 ,  " + referralInfo.getInt("9") + "),"
                        
                        + " ( " + survey_id + ", 10 ,  " + referralInfo.getInt("10") + "),"
                        + " ( " + survey_id + ", 11 ,  " + referralInfo.getInt("11") + "),"
                        + " ( " + survey_id + ", 12 ,  " + referralInfo.getInt("12") + "),"
                        + " ( " + survey_id + ", 13 ,  " + referralInfo.getInt("13") + "),"
                        + " ( " + survey_id + ", 14 ,  '" + referralInfo.getString("14") + "')";
                    
                    
                    
                    
                    
                    String slqDefault = "INSERT INTO survey_defaulters(survey_id, defaulters_info_def_id, response) VALUES"
                        + " ( " + survey_id + ", 1 ,  " + defaultersInfo.getInt("1") + "),"
                        + " ( " + survey_id + ", 2 ,  " + defaultersInfo.getInt("2") + "),"
                        + " ( " + survey_id + ", 3 ,  " + defaultersInfo.getInt("3") + "),"
                        + " ( " + survey_id + ", 4 ,  " + defaultersInfo.getInt("4") + "),"
                        + " ( " + survey_id + ", 5 ,  " + defaultersInfo.getInt("5") + ")";
                    
                    String slqDeath = "INSERT INTO survey_death(survey_id, death_info_def_id, response) VALUES"
                        + " ( " + survey_id + ", 1 ,  " + deathInfo.getInt("1") + "),"
                        + " ( " + survey_id + ", 2 ,  " + deathInfo.getInt("2") + "),"
                        + " ( " + survey_id + ", 3 ,  " + deathInfo.getInt("3") + "),"
                        + " ( " + survey_id + ", 4 ,  " + deathInfo.getInt("4") + "),"
                        + " ( " + survey_id + ", 5 ,  '" + deathInfo.getString("5") + "')";
                    
                    
                    
                    String slqHousehold = "INSERT INTO survey_household(survey_id, household_info_def_id, response) VALUES "
                        + " ( " + survey_id + ", 1 ,  " + householdInfo.getInt("1") + "),"
                        + " ( " + survey_id + ", 2 ,  " + householdInfo.getInt("2") + "),"
                        + " ( " + survey_id + ", 3 ,  " + householdInfo.getInt("3") + ")";
                    

                    
                    
                    log.info("\n\nslqMother : " + slqMother);
                    log.info("\n\nslqChild : " + slqChild);
                    log.info("\n\nslqReferral : " + slqReferral);
                    log.info("\n\nslqDefault : " + slqDefault);
                    log.info("\n\nslqDeath : " + slqDeath);
                    log.info("\n\nslqHousehold : " + slqHousehold);
                    db.executeQuery(slqMother);
                    db.executeQuery(slqChild);
                    db.executeQuery(slqReferral);
                    db.executeQuery(slqDefault);
                    db.executeQuery(slqDeath);
                    db.executeQuery(slqHousehold);
                    
                   success = 1; message = "Survey Submited Successfully"; 
                }
                
                

                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                JSONObject jobj = new JSONObject();
                jobj.put("success", success);
                jobj.put("message", message);
                out.print(jobj);
                log.info("JSON  SURVEY: " + jobj.toString());
            }catch(JSONException jsone){
                if(doLog) log.severe("JSONException : " + jsone.toString());
            }catch(SQLException sqle){
                if(doLog) log.severe("SQLException : " + sqle.toString());
            }
			
            
            if(db != null){
				db.close();
				log.info("Closing Connection --------------");
			}
            
        }  // send survey
        else if(tag.equals("getSurveys")){
            log.info("Reached GetSurveys");
			String dbconfig = "java:/comp/env/jdbc/database";
			BDB db = new BDB(dbconfig);log.info("Connection Opened--------------");
			int success = 0; String message = "You have No returned Surveys.";
			
            try{
                JSONObject jin = getInputStreamAsJson(req);
                log.info("jin : " + jin);
                String health_worker_id = jin.getString("health_worker_id");
                
                /*
                String password = jin.getString("password");
                String app_version = jin.getString("app_version");*/
                
                String chkSql = "SELECT survey_id, org_id, village_id, village_name, health_worker_id, household_number, household_member, survey_time, location_lat, location_lng, COALESCE(remarks, 'None'::text) AS remarks , COALESCE(return_reason, 'None'::text) AS return_reason FROM vw_surveys WHERE survey_status = 2  AND health_worker_id = " + health_worker_id;
                log.info("SQL : " + chkSql);

                ResultSet rs = db.readQuery(chkSql);
                JSONObject jobj = new JSONObject();
                JSONArray surveys = new JSONArray();
                
                while (rs.next()) {
                    JSONObject survey = new JSONObject();
                    
                    JSONObject basicInfo = new JSONObject();
                    JSONObject motherInfo = new JSONObject();
                    JSONObject childInfo = new JSONObject();
                    JSONObject referralInfo = new JSONObject();
                    JSONObject defaultersInfo = new JSONObject();
                    JSONObject deathInfo = new JSONObject();
                    JSONObject householdInfo = new JSONObject();
                    int survey_id = rs.getInt("survey_id");
                    basicInfo.put("survey_id", survey_id);
                    basicInfo.put("village_id", rs.getInt("village_id"));
                    basicInfo.put("village_name", rs.getString("village_name"));
                    basicInfo.put("household_number", rs.getString("household_number"));
                    basicInfo.put("household_member", rs.getString("household_member"));
                    basicInfo.put("survey_time", rs.getString("survey_time"));
                    basicInfo.put("location_lat", rs.getString("location_lat"));
                    basicInfo.put("location_lng", rs.getString("location_lng"));
                    basicInfo.put("remarks", rs.getString("remarks"));
                    basicInfo.put("returnReason", rs.getString("return_reason"));
                    
                    
                    String sqlmotherInfo = "SELECT survey_mother_id, survey_id, mother_info_def_id, response FROM survey_mother WHERE survey_id = " + survey_id + " ORDER BY mother_info_def_id  ASC" ;
                    String sqlchildInfo = "SELECT survey_child_id, survey_id, child_info_def_id, response FROM survey_child WHERE survey_id = " + survey_id + " ORDER BY child_info_def_id  ASC" ;
                    String sqlreferralInfo = "SELECT survey_referral_id, survey_id, referral_info_defs_id, response FROM survey_referrals WHERE survey_id = " + survey_id + " ORDER BY referral_info_defs_id ASC" ;
                    String sqldefaultersInfo = "SELECT survey_defaulter_id, survey_id, defaulters_info_def_id, response FROM survey_defaulters WHERE survey_id = " + survey_id + " ORDER BY defaulters_info_def_id ASC" ;
                    String sqldeathInfo = "SELECT survey_death_id, survey_id, death_info_def_id, response FROM survey_death WHERE survey_id = " + survey_id + " ORDER BY death_info_def_id ASC" ;
                    String sqlhouseholdInfo = "SELECT survey_household_id, survey_id, household_info_def_id, response FROM survey_household WHERE survey_id = " + survey_id + " ORDER BY household_info_def_id ASC" ;
                    
                    ResultSet rsmotherInfo = db.readQuery(sqlmotherInfo);
                    ResultSet rschildInfo = db.readQuery(sqlchildInfo);
                    ResultSet rsreferralInfo = db.readQuery(sqlreferralInfo);
                    ResultSet rsdefaultersInfo = db.readQuery(sqldefaultersInfo);
                    ResultSet rsdeathInfo = db.readQuery(sqldeathInfo);
                    ResultSet rshouseholdInfo = db.readQuery(sqlhouseholdInfo);
                    
                    while (rsmotherInfo.next()) {
                        motherInfo.put(rsmotherInfo.getString("mother_info_def_id"), rsmotherInfo.getString("response"));
                    }
                    
                    while (rschildInfo.next()) {
                        childInfo.put(rschildInfo.getString("child_info_def_id"), rschildInfo.getString("response"));
                    }
                    
                    while (rsreferralInfo.next()) {
                        referralInfo.put(rsreferralInfo.getString("referral_info_defs_id"), rsreferralInfo.getString("response"));
                    }
                    
                    while (rsdefaultersInfo.next()) {
                        defaultersInfo.put(rsdefaultersInfo.getString("defaulters_info_def_id"), rsdefaultersInfo.getString("response"));
                    }
                    
                    while (rsdeathInfo.next()) {
                        deathInfo.put(rsdeathInfo.getString("death_info_def_id"), rsdeathInfo.getString("response"));
                    }
                    
                    while (rshouseholdInfo.next()) {
                        householdInfo.put(rshouseholdInfo.getString("household_info_def_id"), rshouseholdInfo.getString("response"));
                    }
                    
                    
                    survey.put("basicInfo", basicInfo);
                    survey.put("motherInfo", motherInfo);
                    survey.put("childInfo", childInfo);
                    survey.put("referralInfo", referralInfo);
                    survey.put("defaultersInfo", defaultersInfo);
                    survey.put("deathInfo", deathInfo);
                    survey.put("householdInfo", householdInfo);
                    
                    surveys.put(survey);
                    
                    success = 1; message = "You have returned Surveys.";
                }
                
                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                
                jobj.put("success", success);
                jobj.put("message", message);
                jobj.put("surveys", surveys);
                out.print(jobj);
                log.info("JSON  SURVEY : " + jobj.toString());
                
            }catch(JSONException jsone){
                if(doLog) log.severe("JSONException : " + jsone.toString());
            }catch(SQLException sqle){
                if(doLog) log.severe("SQLException : " + sqle.toString());
            }
			
            
            if(db != null){
				db.close();
				log.info("Closing Connection --------------");
			}
            
            
            
            
        }// end getSurveys
        else if(tag.equals("editSurvey")){
            log.info("Reached edit survey");
			String dbconfig = "java:/comp/env/jdbc/database";
			BDB db = new BDB(dbconfig);log.info("Connection Opened--------------");
			int success = 0; String message = "Survey Edit Failed";
			
            try{
                JSONObject jSurvey = getInputStreamAsJson(req);
                log.info("jSurvey : " + jSurvey);
                
                JSONObject basicInfo = jSurvey.getJSONObject("basicInfo");
                JSONObject motherInfo = jSurvey.getJSONObject("motherInfo");
                JSONObject childInfo = jSurvey.getJSONObject("childInfo");
                JSONObject referralInfo = jSurvey.getJSONObject("referralInfo");
                JSONObject defaultersInfo = jSurvey.getJSONObject("defaultersInfo");
                JSONObject deathInfo = jSurvey.getJSONObject("deathInfo");
                JSONObject householdInfo = jSurvey.getJSONObject("householdInfo");
                int surveyId = basicInfo.getInt("survey_id");;
                
                
                if(surveyId != -1){
                    String sqlSurvey = "UPDATE surveys SET "
                            + " household_number='" + basicInfo.getString("houseHoldNum")  + "', " 
                            + " household_member='" + basicInfo.getString("householdMember") + "', "  
                            + " survey_time= CURRENT_TIMESTAMP, "
                            + " location_lat = '" + basicInfo.getString("latitude")  + "', "  
                            + " location_lng = '" + basicInfo.getString("longitude")  + "', " 
                            + " remarks = '" + basicInfo.getString("remarks") + "', "
                            + " survey_status = 3 WHERE survey_id = " + surveyId;
                

                    String slqMother = " UPDATE survey_mother SET  response= '" + motherInfo.getInt("1") + "' WHERE survey_id = " + surveyId + " AND mother_info_def_id = 1 ; "
                        + " UPDATE survey_mother SET  response= '" + motherInfo.getInt("2") + "' WHERE survey_id = " + surveyId + " AND mother_info_def_id =  2; "
                        + " UPDATE survey_mother SET  response= '" + motherInfo.getInt("3") + "' WHERE survey_id = " + surveyId + " AND mother_info_def_id =  3; "
                        + " UPDATE survey_mother SET  response= '" + motherInfo.getInt("4") + "' WHERE survey_id = " + surveyId + " AND mother_info_def_id =  4; "
                        + " UPDATE survey_mother SET  response= '" + motherInfo.getInt("5") + "' WHERE survey_id = " + surveyId + " AND mother_info_def_id =  5; "
                        + " UPDATE survey_mother SET  response= '" + motherInfo.getInt("6") + "' WHERE survey_id = " + surveyId + " AND mother_info_def_id =  6; "
                        + " UPDATE survey_mother SET  response= '" + motherInfo.getInt("7") + "' WHERE survey_id = " + surveyId + " AND mother_info_def_id =  7; ";
                
                    String slqChild = " UPDATE survey_child SET  response= '" + childInfo.getInt("1") + "' WHERE survey_id = " + surveyId + " AND child_info_def_id = 1 ; "
                        + " UPDATE survey_child SET  response= '" + childInfo.getInt("2") + "' WHERE survey_id = " + surveyId + " AND child_info_def_id =  2; "
                        + " UPDATE survey_child SET  response= '" + childInfo.getInt("3") + "' WHERE survey_id = " + surveyId + " AND child_info_def_id =  3; "
                        + " UPDATE survey_child SET  response= '" + childInfo.getInt("4") + "' WHERE survey_id = " + surveyId + " AND child_info_def_id =  4; "
                        + " UPDATE survey_child SET  response= '" + childInfo.getInt("5") + "' WHERE survey_id = " + surveyId + " AND child_info_def_id =  5; ";
                    
                    
                    String slqReferral = " UPDATE survey_referrals SET  response= '" + referralInfo.getInt("1") + "' WHERE survey_id = " + surveyId + " AND referral_info_defs_id = 1 ; "
                        + " UPDATE survey_referrals SET  response= '" + referralInfo.getInt("2") + "' WHERE survey_id = " + surveyId + " AND referral_info_defs_id =  2; "
                        + " UPDATE survey_referrals SET  response= '" + referralInfo.getInt("3") + "' WHERE survey_id = " + surveyId + " AND referral_info_defs_id =  3; "
                        + " UPDATE survey_referrals SET  response= '" + referralInfo.getInt("4") + "' WHERE survey_id = " + surveyId + " AND referral_info_defs_id =  4; "
                        + " UPDATE survey_referrals SET  response= '" + referralInfo.getInt("5") + "' WHERE survey_id = " + surveyId + " AND referral_info_defs_id =  5; "
                        + " UPDATE survey_referrals SET  response= '" + referralInfo.getInt("6") + "' WHERE survey_id = " + surveyId + " AND referral_info_defs_id =  6; "
                        + " UPDATE survey_referrals SET  response= '" + referralInfo.getInt("7") + "' WHERE survey_id = " + surveyId + " AND referral_info_defs_id =  7; "
                        + " UPDATE survey_referrals SET  response= '" + referralInfo.getInt("8") + "' WHERE survey_id = " + surveyId + " AND referral_info_defs_id =  8; "
                        + " UPDATE survey_referrals SET  response= '" + referralInfo.getInt("9") + "' WHERE survey_id = " + surveyId + " AND referral_info_defs_id =  9; "
                        + " UPDATE survey_referrals SET  response= '" + referralInfo.getInt("10") + "' WHERE survey_id = " + surveyId + " AND referral_info_defs_id =  10; "
                        + " UPDATE survey_referrals SET  response= '" + referralInfo.getInt("11") + "' WHERE survey_id = " + surveyId + " AND referral_info_defs_id =  11; "
                        + " UPDATE survey_referrals SET  response= '" + referralInfo.getInt("12") + "' WHERE survey_id = " + surveyId + " AND referral_info_defs_id =  12; "
                        + " UPDATE survey_referrals SET  response= '" + referralInfo.getInt("13") + "' WHERE survey_id = " + surveyId + " AND referral_info_defs_id =  13; "
                        + " UPDATE survey_referrals SET  response= '" + referralInfo.getString("14") + "' WHERE survey_id = " + surveyId + " AND referral_info_defs_id =  14; ";
                    
                    
                    String slqDefault = " UPDATE survey_defaulters SET  response= '" + defaultersInfo.getInt("1") + "' WHERE survey_id = " + surveyId + " AND defaulters_info_def_id = 1 ; "
                        + " UPDATE survey_defaulters SET  response= '" + defaultersInfo.getInt("2") + "' WHERE survey_id = " + surveyId + " AND defaulters_info_def_id =  2; "
                        + " UPDATE survey_defaulters SET  response= '" + defaultersInfo.getInt("3") + "' WHERE survey_id = " + surveyId + " AND defaulters_info_def_id =  3; "
                        + " UPDATE survey_defaulters SET  response= '" + defaultersInfo.getInt("4") + "' WHERE survey_id = " + surveyId + " AND defaulters_info_def_id =  4; ";
                    
                    
                    String slqDeath = " UPDATE survey_death SET  response= '" + deathInfo.getInt("1") + "' WHERE survey_id = " + surveyId + " AND death_info_def_id = 1 ; "
                        + " UPDATE survey_death SET  response= '" + deathInfo.getInt("2") + "' WHERE survey_id = " + surveyId + " AND death_info_def_id =  2; "
                        + " UPDATE survey_death SET  response= '" + deathInfo.getInt("3") + "' WHERE survey_id = " + surveyId + " AND death_info_def_id =  3; "
                        + " UPDATE survey_death SET  response= '" + deathInfo.getInt("4") + "' WHERE survey_id = " + surveyId + " AND death_info_def_id =  4; "
                        + " UPDATE survey_death SET  response= '" + deathInfo.getString("5") + "' WHERE survey_id = " + surveyId + " AND death_info_def_id =  5; ";
                    
                   String slqHousehold = " UPDATE survey_household SET  response= '" + householdInfo.getInt("1") + "' WHERE survey_id = " + surveyId + " AND household_info_def_id = 1 ; "
                        + " UPDATE survey_household SET  response= '" + householdInfo.getInt("2") + "' WHERE survey_id = " + surveyId + " AND household_info_def_id =  2; "
                        + " UPDATE survey_household SET  response= '" + householdInfo.getInt("3") + "' WHERE survey_id = " + surveyId + " AND household_info_def_id =  3; ";
                    
                    
                    log.info("\n\nsqlSurvey : SQl : \n" + sqlSurvey );
                    log.info("\n\nslqMother : " + slqMother);
                    log.info("\n\nslqChild : " + slqChild);
                    log.info("\n\nslqReferral : " + slqReferral);
                    log.info("\n\nslqDefault : " + slqDefault);
                    log.info("\n\nslqDeath : " + slqDeath);
                    log.info("\n\nslqHousehold : " + slqHousehold);
                    
                    String res = db.executeUpdate(sqlSurvey);
                    res = db.executeUpdate(slqMother);
                    res = db.executeUpdate(slqChild);
                    res = db.executeUpdate(slqReferral);
                    res = db.executeUpdate(slqDefault);
                    res = db.executeUpdate(slqDeath);
                    res = db.executeUpdate(slqHousehold);
                    
                    if(res == null){
                        success = 1; message = "Survey Updated Successfully";      
                    }else{
                        success = 0; message = "Survey Update  Failed"; 
                    }
                    
                   
                }
                
                

                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                JSONObject jobj = new JSONObject();
                jobj.put("success", success);
                jobj.put("message", message);
                out.print(jobj);
                log.info("JSON  SURVEY: " + jobj.toString());
            }catch(JSONException jsone){
                if(doLog) log.severe("JSONException : " + jsone.toString());
            }catch(Exception sqle){
                if(doLog) log.severe("Exception : " + sqle.toString());
            }
			
            
            if(db != null){
				db.close();
				log.info("Closing Connection --------------");
			}
            
        } //end edit survey
        else if(tag.equals("form100")){
            log.info("Reached post form100");
			String dbconfig = "java:/comp/env/jdbc/database";
			BDB db = new BDB(dbconfig);log.info("Connection Opened--------------");
			int success = 0; String message = "An error Occurred";
			
            try{
                JSONObject jsonObject = getInputStreamAsJson(req);
                log.info("jsonObject 100 : " + jsonObject);

                String sqlS = "INSERT INTO survey_100( org_id, health_worker_id, form_serial, patient_name, patient_gender, patient_age_type, patient_age, community_healt_unit, link_health_facility_id, referral_reason, " 
                            + " treatment, comments, village_id, community_unit, receiving_officer_name, receiving_officer_profession, health_facility_name, action_taken ) "
                            + " VALUES ( " 
                            + jsonObject.getInt("org_id") + ", "
                            + jsonObject.getInt("health_worker_id")  + ", '" 
                            + jsonObject.getString("formSerial") + "', '"
                            + jsonObject.getString("patientName") + "', '"
                            + jsonObject.getString("patientGender") + "', '"
                            + jsonObject.getString("patientAgeType") + "', '"
                            + jsonObject.getString("patientAge") + "', '"
                            + jsonObject.getString("communityHealtUnit") + "', '"
                            + jsonObject.getString("linkHealthFacility") + "', '"
                            + jsonObject.getString("referralReason") + "', '"
                            + jsonObject.getString("treatment") + "', '"
                            + jsonObject.getString("comments") + "', '"
                            + jsonObject.getString("village") + "', '"
                            + jsonObject.getString("communityUnit") + "', '"
                            + jsonObject.getString("receivingOfficerName") + "', '"
                            + jsonObject.getString("receivingOfficerProfession") + "', '"
                            + jsonObject.getString("healthFacilityName") + "', '"
                            + jsonObject.getString("actionTaken") + "')";

                log.info("INSERT Survey .................... SQl : \n" + sqlS );
                
                String rs = db.executeQuery(sqlS);
                
                if(rs == null){
                    success = 1; message = "Referral Submited Successfully"; 
                }

                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                JSONObject jobj = new JSONObject();
                jobj.put("success", success);
                jobj.put("message", message);
                out.print(jobj);
                log.info("JSON  SURVEY 100: " + jobj.toString());
            }catch(JSONException jsone){
                if(doLog) log.severe("JSONException : " + jsone.toString());
            }catch(Exception e){
                if(doLog) log.severe("Exception : " + e.toString());
            }
			
            
            if(db != null){
				db.close();
				log.info("Closing Connection --------------");
			}
            
        } 
        else if(tag.equals("getSublocations") || tag.equals("getVillages") || tag.equals("getLinkFacilities") ){
            
            log.info("Reached : " + tag);
			String dbconfig = "java:/comp/env/jdbc/database";
			BDB db = new BDB(dbconfig);log.info("Connection Opened--------------");
			int success = 0; String message = "No Data available";
			
            try{
                JSONObject jin = getInputStreamAsJson(req);
                log.info("jin : " + jin);
                String sub_location_id = "";
                String sql = "", opt0Title = "";
                if(tag.equals("getSublocations") ){
                    opt0Title = "Select Sub Location";
                    sql = "SELECT sub_location_id AS id , sub_location_name AS name FROM sub_locations;";    
                }else if(tag.equals("getVillages")){
                    opt0Title = "Select Village";
                    sub_location_id = jin.getString("sub_location_id");
                    sql = "SELECT village_id  AS id , village_name AS name FROM villages WHERE sub_location_id = " + sub_location_id;
                }else if(tag.equals("getLinkFacilities") ){
                    opt0Title = "Select Link Facility";
                    sub_location_id = jin.getString("sub_location_id");
                    sql = "SELECT link_health_facility_id AS id, link_health_facility_name AS name FROM link_health_facilities WHERE sub_location_id = " + sub_location_id;
                }
                
                ResultSet rs = db.readQuery(sql);
                JSONObject jobj = new JSONObject();
                JSONArray areas = new JSONArray();
                JSONObject opt0 = new JSONObject();
                opt0.put("id", 0);
                opt0.put("name", opt0Title);
                areas.put(opt0);
                
                while (rs.next()) {
                    JSONObject area = new JSONObject();
                    area.put("id", rs.getInt("id"));
                    area.put("name", rs.getString("name"));
                    
                    areas.put(area);
                }
                success = 1; 
                if(tag.equals("getSublocations") ){
                    message = "Sub Locations Loaded successfully.";
                }else if(tag.equals("getVillages")){
                    message = "Vilages Loaded successfully.";
                }else if(tag.equals("getLinkFacilities") ){
                    message = "Link Health Facilities Loaded successfully.";
                }
                
                log.info("SQL : " + sql );
                
                resp.setContentType("application/json");
                PrintWriter out = resp.getWriter();
                
                jobj.put("success", success);
                jobj.put("message", message);
                jobj.put("areas", areas);
                out.print(jobj);
                log.info("JSON  " + tag + " : " + jobj.toString());
                
            }catch(JSONException jsone){
                if(doLog) log.severe("JSONException : " + jsone.toString());
            }catch(SQLException sqle){
                if(doLog) log.severe("SQLException : " + sqle.toString());
            }
            
            if(db != null){
				db.close();
				log.info("Closing Connection --------------");
			}
        }//getSublocations || villages
        
        
	}//dopost
   
    
    public static JSONObject getInputStreamAsJson(HttpServletRequest req){
		JSONObject jsonObject = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(req.getInputStream()));
			String json = "";
			if(br != null){
				json = br.readLine();
			}
			if(doLog) log.info("json : " + json);
			
			if(json == null){
				jsonObject = null;
			}else{
				jsonObject = new JSONObject(json);
			}

		} catch (JSONException | IOException e1) {
			if(doLog) log.severe("JSONException : " + e1.toString());
		}
		return jsonObject;
		
	}
	
}


