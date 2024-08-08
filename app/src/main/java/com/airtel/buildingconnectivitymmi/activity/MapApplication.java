package com.airtel.buildingconnectivitymmi.activity;

import android.app.Application;

import com.airtel.buildingconnectivitymmi.util.FontsOverride;
import com.mapbox.mapboxsdk.MapmyIndia;
import com.mmi.services.account.MapmyIndiaAccountManager;

/**
 * Created by CEINFO on 29-06-2018.
 */

public class MapApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/Roboto.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Roboto.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/Roboto.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/Roboto.ttf");

        MapmyIndiaAccountManager.getInstance().setRestAPIKey(getRestAPIKey());
        MapmyIndiaAccountManager.getInstance().setMapSDKKey(getMapSDKKey());
        MapmyIndiaAccountManager.getInstance().setAtlasClientId(getAtlasClientId());
        MapmyIndiaAccountManager.getInstance().setAtlasClientSecret(getAtlasClientSecret());
        MapmyIndiaAccountManager.getInstance().setAtlasGrantType(getAtlasGrantType());
        MapmyIndia.getInstance(this);
    }


    public String getAtlasClientId() {
        return "jI5papL64WM2SpWpJ-KM9XV05eAENVSD_gbh17QcykkV4ruHUSftaZM9aOqv1NuACsmiu4cxYm5FDl8XY2oetQ==";
    }

    public String getAtlasClientSecret() {
        return "ebEc8GH231eF5FGdFASMTHWT_PRAFIAuwvu03UYXsGXaUzw7wjIs5LYQHN6oXZCjBS4LAhvb0Hp0xleeCEac5nZXwtts-GH2";
    }


    public String getAtlasGrantType() {
        return "client_credentials";
    }

    public String getMapSDKKey() {
        return "c96w43js2zv6nqnfoge6tg3dwiyfn17b";
    }

    public String getRestAPIKey() {
        return "a67eljkbpzg4sojigyhn61qw8ggajt3s";
    }

}
