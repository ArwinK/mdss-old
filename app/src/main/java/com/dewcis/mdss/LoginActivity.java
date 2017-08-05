package com.dewcis.mdss;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.dewcis.mdss.model.MSession;
import com.dewcis.mdss.model.Success;
import com.dewcis.mdss.model.User;
import com.dewcis.mdss.utils.MUtil;
import com.dewcis.mdss.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


/**
 * A login screen that offers login via email/password.
 */

/**
 * Created by henriquedn on 4/14/15.
 * henrydkm@gmail.com
 * henry.maina@dewcis.com
 */
public class LoginActivity extends Activity {
    String TAG = MApplication.TAG;
    static boolean doLog = MApplication.LOGDEBUG;
    static String CTAG = LoginActivity.class.getName() + " : " ;
    private EditText txtEmail, txtPassword;
    private Button btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MSession session = new MSession(getApplicationContext());

        if(session.isLoggedIn()){
            Log.i(TAG, CTAG + " User : " + session.getUser().toJson());

            Intent i = new Intent(getApplicationContext(), Home.class);
            startActivity(i);
            LoginActivity.this.finish();
        }

        // Set up the login form.
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }


    public void attemptLogin() {

        // Reset errors.
        txtEmail.setError(null);
        txtPassword.setError(null);

        // Store values at the time of the login attempt.
        String mobile_num = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        boolean ok = true;
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(mobile_num)) {
            txtEmail.setError(getString(R.string.error_field_required));
            ok = false; return;
        }/* else if (!isEmailValid(email)) {
            txtEmail.setError(getString(R.string.error_invalid_email));
            ok = false; return;
        }*/

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            txtPassword.setError(getString(R.string.error_invalid_password));
            ok = false; return;
        }

        if(ok){
            if(MUtil.isNetworkConnected(getApplicationContext())){
                JSONObject details = new JSONObject();
                try {
                    details.put(User.MOBILE_NUM, mobile_num);
                    details.put("password", password);
                    details.put("app_version", MUtil.getAppVersionName(getApplicationContext()));

                } catch (JSONException e) {
                    Log.e(TAG, LoginActivity.class.getName() + " : " + e.toString());
                }
                authenticate(getApplicationContext(), details);
            }else{
                Toast.makeText(getApplicationContext(), "No Internet Connection Detected", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
    public void authenticate(final Context context, final JSONObject details){
        final ProgressDialog pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCancelable(false);

        MUtil.showProgressDialog(pDialog);
        String url = MApplication.url + "authenticate";
        //if(doLog)

        Log.i(MApplication.TAG, LoginActivity.class.getName() + " : Details " + details.toString());
        JsonObjectRequest mVerifyReq = new JsonObjectRequest(Request.Method.POST, url, details,
                new Response.Listener<JSONObject>() {
                    public void onResponse(JSONObject response) {
                        Success success = Success.makeFromJson(response);
                        if(doLog) Log.i(TAG, CTAG + response.toString());
                        Log.i(TAG, LoginActivity.class.getName() + "Response : " +response.toString() + "\nSuccess : " + success.isSuccess() + "\nMessage : " + success.getMessage());
                        MUtil.hideProgressDialog(pDialog);
                        if(success.isSuccess()){
                            Toast.makeText(context, success.getMessage(), Toast.LENGTH_SHORT).show();
                            try{
                                User user = User.makeFromJson(response.getJSONObject("user_details"));
                                MSession session = new MSession(context);
                                session.setUser(user);
                                session.save();

                                Log.i(TAG, CTAG + "> Saved User : " + session.getUser().toJson());

                                Intent i = new Intent(getApplicationContext(), Home.class);
                                startActivity(i);
                                LoginActivity.this.finish();

                            }catch (JSONException e){
                                Crouton.makeText(LoginActivity.this , "Sorry, Could Not Create Session" , Style.ALERT).show();
                            }

                        }else{
                            Crouton.makeText(LoginActivity.this , success.getMessage() , Style.ALERT).show();
                        }
                    };
                } ,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(MApplication.TAG, error.toString());
                        MUtil.hideProgressDialog(pDialog);
                        Crouton.makeText(LoginActivity.this, VolleyErrorHelper.getMessage(error, context), Style.ALERT).show();
                    }
                }){
        };
        mVerifyReq.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MApplication.getInstance().addToRequestQueue(mVerifyReq, "authenticate");
    }


}



