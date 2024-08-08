package com.airtel.buildingconnectivitymmi.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.airtel.buildingconnectivitymmi.R;
import com.google.android.material.snackbar.Snackbar;

import static android.view.View.GONE;

public class UIUtils {

    /**
     * show Progress Dialog
     *
     * @return ProgressDialog
     */
    public static Dialog showProgressDialog(Context activityContext) {
        Dialog dialog = null;

        try {
            dialog = new Dialog(activityContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }
            dialog.setContentView(R.layout.progress_dialog_layout);
            dialog.setCancelable(false);
            dialog.show();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return dialog;
    }

    /**
     * dismiss progress dialog
     *
     * @author Pinelabs
     */
    public static void dismissDialog(Dialog dialog) {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        } catch (IllegalArgumentException ignored) {

        }
    }


    public static Snackbar makeCustomSnackbar(Context context, String msg, Boolean flagActionShow) {
        Snackbar snackbar = null;
        View parentLayout = ((Activity)context).findViewById(android.R.id.content);
        if (parentLayout!=null){

            if (flagActionShow){
                snackbar = Snackbar.make(parentLayout, msg, Snackbar.LENGTH_INDEFINITE)
                        .setActionTextColor(context.getResources().getColor(R.color.colorPrimary));
                snackbar.setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setVisibility(GONE);
                    }
                });
            }else {
                snackbar = Snackbar.make(parentLayout, msg, Snackbar.LENGTH_SHORT)
                        .setActionTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
//            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout)snackbar.getView();
//            layout.setMinimumHeight(300);
            View snackbarView = snackbar.getView();
            TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            textView.setMaxLines(3);
            TextView snackbarActionTextView = snackbarView.findViewById(com.google.android.material.R.id.snackbar_action);
            Typeface bold = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Bold.ttf");
            snackbarActionTextView.setTypeface(bold);

        }
        return snackbar;
    }
}
