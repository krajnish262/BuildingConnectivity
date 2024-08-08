package com.airtel.buildingconnectivitymmi.util;

import android.content.Context;
import android.net.ConnectivityManager;

public class NetworkUtils {

    public NetworkUtils() {

    }

    public static boolean isNetworkConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );

        android.net.NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }
}
