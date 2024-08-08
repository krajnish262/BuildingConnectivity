package com.airtel.buildingconnectivitymmi.util;

import android.util.Log;

public class OtpListenerImpl implements OtpCompleteListener, OtpCompleteNotListener {
    private final String TAG = OtpListenerImpl.class.getSimpleName();
    @Override
    public void onOtpCompleted(String Otp) {
        Log.e(TAG, "onOtpCompleted: "+Otp);
    }

    @Override
    public void onOtpNotCompleted() {
        Log.e(TAG, "onOtpNotCompleted: ");
    }
}
