package com.airtel.buildingconnectivitymmi.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.airtel.buildingconnectivitymmi.BuildConfig;
import com.airtel.buildingconnectivitymmi.R;
import com.airtel.buildingconnectivitymmi.util.Constant;
import com.airtel.buildingconnectivitymmi.util.KeyboardUtils;
import com.airtel.buildingconnectivitymmi.util.MethodsUtil;
import com.airtel.buildingconnectivitymmi.util.NetworkUtils;
import com.airtel.buildingconnectivitymmi.util.UIUtils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = Login.class.getSimpleName();
    private TextView tvBack, tvCountdown;
    private ImageView airtelLogo;
    private TextInputEditText editMobileNo;
    private Button btnNext;
    private String getLoginEndUrl = "v1/login";
    private String getGenerateOtp = "v1/otp_generate";
    private Dialog mDialog;
    private String mobile, key;
    private boolean isKeyboardVisible;
    private final String[] permissions = {Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkAllPermission();

        TextView tvVersionName = findViewById(R.id.tvVersionName);
        tvVersionName.setText("Version : " + BuildConfig.VERSION_NAME);

        initViews();
        tvBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mobile = bundle.getString("mobile");
            if (!TextUtils.isEmpty(mobile)) {
                editMobileNo.setText(mobile);
            }
        }

        //Managing UI in case of orientation change
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) airtelLogo.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            airtelLogo.setLayoutParams(params);
        } else {
            // In portrait
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) airtelLogo.getLayoutParams();
            params.setMargins(0, 70, 0, 70);
            airtelLogo.setLayoutParams(params);
        }

        KeyboardUtils.addKeyboardToggleListener(this, new KeyboardUtils.SoftKeyboardToggleListener() {
            @Override
            public void onToggleSoftKeyboard(boolean isVisible) {
                isKeyboardVisible = isVisible;
            }
        });


        if (!MethodsUtil.isOTPCacheOver(getApplicationContext())) {
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setEnabled(false);
            btnNext.setBackground(getResources().getDrawable(R.drawable.button_round_grey_disable));

            SharedPreferences sharedPreferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
            if (sharedPreferences.getLong("otp_cache_time", 0) != 0) {
                if (Calendar.getInstance().getTime().getTime() < (sharedPreferences.getLong("otp_cache_time", 0))) {
                    //reboot cache time is over
                    long t = sharedPreferences.getLong("otp_cache_time", 0) - Calendar.getInstance().getTime().getTime();
                    Calendar rem = Calendar.getInstance();
                    rem.setTimeInMillis(t);
                    int sec = rem.get(Calendar.SECOND);


                    CountDownTimer downTimer = new CountDownTimer(sec * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            tvCountdown.setVisibility(View.VISIBLE);
                            tvCountdown.setText("Please wait for " + (millisUntilFinished + 1000) / 1000 + " seconds");
                            //Toast.makeText(ForgetPassword.this, ""+millisUntilFinished/1000, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFinish() {
                            tvCountdown.setVisibility(View.GONE);
                            btnNext.setVisibility(View.VISIBLE);
                            btnNext.setEnabled(true);
                            btnNext.setBackground(getResources().getDrawable(R.drawable.button_round));
                        }
                    };
                    downTimer.start();
                }
            }
        }
    }

    public void initViews() {
        tvBack = findViewById(R.id.tvBack);
        editMobileNo = findViewById(R.id.editMobileNo);
        btnNext = findViewById(R.id.btnNext);
        airtelLogo = findViewById(R.id.airtelLogo);
        tvCountdown = findViewById(R.id.tvCountdown);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) airtelLogo.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            airtelLogo.setLayoutParams(params);
        } else {
            // In portrait
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) airtelLogo.getLayoutParams();
            params.setMargins(0, 70, 0, 70);
            airtelLogo.setLayoutParams(params);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tvBack: {
                onBackPressed();
                break;
            }
            case R.id.btnNext: {
                if (isKeyboardVisible) {
                    // Check if no view has focus:
                    View view = this.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }

                mobile = editMobileNo.getText().toString().trim();
                key = md5(mobile + Constant.STATIC_KEY);

                if (NetworkUtils.isNetworkConnected(Login.this)) {
                    callLoginApi(); //Calling reset password API if internet connected
                } else {
                    UIUtils.makeCustomSnackbar(Login.this, getString(R.string.not_connected_to_internet), Constant.SHOW_OK).show();
                }
                break;
            }
        }
    }

    private void callLoginApi() {
        if (mobile.isEmpty()) {
            UIUtils.makeCustomSnackbar(this, "Field can not be empty!", Constant.SHOW_OK).show();
        } else if (mobile.length() != 10) {
            UIUtils.makeCustomSnackbar(this, "Enter a valid Mobile Number!", Constant.SHOW_OK).show();
        } else {
            try {
                mDialog = UIUtils.showProgressDialog(Login.this);
                mDialog.setCancelable(false);

                JSONObject mainJObject = new JSONObject();
                mainJObject.put("mobile", mobile);
                mainJObject.put("app_name", "building");
                mainJObject.put("token", "");

                RequestQueue queue = Volley.newRequestQueue(this);
                String url = BuildConfig.baseUrl + getLoginEndUrl;
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, mainJObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                UIUtils.dismissDialog(mDialog);
                                if (response != null) {

                                    String statusCode = response.optString("StatusCode");
                                    String msg = response.optString("Message");
                                    if (statusCode.equals("200")) {
                                        JSONObject jsonObject = response.optJSONObject("data");
                                        if (jsonObject != null) {
                                            SharedPreferences loginPref = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
                                            String eng_name = jsonObject.optString("name");
                                            loginPref.edit().putString("eng_name", eng_name).apply();
                                            String circle = jsonObject.optString("circle");
                                            loginPref.edit().putString("circle", circle).apply();
                                            String tl_name = jsonObject.optString("tl_name");
                                            loginPref.edit().putString("tl_name", tl_name).apply();
                                            String tl_mobile = jsonObject.optString("tl_mobile");
                                            loginPref.edit().putString("tl_mobile", tl_mobile).apply();
                                        }

                                        callGenerateOtp();

                                    } else {
                                        UIUtils.makeCustomSnackbar(Login.this, msg, Constant.SHOW_OK).show();
                                    }

                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                UIUtils.dismissDialog(mDialog);
                                if (error.networkResponse != null) {
                                    String msg = MethodsUtil.getStatusCodeMessage(error.networkResponse.data);
//                                    Toast.makeText(ForgetPassword.this, msg, Toast.LENGTH_SHORT).show();
                                    UIUtils.makeCustomSnackbar(Login.this, msg, Constant.SHOW_OK).show();
                                } else {
//                                    Toast.makeText(ForgetPassword.this, R.string.error_generic, Toast.LENGTH_SHORT).show();
                                    UIUtils.makeCustomSnackbar(Login.this, getString(R.string.error_generic), Constant.SHOW_OK).show();
                                }
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("key", key);
                        params.put("Content-Type", "application/json");
                        String auth = MethodsUtil.getAuthOtp();
                        params.put("Authorization", auth);

                        return params;
                    }

                };
                //Increasing timeout period
                getRequest.setRetryPolicy(new RetryPolicy() {
                    @Override
                    public int getCurrentTimeout() {

                        return 30000;
                    }

                    @Override
                    public int getCurrentRetryCount() {

                        return 0;
                    }

                    @Override
                    public void retry(VolleyError error) throws VolleyError {
                        UIUtils.dismissDialog(mDialog);
                        throw error;
                    }
                });
                queue.add(getRequest);

            } catch (Exception e) { // for caught any exception during the excecution of the service
                UIUtils.makeCustomSnackbar(Login.this, "Something went wrong!", Constant.SHOW_OK).show();
            }
        }
    }

    private void callGenerateOtp() {
        if (mobile.isEmpty()) {
            UIUtils.makeCustomSnackbar(this, "Field can not be empty!", Constant.SHOW_OK).show();
        } else if (mobile.length() != 10) {
            UIUtils.makeCustomSnackbar(this, "Enter a valid Mobile Number!", Constant.SHOW_OK).show();
        } else {
            try {
                mDialog = UIUtils.showProgressDialog(Login.this);
                mDialog.setCancelable(false);

                JSONObject mainJObject = new JSONObject();
                mainJObject.put("mobile", mobile);
                mainJObject.put("type", "building_connectivity");

                RequestQueue queue = Volley.newRequestQueue(this);
                String url = BuildConfig.baseUrl + getGenerateOtp;
                JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, mainJObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                UIUtils.dismissDialog(mDialog);
                                if (response != null) {

                                    String statusCode = response.optString("StatusCode");
                                    String msg = response.optString("Message");
                                    if (statusCode.equals("200")) {

                                        JSONObject jsonObject = response.optJSONObject("data");
                                        if (jsonObject != null) {

                                            String token = jsonObject.optString("token");

                                            if (!token.equalsIgnoreCase("")) {
                                                startOtpActivity(token);
                                            }
                                        }

                                    } else {
                                        tvCountdown.setVisibility(View.GONE);
                                        btnNext.setVisibility(View.VISIBLE);
                                        btnNext.setEnabled(true);
                                        btnNext.setBackground(getResources().getDrawable(R.drawable.button_round));
                                        UIUtils.makeCustomSnackbar(Login.this, msg, Constant.SHOW_OK).show();
                                    }

                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                UIUtils.dismissDialog(mDialog);
                                if (error.networkResponse != null) {
                                    String msg = MethodsUtil.getStatusCodeMessage(error.networkResponse.data);
                                    UIUtils.makeCustomSnackbar(Login.this, msg, Constant.SHOW_OK).show();
                                } else {
                                    UIUtils.makeCustomSnackbar(Login.this, getString(R.string.error_generic), Constant.SHOW_OK).show();
                                }
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("key", key);
                        params.put("Content-Type", "application/json");
                        String auth = MethodsUtil.getAuthOtp();
                        params.put("Authorization", auth);
                        return params;
                    }

                };
                //Increasing timeout period
                getRequest.setRetryPolicy(new RetryPolicy() {
                    @Override
                    public int getCurrentTimeout() {

                        return 30000;
                    }

                    @Override
                    public int getCurrentRetryCount() {

                        return 0;
                    }

                    @Override
                    public void retry(VolleyError error) throws VolleyError {
                        UIUtils.dismissDialog(mDialog);
                        throw error;
                    }
                });
                queue.add(getRequest);

            } catch (Exception e) { // for caught any exception during the excecution of the service
                UIUtils.makeCustomSnackbar(Login.this, "Something went wrong!", Constant.SHOW_OK).show();
            }
        }
    }

    private void startOtpActivity(String token) {
        MethodsUtil.setResendCacheTime(Login.this, 15);
        Intent intent = new Intent(Login.this, OtpActivity.class);
        Bundle b = new Bundle();
        b.putString("token", token);
        b.putString("mobile", mobile);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException ignored) {
        }
        return "";
    }


    //--------------------------------------CHECK PERMISSION FOR THE USER--------------------------------------------------------

    private void checkAllPermission() {
        if (checkSelfPermissionMethod() != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, permissions, Constant.MY_PERMISSIONS_REQUEST_ALL);
        }
    }

    private int checkSelfPermissionMethod() {
        int p = 0;
        for (String permission : permissions) {
            p = p + ActivityCompat.checkSelfPermission(this, permission);
        }
        return p;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constant.MY_PERMISSIONS_REQUEST_ALL) {
            int grantResultAll = 0;
            for (int grantResult : grantResults) {
                grantResultAll = grantResultAll + grantResult;
            }
            if (grantResultAll != PackageManager.PERMISSION_GRANTED) {
                checkAllPermission();
            }
        }
    }
}
