package com.airtel.buildingconnectivitymmi.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.airtel.buildingconnectivitymmi.R;

public class GenericTextWatcher implements TextWatcher, View.OnTouchListener {
    private final EditText[] editText;
    private View view;
    private static boolean flagIsIgnore = false;
    private static OtpListenerImpl mListener;

    public GenericTextWatcher(View view, EditText editText[]) {
        this.editText = editText;
        this.view = view;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (!flagIsIgnore) {
            String text = editable.toString();
            String otp;
            switch (view.getId()) {
                case R.id.otp_edit_box1:
                    if (text.length() == 1) {
                        editText[1].requestFocus();
                        editText[1].setSelection(editText[1].getText().length());
                    }
                    otp = text + editText[1].getText().toString() + editText[2].getText().toString() + editText[3].getText().toString();
                    checkOtp(otp);
                    break;
                case R.id.otp_edit_box2:
                    if (text.length() == 1) {
                        editText[2].requestFocus();
                        editText[2].setSelection(editText[2].getText().length());
                    } else if (text.length() == 0) {
                        editText[0].requestFocus();
                        editText[0].setSelection(editText[0].getText().length());
                    }
                    otp = editText[0].getText().toString() + text + editText[2].getText().toString() + editText[3].getText().toString();
                    checkOtp(otp);
                    break;
                case R.id.otp_edit_box3:
                    if (text.length() == 1) {
                        editText[3].requestFocus();
                        editText[3].setSelection(editText[3].getText().length());
                    } else if (text.length() == 0) {
                        editText[1].requestFocus();
                        editText[1].setSelection(editText[1].getText().length());
                    }

                    otp = editText[0].getText().toString() + editText[1].getText().toString() + text + editText[3].getText().toString();
                    checkOtp(otp);
                    break;
                case R.id.otp_edit_box4:
                    if (text.length() == 0) {
                        editText[2].requestFocus();
                        editText[2].setSelection(editText[2].getText().length());
                    }
                    otp = editText[0].getText().toString() + editText[1].getText().toString() + editText[2].getText().toString() + text;
                    checkOtp(otp);
                    break;
            }
        }
    }

    private void checkOtp(String otp) {
        if (otp.length() == 4) {
            mListener.onOtpCompleted(otp);
        }else {
            mListener.onOtpNotCompleted();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        flagIsIgnore = false;
        /*switch (view.getId()) {

            case R.id.otp_edit_box1:
                *//*if (text.length() == 1)
                {
                    flagIsIgnore = true;
                    editText[1].requestFocus();
                    editText[1].setSelection(editText[1].getText().length());
                }*//*
                break;
            case R.id.otp_edit_box2:
                if (TextUtils.isEmpty(editText[1].getText())){
                    if (TextUtils.isEmpty(editText[0].getText())){
                        flagIsIgnore = true;
                        editText[0].requestFocus();
                    }else {
                        flagIsIgnore = true;
                        editText[1].requestFocus();
                    }
                }else {
                    flagIsIgnore = true;
                    editText[1].requestFocus();
                }
                break;
            case R.id.otp_edit_box3:
                if (TextUtils.isEmpty(editText[2].getText())){
                    if (TextUtils.isEmpty(editText[1].getText())){
                        if (TextUtils.isEmpty(editText[0].getText())){
                            flagIsIgnore = true;
                            editText[0].requestFocus();
                        }else {
                            flagIsIgnore = true;
                            editText[1].requestFocus();
                        }
                    }else {
                        flagIsIgnore = true;
                        editText[2].requestFocus();
                    }
                }else {
                    flagIsIgnore = true;
                    editText[2].requestFocus();
                }
                break;
            case R.id.otp_edit_box4:
                if (TextUtils.isEmpty(editText[3].getText())){
                    if (TextUtils.isEmpty(editText[2].getText())){
                        if (TextUtils.isEmpty(editText[1].getText())){
                            if (TextUtils.isEmpty(editText[0].getText())){
                                flagIsIgnore = true;
                                editText[0].requestFocus();
                            }else {
                                flagIsIgnore = true;
                                editText[1].requestFocus();
                            }
                        }else {
                            flagIsIgnore = true;
                            editText[2].requestFocus();
                        }
                    }else {

                        flagIsIgnore = true;
                        editText[3].requestFocus();

                    }
                }else {
                    flagIsIgnore = true;
                    editText[3].requestFocus();
                }
                break;
        }*/

//        Toast.makeText(view.getContext(), "beforeTextChanged: "+view.getId()+"::"+arg0+":"+arg1+":"+arg2+":"+arg3, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }


    public static void bindListener(OtpListenerImpl listener) {
        mListener = listener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        editText[0].setText("");
        editText[1].setText("");
        editText[2].setText("");
        editText[3].setText("");
        editText[0].setSelected(true);
        Toast.makeText(v.getContext(), "onClick"+v.getId(), Toast.LENGTH_SHORT).show();
        return false;
    }
}