package com.airtel.buildingconnectivitymmi.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;

import com.airtel.buildingconnectivitymmi.BuildConfig;
import com.airtel.buildingconnectivitymmi.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.view.View.GONE;

public class MethodsUtil {

    public static boolean isResendCacheOver(Context ctx) {
        boolean flag = true;
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(Constant.LOGIN_PREF, ctx.MODE_PRIVATE);
        if (sharedPreferences.getLong("resend_cache_time", 0) != 0) {
            if (Calendar.getInstance().getTime().getTime() < (sharedPreferences.getLong("resend_cache_time", 0))) {
                //reboot cache time is over
                flag = false;
            }
        }
        return flag;
    }

    public static void setResendCacheTime(Context ctx, int seconds) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(Constant.LOGIN_PREF, ctx.MODE_PRIVATE);
        Calendar currentTime = Calendar.getInstance();
        Calendar reboot_time = Calendar.getInstance();

        reboot_time.set(Calendar.HOUR, currentTime.get(Calendar.HOUR));
        reboot_time.set(Calendar.MINUTE, currentTime.get(Calendar.MINUTE));
        reboot_time.set(Calendar.SECOND, currentTime.get(Calendar.SECOND) + seconds);

        long reeboot = reboot_time.getTime().getTime();
        sharedPreferences.edit().putLong("resend_cache_time", reeboot).apply();
    }

    public static String getAuthOtp() {
        String credentials = BuildConfig.basicAuthUsernameOtp + ":" + BuildConfig.basicAuthPassOtp;
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }
    public static String getAuth() {
        String credentials = BuildConfig.basicAuthUsername + ":" + BuildConfig.basicAuthPass;
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }


    public static String getStatusCodeMessage(byte[] data) {
        String result = "";
        try {
            String s = new String(data);
            JSONObject object = new JSONObject(s);
            result = object.optString("Message");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
//        return new String(data);
    }

    public static boolean isOTPCacheOver(Context ctx) {
        boolean flag = true;
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(Constant.LOGIN_PREF, ctx.MODE_PRIVATE);
        if (sharedPreferences.getLong("otp_cache_time", 0) != 0) {
            if (Calendar.getInstance().getTime().getTime() < (sharedPreferences.getLong("otp_cache_time", 0))) {
                //reboot cache time is over
                flag = false;
            }
        }
        return flag;
    }

    public static void setOTPCacheTime(Context ctx, int seconds) {
        SharedPreferences sharedPreferences = ctx.getSharedPreferences(Constant.LOGIN_PREF, ctx.MODE_PRIVATE);
        Calendar currentTime = Calendar.getInstance();
        Calendar reboot_time = Calendar.getInstance();

        reboot_time.set(Calendar.HOUR, currentTime.get(Calendar.HOUR));
        reboot_time.set(Calendar.MINUTE, currentTime.get(Calendar.MINUTE));
        reboot_time.set(Calendar.SECOND, currentTime.get(Calendar.SECOND) + seconds);

        long reeboot = reboot_time.getTime().getTime();
        sharedPreferences.edit().putLong("otp_cache_time", reeboot).apply();
    }

    private static StringBuilder hexString;

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            hexString = new StringBuilder();
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
