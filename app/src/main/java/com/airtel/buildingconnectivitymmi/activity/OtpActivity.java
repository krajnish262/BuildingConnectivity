package com.airtel.buildingconnectivitymmi.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.airtel.buildingconnectivitymmi.BuildConfig;
import com.airtel.buildingconnectivitymmi.R;
import com.airtel.buildingconnectivitymmi.util.Constant;
import com.airtel.buildingconnectivitymmi.util.GenericTextWatcher;
import com.airtel.buildingconnectivitymmi.util.KeyboardUtils;
import com.airtel.buildingconnectivitymmi.util.MethodsUtil;
import com.airtel.buildingconnectivitymmi.util.NetworkUtils;
import com.airtel.buildingconnectivitymmi.util.OtpListenerImpl;
import com.airtel.buildingconnectivitymmi.util.SmsListener;
import com.airtel.buildingconnectivitymmi.util.SmsReceiver;
import com.airtel.buildingconnectivitymmi.util.UIUtils;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OtpActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = OtpActivity.class.getSimpleName();
    private TextView tvSentDesc, tvCountdown, tvCountdownResend, tvResendSMS, tvEditNumber;
    private ImageView airtelLogo;
    private Button btnNext;
    private String getGenerateOtp = "v1/otp_generate";
    private String getOtpVerifyEndUrl = "v1/verify_otp";
    private Dialog mDialog;
    private String mobile, key;
    private boolean isKeyboardVisible;
    private EditText[] editTextOtp;
    private static String token = null;

    private EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four;
    SmsReceiver objectBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        registerMyReciever();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            token = bundle.getString("token");
            mobile = bundle.getString("mobile");
            key = md5(mobile + Constant.STATIC_KEY);
        }

        otp_textbox_one = findViewById(R.id.otp_edit_box1);
        otp_textbox_two = findViewById(R.id.otp_edit_box2);
        otp_textbox_three = findViewById(R.id.otp_edit_box3);
        otp_textbox_four = findViewById(R.id.otp_edit_box4);

        editTextOtp = new EditText[]{otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four};

        otp_textbox_one.addTextChangedListener(new GenericTextWatcher(otp_textbox_one, editTextOtp));
        otp_textbox_two.addTextChangedListener(new GenericTextWatcher(otp_textbox_two, editTextOtp));
        otp_textbox_three.addTextChangedListener(new GenericTextWatcher(otp_textbox_three, editTextOtp));
        otp_textbox_four.addTextChangedListener(new GenericTextWatcher(otp_textbox_four, editTextOtp));


        /*otp_textbox_one.setOnTouchListener(new GenericTextWatcher(otp_textbox_one, editTextOtp));
        otp_textbox_two.setOnTouchListener(new GenericTextWatcher(otp_textbox_two, editTextOtp));
        otp_textbox_three.setOnTouchListener(new GenericTextWatcher(otp_textbox_three, editTextOtp));
        otp_textbox_four.setOnTouchListener(new GenericTextWatcher(otp_textbox_four, editTextOtp));*/


        initViews();
        disableNext();
        tvSentDesc.setText(getString(R.string.we_have_sent_code) + " +91" + mobile + ".");
        btnNext.setOnClickListener(this);
        tvResendSMS.setOnClickListener(this);
        tvEditNumber.setOnClickListener(this);
        /*root_otp_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OtpActivity.this, "HIII", Toast.LENGTH_SHORT).show();
            }
        });*/


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
        defaultInit();
        GenericTextWatcher.bindListener(new OtpListenerImpl() {
            @Override
            public void onOtpCompleted(String otp) {
                super.onOtpCompleted(otp);
                //start action on otp completed
                /*tvCountdownResend.setVisibility(View.GONE);
                tvResendSMS.setVisibility(View.VISIBLE);
                tvResendSMS.setEnabled(true);
                tvResendSMS.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));*/
                startAutoNext(otp);
            }

            @Override
            public void onOtpNotCompleted() {
                super.onOtpNotCompleted();
//                btnNext.setVisibility(View.VISIBLE);
//                btnNext.setEnabled(true);
//                btnNext.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_round_oval, null));
            }
        });

        //For auto-reading OTP
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                unregisterMyReciever();

                String[] resOtp = messageText.split("");
                resetOTPBox(resOtp.length);
                for (int x = 0; x < resOtp.length; x++) {
                    editTextOtp[x].setText(resOtp[x]);
                    editTextOtp[x].setInputType(InputType.TYPE_CLASS_NUMBER);
                }
                editTextOtp[resOtp.length].setSelection(editTextOtp[resOtp.length].getText().length());
                editTextOtp[resOtp.length].requestFocus();

            }
        });

        resendCheckCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterMyReciever();
    }

    private void registerMyReciever() {
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        objectBroadcast=new SmsReceiver();
        registerReceiver(objectBroadcast,intentFilter);
    }
    private void unregisterMyReciever() {
        if (objectBroadcast!=null)
        {
            try {
                unregisterReceiver(objectBroadcast);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void disableNext() {
        btnNext.setVisibility(View.VISIBLE);
        btnNext.setEnabled(false);
        btnNext.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_round_grey_disable, null));
    }

    private void resendCheckCache() {

        if (!MethodsUtil.isResendCacheOver(getApplicationContext())) {
            tvResendSMS.setVisibility(View.VISIBLE);
            tvResendSMS.setEnabled(false);
            tvResendSMS.setTextColor(ResourcesCompat.getColor(getResources(), R.color.grey, null));

            SharedPreferences sharedPreferences = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
            if (sharedPreferences.getLong("resend_cache_time", 0) != 0) {
                if (Calendar.getInstance().getTime().getTime() < (sharedPreferences.getLong("resend_cache_time", 0))) {
                    //reboot cache time is over
                    long t = sharedPreferences.getLong("resend_cache_time", 0) - Calendar.getInstance().getTime().getTime();
                    Calendar rem = Calendar.getInstance();
                    rem.setTimeInMillis(t);
                    int sec = rem.get(Calendar.SECOND);


                    tvCountdownResend.setVisibility(View.VISIBLE);
                    CountDownTimer downTimer = new CountDownTimer(sec * 1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long sec = (millisUntilFinished + 1000) / 1000;
                            String strSec = String.format(Locale.US, "%02d", sec);
                            tvCountdownResend.setText("(0:"+strSec + ")");
                        }

                        @Override
                        public void onFinish() {
                            tvCountdownResend.setVisibility(View.GONE);
                            tvResendSMS.setVisibility(View.VISIBLE);
                            tvResendSMS.setEnabled(true);
                            tvResendSMS.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
                        }
                    };
                    downTimer.start();
                }
            }
        }
    }

    private void defaultInit() {
        otp_textbox_one.setSelection(otp_textbox_one.getText().length());
        otp_textbox_one.setInputType(InputType.TYPE_CLASS_NUMBER);
        otp_textbox_one.requestFocus();
    }

    private void startAutoNext(String opt_recieved) {
        /*btnNext.setVisibility(View.VISIBLE);
        btnNext.setEnabled(false);
        btnNext.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.button_round_oval_disabled, null));*/
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
        if (NetworkUtils.isNetworkConnected(OtpActivity.this)) {

            //String opt_recieved = editTextOtp[0].getText().toString() + editTextOtp[1].getText().toString() + editTextOtp[2].getText().toString() + editTextOtp[3].getText().toString();
            if (!TextUtils.isEmpty(opt_recieved) && !TextUtils.isEmpty(token) && !TextUtils.isEmpty(mobile)) {
                callOtpApi(opt_recieved, token); //Calling reset password API if internet connected
            } else {
                UIUtils.makeCustomSnackbar(OtpActivity.this, getString(R.string.fields_can_not_be_empty), Constant.SHOW_OK).show();
            }

        } else {
            UIUtils.makeCustomSnackbar(OtpActivity.this, getString(R.string.not_connected_to_internet), Constant.SHOW_OK).show();
        }
    }

    public void initViews() {
//        root_otp_layout = findViewById(R.id.root_otp_layout);
        airtelLogo = findViewById(R.id.airtelLogo);
        tvCountdown = findViewById(R.id.tvCountdown);
        tvCountdownResend = findViewById(R.id.tvCountdownResend);
        btnNext = findViewById(R.id.btnNextOtp);
        tvSentDesc = findViewById(R.id.tvSentDesc);
        tvResendSMS = findViewById(R.id.tvResendSMS);
        tvEditNumber = findViewById(R.id.tvEditNumber);
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
            case R.id.btnNextOtp:
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
                if (NetworkUtils.isNetworkConnected(OtpActivity.this)) {

                    String otp = editTextOtp[0].getText().toString() + editTextOtp[1].getText().toString() + editTextOtp[2].getText().toString() + editTextOtp[3].getText().toString();
                    if (!TextUtils.isEmpty(otp) && !TextUtils.isEmpty(token) && !TextUtils.isEmpty(mobile)) {
                        callOtpApi(otp, token); //Calling reset password API if internet connected
                    } else {
                        Toast.makeText(this, "Some fields are empty", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    UIUtils.makeCustomSnackbar(OtpActivity.this, getString(R.string.not_connected_to_internet), Constant.SHOW_OK).show();
                }
                break;
            case R.id.tvResendSMS:
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
                if (NetworkUtils.isNetworkConnected(OtpActivity.this)) {
                    callGenerateOtp();
                } else {
                    UIUtils.makeCustomSnackbar(OtpActivity.this, getString(R.string.not_connected_to_internet), Constant.SHOW_OK).show();
                }
                break;
            case R.id.tvEditNumber:
                resetOTPBox(4);
                unregisterMyReciever();
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
                startLoginActivity();
                break;
        }
    }

    private void resetOTPBox(int length) {
        for (int i = 0; i < length; i++) {
            editTextOtp[i].setText("");
        }
    }

    private void startLoginActivity() {
        Intent intent = new Intent(OtpActivity.this, MainActivity.class);
        Bundle b = new Bundle();
        b.putString("mobile", mobile);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    private void callOtpApi(String otp, String token) {

        JSONObject mainJObject = new JSONObject();

        try {

            mainJObject.put("mobile", mobile);
            mainJObject.put("otp", otp);
            mainJObject.put("token", token);
        } catch (JSONException ignored) {
        }

        try {
            mDialog = UIUtils.showProgressDialog(OtpActivity.this);
            mDialog.setCancelable(false);

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = BuildConfig.baseUrl + getOtpVerifyEndUrl;
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
                                        SharedPreferences loginPref = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
                                        loginPref.edit().putString("token", token).apply();
                                        loginPref.edit().putString("user_id", mobile).apply();

                                        // Toast.makeText(ForgetPassword.this, msg, Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                                        intent.putExtra("mobile", mobile);
                                        intent.putExtra("token", token);
                                        intent.putExtra("key", key);
                                        startActivity(intent);
                                        finish();
                                        OtpActivity.this.overridePendingTransition(R.anim.zoom, R.anim.zoomout);
                                    }

                                } else {
//                                    Toast.makeText(ForgetPassword.this, msg, Toast.LENGTH_LONG).show();
                                    UIUtils.makeCustomSnackbar(OtpActivity.this, msg, Constant.SHOW_OK).show();
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
//                                Toast.makeText(ForgetPassword.this, msg, Toast.LENGTH_SHORT).show();
                                UIUtils.makeCustomSnackbar(OtpActivity.this, msg, Constant.SHOW_OK).show();
                            } else {
//                                Toast.makeText(ForgetPassword.this, R.string.error_generic, Toast.LENGTH_SHORT).show();
                                UIUtils.makeCustomSnackbar(OtpActivity.this, getString(R.string.error_generic), Constant.SHOW_OK).show();
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

//            Toast.makeText(ForgetPassword.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            UIUtils.makeCustomSnackbar(OtpActivity.this, "Something went wrong!", Constant.SHOW_OK).show();
        }

    }

    private void callGenerateOtp() {


        if (mobile.isEmpty()) {
//            Toast.makeText(this, "Field can not be empty!", Toast.LENGTH_SHORT).show();
            UIUtils.makeCustomSnackbar(this, "Field can not be empty!", Constant.SHOW_OK).show();
        } else if (mobile.length() != 10) {
//            Toast.makeText(this, "Enter a valid Mobile Number!", Toast.LENGTH_SHORT).show();
            UIUtils.makeCustomSnackbar(this, "Enter a valid Mobile Number!", Constant.SHOW_OK).show();
        } else {

            try {
                registerMyReciever();
                mDialog = UIUtils.showProgressDialog(OtpActivity.this);
                mDialog.setCancelable(false);

                MethodsUtil.setResendCacheTime(OtpActivity.this, 15);
                resendCheckCache();


                JSONObject mainJObject = new JSONObject();
                mainJObject.put("mobile", mobile);
                mainJObject.put("type", "engineer");

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

                                            token = jsonObject.optString("token");

                                        }

                                    } else {
                                        UIUtils.makeCustomSnackbar(OtpActivity.this, msg, Constant.SHOW_OK).show();
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
                                    UIUtils.makeCustomSnackbar(OtpActivity.this, msg, Constant.SHOW_OK).show();
                                } else {
//                                    Toast.makeText(ForgetPassword.this, R.string.error_generic, Toast.LENGTH_SHORT).show();
                                    UIUtils.makeCustomSnackbar(OtpActivity.this, getString(R.string.error_generic), Constant.SHOW_OK).show();
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

//                Toast.makeText(ForgetPassword.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                UIUtils.makeCustomSnackbar(OtpActivity.this, "Something went wrong!", Constant.SHOW_OK).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        OtpActivity.this.overridePendingTransition(R.anim.fade, R.anim.fadeout);
    }

    public static final String md5(final String s) {
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
}
