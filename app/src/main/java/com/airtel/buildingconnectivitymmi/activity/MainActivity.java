package com.airtel.buildingconnectivitymmi.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airtel.buildingconnectivitymmi.BuildConfig;
import com.airtel.buildingconnectivitymmi.R;
import com.airtel.buildingconnectivitymmi.adapter.AutoSuggestAdapter;
import com.airtel.buildingconnectivitymmi.model.CustomMarker;
import com.airtel.buildingconnectivitymmi.model.NEDetails;
import com.airtel.buildingconnectivitymmi.util.AppConstants;
import com.airtel.buildingconnectivitymmi.util.Constant;
import com.airtel.buildingconnectivitymmi.util.GeoLocation;
import com.airtel.buildingconnectivitymmi.util.GpsUtils;
import com.airtel.buildingconnectivitymmi.util.MethodsUtil;
import com.airtel.buildingconnectivitymmi.util.NetworkUtils;
import com.airtel.buildingconnectivitymmi.util.TransparentProgressDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.mapboxsdk.maps.UiSettings;
import com.mapmyindia.sdk.plugins.places.autocomplete.model.PlaceOptions;
import com.mapmyindia.sdk.plugins.places.autocomplete.ui.PlaceAutocompleteFragment;
import com.mapmyindia.sdk.plugins.places.autocomplete.ui.PlaceSelectionListener;
import com.mmi.services.api.autosuggest.MapmyIndiaAutoSuggest;
import com.mmi.services.api.autosuggest.model.AutoSuggestAtlasResponse;
import com.mmi.services.api.autosuggest.model.ELocation;
import com.mmi.services.api.geocoding.GeoCode;
import com.mmi.services.api.geocoding.GeoCodeResponse;
import com.mmi.services.api.geocoding.MapmyIndiaGeoCoding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.UiSettings;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.google.android.libraries.places.api.model.Place;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, TextWatcher, PopupMenu.OnMenuItemClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MapboxMap mMap;
    CardView card_view_check;
    CardView card_view_current_location;
    String apiKey_old = "AIzaSyDIIcIhkh5TtUSIB2h92rVfXcn4u14HCBo";
    String apiKey = "AIzaSyAyp2O8KiiD-wsnmiy9f1qsgHLuqfDZOSs";
    private String connectivityV2 = "v2/connectivity";
    private String connectivityV4 = "v4/connectivity";
    private static StringBuilder hexString;
    private static final boolean isToastLocationEnabled = false;
    ArrayList<NEDetails> arrayListNE;
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
    private EditText autoSuggestTextGoogle;

    String lat = null;
    String lng = null;
    private LinearLayout linearLayoutTech, linearLayoutLTS, linearLayoutLTSHeading, linearLayoutLTS1, linearLayoutLTS2, linearLayoutLTS3;
    TextView tvTechType, tvTechnology, tvLastNe1, tvLastNe2, tvLastNe3, tvTotalCapacity1, tvTotalCapacity2, tvTotalCapacity3, tvAvailablePort1, tvAvailablePort2, tvAvailablePort3, tvUtilizedPort1, tvUtilizedPort2, tvUtilizedPort3;
    CardView cardInfoMarker;
    ProgressBar pbLoading;

    private FusedLocationProviderClient mFusedLocationClient;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isContinue = false;
    private boolean isGPS = false;

    private boolean doubleBackToExitPressedOnce;

    //mmi

    private EditText autoSuggestText;
    private RecyclerView recyclerView;
    private CardView card_view_recycler;
    private LinearLayoutManager mLayoutManager;
    private TransparentProgressDialog transparentProgressDialog;
    private Handler handler;
    private boolean isTextWatcherDisabled = false;

    private ImageView imgMarker;
    private ImageView imgClear;

    public boolean isTextWatcherDisabled() {
        return isTextWatcherDisabled;
    }

    public void setTextWatcherDisabled(boolean textWatcherDisabled) {
        isTextWatcherDisabled = textWatcherDisabled;
    }

    String city, state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectivity);
        TextView txtToolbarTitle = (TextView) findViewById(R.id.txtToolbarTitle);
        txtToolbarTitle.setText(getString(R.string.app_name));
        linearLayoutTech = findViewById(R.id.linearLayoutTech);
        linearLayoutLTS = findViewById(R.id.linearLayoutLTS);
        linearLayoutLTSHeading = findViewById(R.id.linearLayoutLTSHeading);
        linearLayoutLTS1 = findViewById(R.id.linearLayoutLTS1);
        linearLayoutLTS2 = findViewById(R.id.linearLayoutLTS2);
        linearLayoutLTS3 = findViewById(R.id.linearLayoutLTS3);
        linearLayoutTech.setVisibility(View.GONE);
        linearLayoutLTS.setVisibility(View.GONE);
        tvTechnology = findViewById(R.id.tvTechnology);
        tvLastNe1 = findViewById(R.id.tvLastNe1);
        tvLastNe2 = findViewById(R.id.tvLastNe2);
        tvLastNe3 = findViewById(R.id.tvLastNe3);
        tvTotalCapacity1 = findViewById(R.id.tvTotalCapacity1);
        tvTotalCapacity2 = findViewById(R.id.tvTotalCapacity2);
        tvTotalCapacity3 = findViewById(R.id.tvTotalCapacity3);
        tvAvailablePort1 = findViewById(R.id.tvAvailablePort1);
        tvAvailablePort2 = findViewById(R.id.tvAvailablePort2);
        tvAvailablePort3 = findViewById(R.id.tvAvailablePort3);
        tvUtilizedPort1 = findViewById(R.id.tvUtilizedPort1);
        tvUtilizedPort2 = findViewById(R.id.tvUtilizedPort2);
        tvUtilizedPort3 = findViewById(R.id.tvUtilizedPort3);
        cardInfoMarker = findViewById(R.id.cardInfoMarker);
        cardInfoMarker.setVisibility(View.GONE);

        tvTechType = findViewById(R.id.tvTechType);
        pbLoading = findViewById(R.id.pbLoading);
        pbLoading.setVisibility(View.GONE);


        if (savedInstanceState != null) {
            String tmp = savedInstanceState.getString("tvTechType");
            if (tmp != null) {
                tvTechType.setText(tmp);
            }
        }


        //google places api init
        googlePlacesMethod();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }


        initReferences();
        initListeners();
//        initPlacesApi();

        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(autoSuggestText.getText())) {

                    resetMethod();
                }
            }
        });

        card_view_current_location = findViewById(R.id.card_view_current_location);
        card_view_check = findViewById(R.id.card_view_check);
        card_view_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lat != null && lng != null) {
                    tvTechType.setText("");
                    if (NetworkUtils.isNetworkConnected(MainActivity.this)) {
                        pbLoading.setVisibility(View.VISIBLE);
                        checkRequest(lat, lng);
                    } else {
                        Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
//                Toast.makeText(MainActivity.this, "Not Implemented", Toast.LENGTH_SHORT).show();
            }
        });


        //for location
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds
        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        if (!isContinue) {
                            LatLng latlng = new LatLng(wayLatitude, wayLongitude);
                            callMapFunction(latlng);
                        }

                        /*if (!isContinue) {
                            txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                        } else {
                            stringBuilder.append(wayLatitude);
                            stringBuilder.append("-");
                            stringBuilder.append(wayLongitude);
                            stringBuilder.append("\n\n");
                            txtContinueLocation.setText(stringBuilder.toString());
                        }*/
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };

        card_view_current_location.setOnClickListener(v -> {

            if (!isGPS) {
                Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show();
                return;
            }
            isContinue = false;
            getLocation();
        });


    }

    private void resetMethod() {
        autoSuggestText.setText("");
        autoSuggestTextGoogle.setText("");
        tvTechType.setText("");
        lat = null;
        lng = null;
        if (mMap!=null)
            mMap.clear();
        cardInfoMarker.setVisibility(View.GONE);

        linearLayoutTech.setVisibility(View.GONE);
        linearLayoutLTS.setVisibility(View.GONE);
        tvTechnology.setText("");
        tvLastNe1.setText("");
        tvLastNe2.setText("");
        tvLastNe3.setText("");
        tvTotalCapacity1.setText("");
        tvTotalCapacity2.setText("");
        tvTotalCapacity3.setText("");
        tvAvailablePort1.setText("");
        tvAvailablePort2.setText("");
        tvAvailablePort3.setText("");
        tvUtilizedPort1.setText("");
        tvUtilizedPort2.setText("");
        tvUtilizedPort3.setText("");

    }


    private void googlePlacesMethod() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*com.google.android.gms.maps.SupportMapFragment mapFragment = (com.google.android.gms.maps.SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }*/

        // Initialize the SDK
        Places.initialize(getApplicationContext(), apiKey);
        // Create a new Places client instance
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(fields);
            autocompleteFragment.setCountry("IN");
            final View search_view = autocompleteFragment.getView();
            if (search_view != null) {
                search_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((TextView) v.findViewById(R.id.places_autocomplete_search_input)).setText("");
                        lat = null;
                        lng = null;
                    }
                });
                ((EditText) search_view.findViewById(R.id.places_autocomplete_search_input)).setTextSize(12.0f);
                ((View) search_view.findViewById(R.id.places_autocomplete_search_button)).setVisibility(View.GONE);
                autoSuggestTextGoogle = ((EditText) search_view.findViewById(R.id.places_autocomplete_search_input));
                /*((View) search_view.findViewById(R.id.places_autocomplete_clear_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        v.setVisibility(View.GONE);
                        ((EditText) search_view.findViewById(R.id.places_autocomplete_search_input)).setText("");
//                        ((TextView) v.findViewById(R.id.places_autocomplete_search_input)).setText("");
                        tvTechType.setText("");
//                        ((EditText) autocompleteFragment.getView().findViewById(R.id.places_autocomplete_edit_text)).setText("");
                    }
                });*/
            }

            // Set up a PlaceSelectionListener to handle the response.
            autocompleteFragment.setOnPlaceSelectedListener(new com.google.android.libraries.places.widget.listener.PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(final Place place) {
                    // TODO: Get info about the selected place.
//                    Log.e(TAG, "Place: " + place.getName() + ", " + place.getId() + ", " + place.getLatLng());
                    tvTechType.setText("");
                    if (search_view != null) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                String tmp = place.getName() + ", " + place.getAddress();
//                                autoSuggestTextGoogle = ((EditText) search_view.findViewById(R.id.places_autocomplete_search_input));
                                if (null!=autoSuggestTextGoogle)
                                    autoSuggestTextGoogle.setText(tmp);
                            }
                        }, 300);
                    }
                    com.google.android.gms.maps.model.LatLng placeLatLng = place.getLatLng();
                    com.google.android.gms.maps.model.MarkerOptions markerOptions = null;
                    if (placeLatLng != null) {
                        //mMap.clear();

                        addMarker(placeLatLng.latitude, placeLatLng.longitude);

                        /*DecimalFormat df = new DecimalFormat("#.######");
                        lat = df.format(place.latitude);
                        lng = df.format(place.longitude);*/


//                        markerOptions = new com.google.android.gms.maps.model.MarkerOptions().position(placeLatLng).title(place.getName());
//                        gMap.addMarker(markerOptions);
//                        gMap.animateCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(placeLatLng, 18));
                    } else {
                        GeoLocation.getAddress(place.getName(), getApplicationContext(), new GeoHandler());
                    }
                    DecimalFormat df = new DecimalFormat("#.######");
                    if (placeLatLng != null) {
                        lat = df.format(placeLatLng.latitude);
                        lng = df.format(placeLatLng.longitude);
                    }
                    Toast.makeText(MainActivity.this, "Location : " + lat + "/" + lng, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.
                    Log.i(TAG, "An error occurred: " + status);
                }
            });
        }
    }


    public void showMyMenu(View view) {
        resetMethod();
    }


    public void showPopup(View view) {

        PopupMenu popup = new PopupMenu(MainActivity.this, view);
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.main_menu, popup.getMenu());

        //item text color
        MenuItem signout = popup.getMenu().findItem(R.id.logout);
        SpannableString sSignout = new SpannableString(signout.getTitle().toString());
        sSignout.setSpan(new ForegroundColorSpan(Color.WHITE), 0, sSignout.length(), 0);
        signout.setTitle(sSignout);

        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.logout) {

            SharedPreferences userPref = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
            userPref.edit().clear().apply();

            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
            MainActivity.this.overridePendingTransition(R.anim.zoom, R.anim.zoomout);

            return true;
        } else
            return false;
    }

    private void initPlacesApi() {

        PlaceOptions placeOptions = PlaceOptions.builder()
//                .location(Point.fromLngLat(midLatLng.getLongitude(), midLatLng.getLatitude()))
                .userAddedLocationEnable(false)
                .build(PlaceOptions.MODE_CARDS);
        PlaceAutocompleteFragment placeAutocompleteFragment = PlaceAutocompleteFragment.newInstance(placeOptions);
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(ELocation eLocation) {

                if (mMap != null) {
//                    mMap.clear();
                    LatLng latLng = new LatLng(Double.parseDouble(eLocation.latitude), Double.parseDouble(eLocation.longitude));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
//                    mMap.addMarker(new MarkerOptions().position(latLng).title(eLocation.placeName).snippet(eLocation.placeAddress));
                }
            }

            @Override
            public void onCancel() {

            }
        });


        getSupportFragmentManager().beginTransaction().add(R.id.map, placeAutocompleteFragment, PlaceAutocompleteFragment.class.getSimpleName())
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            SharedPreferences userPref = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
            userPref.edit().clear().apply();

            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
            MainActivity.this.overridePendingTransition(R.anim.zoom, R.anim.zoomout);

            return true;
        }

        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (tvTechType != null && !tvTechType.getText().toString().equalsIgnoreCase("")) {
            outState.putString("tvTechType", tvTechType.getText().toString());
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(MapboxMap map) {
        mMap = map;

        UiSettings settings = mMap.getUiSettings();
//        settings.setScrollGesturesEnabled(false);
        settings.setLogoEnabled(false);
        settings.setRotateGesturesEnabled(false);

        // Add a marker in India and move the camera
        LatLng india = new LatLng(28.611825, 77.215479);
        MarkerOptions markerOptions = new MarkerOptions().position(india).title("Marker in India");
//        mMap.addMarker(markerOptions);
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(india));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(india, 2));
        imgMarker.setVisibility(View.VISIBLE);

        mMap.addOnCameraIdleListener(new MapboxMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                //get latlng at the center by calling
                LatLng midLatLng = mMap.getCameraPosition().target;
                if (NetworkUtils.isNetworkConnected(MainActivity.this)) {
                    DecimalFormat df = new DecimalFormat("#.######");
                    if (midLatLng != null) {
                        lat = df.format(midLatLng.getLatitude());
                        lng = df.format(midLatLng.getLongitude());

                        if (isToastLocationEnabled) {
                            Toast.makeText(MainActivity.this, "Location : " + lat + "/" + lng, Toast.LENGTH_SHORT).show();
                        }
                        reverseGeocoderNew(midLatLng.getLatitude(), midLatLng.getLongitude());
                    }

                } else {
                    Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    public void onMapError(int i, String s) {

    }

    private class GeoHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            String address;
            if (msg.what == 1) {
                Bundle bundle = msg.getData();
                address = bundle.getString("address");
                lat = bundle.getString("lat");
                lng = bundle.getString("lng");
                //bt_submit.setEnabled(true);
            } else {
                address = null;
            }
            //tv_address.setText(address);

            Bitmap smallMarker = getSmallMarker(100,100, R.drawable.map_logo);

            if (mMap!=null)
                mMap.clear();
            if (lat != null && lng != null) {
                LatLng placeLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                MarkerOptions markerOptions = new MarkerOptions().position(placeLatLng).title(address);
                // Changing marker icon
//                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                //markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.common_google_signin_btn_icon_dark_normal));

//                mMap.addMarker(markerOptions);
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(placeLatLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 14));
            }

        }
    }


    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
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

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void checkRequest(String lat, String lng) {
        if (mMap!=null)
            mMap.clear();
        cardInfoMarker.setVisibility(View.GONE);
        final String searchKey = md5("searchString" + Constant.STATIC_KEY);

        SharedPreferences loginPref = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
        String eng_name = loginPref.getString("eng_name","");
        String circle = loginPref.getString("circle","");
        String tl_name = loginPref.getString("tl_name","");
        String tl_mobile = loginPref.getString("tl_mobile","");


        JSONObject mainJObject = new JSONObject();

        try {
//            mainJObject.put("address", "");
            mainJObject.put("latitude", lat);
            mainJObject.put("longitude", lng);
            mainJObject.put("city", city);
            mainJObject.put("state", state);
            mainJObject.put("eng_name", eng_name);
            mainJObject.put("circle", circle);
            mainJObject.put("tl_name", tl_name);
            mainJObject.put("tl_mobile", tl_mobile);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            RequestQueue queue = Volley.newRequestQueue(this);
//            String url = getString(R.string.base_url2) + connectivityV2;
            String url = BuildConfig.baseUrl + connectivityV4;
            JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST, url, mainJObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pbLoading.setVisibility(View.GONE);
                            if (response != null) {
                                String statusCode = response.optString("StatusCode");
                                String msg = response.optString("Message");
                                if (statusCode.equals("200")) {
                                    /*JSONObject jsonObject = response.optJSONObject("data");
                                    if (jsonObject != null) {
                                        String tech_type = jsonObject.optString("tech_type");
                                    }*/


                                    /*JSONArray jsonArray = response.optJSONArray("data");
                                    JSONObject jsonObject = null;
                                    if (jsonArray != null) {

                                        StringBuilder stringBuilderTechType = new StringBuilder();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            jsonObject = jsonArray.optJSONObject(i);
                                            String tech_type = jsonObject.optString("tech_type");
                                            if (i > 0) {
                                                stringBuilderTechType.append(" + ");
                                            }
                                            stringBuilderTechType.append(tech_type);
                                        }
                                        tvTechType.setText(stringBuilderTechType);

                                    }*/

                                    JSONObject jsonObject = response.optJSONObject("data");
                                    JSONArray techTypeArr = null;
                                    JSONArray neDetailsArr = null;
                                    if (jsonObject != null) {

                                        String logout = jsonObject.optString("logout");
                                        if (logout.equalsIgnoreCase("yes")) {
                                            SharedPreferences userPref = getSharedPreferences(Constant.LOGIN_PREF, MODE_PRIVATE);
                                            userPref.edit().clear().apply();
                                            Intent intent = new Intent(MainActivity.this, Login.class);
                                            startActivity(intent);
                                            finish();
                                            MainActivity.this.overridePendingTransition(R.anim.zoom, R.anim.zoomout);
                                        }


                                        techTypeArr = jsonObject.optJSONArray("tech_type");
                                        StringBuilder stringBuilderTechType = new StringBuilder();
                                        if (techTypeArr != null) {
                                            for (int i = 0; i < techTypeArr.length(); i++) {
                                                try {
                                                    String tech_type = (String) techTypeArr.get(i);
                                                    if (i > 0) {
                                                        stringBuilderTechType.append(" + ");
                                                    }
                                                    stringBuilderTechType.append(tech_type);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        if (stringBuilderTechType.length() > 0) {
//                                            linearLayoutTech.setVisibility(View.VISIBLE);  //26Mar2021
                                            tvTechnology.setText(stringBuilderTechType);
                                        } else {
                                            linearLayoutTech.setVisibility(View.GONE);
                                        }
//                                        tvTechType.setText(stringBuilderTechType);

                                        int countOfNE = jsonObject.optInt("Count_of_NE");
                                        if (countOfNE > 0) {

//                                            linearLayoutLTS.setVisibility(View.VISIBLE);  //26Mar2021
                                            neDetailsArr = jsonObject.optJSONArray("NE_details");

                                            if (neDetailsArr != null) {


                                                arrayListNE = new ArrayList<>(); //testing

                                                for (int i = 0; i < neDetailsArr.length(); i++) {
                                                    try {
                                                        JSONObject neDetails = (JSONObject) neDetailsArr.get(i);

                                                        //testing
                                                        NEDetails details = new NEDetails();
                                                        details.setNeName(neDetails.optString("NE_Name"));
                                                        details.setNeLatitude(neDetails.optDouble("latitude"));
                                                        details.setNeLongitude(neDetails.optDouble("longitude"));
                                                        details.setNeTotalPorts(neDetails.optString("Total_ports"));
                                                        details.setNeAvailablePorts(neDetails.optString("Available_ports"));
                                                        details.setNeUtilizedPort(neDetails.optString("Utilized_port"));
                                                        details.setNeTechnology(neDetails.optString("technology"));
                                                        details.setNeType(neDetails.optString("neType"));
                                                        details.setNeDistanceFromHub(neDetails.optString("distanceFromHub"));
                                                        arrayListNE.add(details);



                                                        if (i == 2 && countOfNE >= 3) {

                                                            String neName = neDetails.getString("NE_Name");
                                                            String neTotalPorts = neDetails.getString("Total_ports");
                                                            String neAvailablePorts = neDetails.getString("Available_ports");
                                                            String neUtilizedPorts = neDetails.optString("Utilized_port");

                                                            tvLastNe3.setText(neName);
                                                            tvTotalCapacity3.setText(neTotalPorts);
                                                            tvAvailablePort3.setText(neAvailablePorts);
                                                            tvUtilizedPort3.setText(neUtilizedPorts);


                                                            linearLayoutLTSHeading.setVisibility(View.VISIBLE);
                                                            linearLayoutLTS1.setVisibility(View.VISIBLE);
                                                            linearLayoutLTS2.setVisibility(View.VISIBLE);
                                                            linearLayoutLTS3.setVisibility(View.VISIBLE);

                                                        } else if (i == 1 && countOfNE >= 2) {

                                                            String neName = neDetails.getString("NE_Name");
                                                            String neTotalPorts = neDetails.getString("Total_ports");
                                                            String neAvailablePorts = neDetails.getString("Available_ports");
                                                            String neUtilizedPorts = neDetails.optString("Utilized_port");

                                                            tvLastNe2.setText(neName);
                                                            tvTotalCapacity2.setText(neTotalPorts);
                                                            tvAvailablePort2.setText(neAvailablePorts);
                                                            tvUtilizedPort2.setText(neUtilizedPorts);

                                                            linearLayoutLTSHeading.setVisibility(View.VISIBLE);
                                                            linearLayoutLTS1.setVisibility(View.VISIBLE);
                                                            linearLayoutLTS2.setVisibility(View.VISIBLE);
                                                            linearLayoutLTS3.setVisibility(View.GONE);

                                                        } else if (i == 0) {
                                                            String neName = neDetails.getString("NE_Name");
                                                            String neTotalPorts = neDetails.getString("Total_ports");
                                                            String neAvailablePorts = neDetails.getString("Available_ports");
                                                            String neUtilizedPorts = neDetails.optString("Utilized_port");

                                                            tvLastNe1.setText(neName);
                                                            tvTotalCapacity1.setText(neTotalPorts);
                                                            tvAvailablePort1.setText(neAvailablePorts);
                                                            tvUtilizedPort1.setText(neUtilizedPorts);

                                                            linearLayoutLTSHeading.setVisibility(View.VISIBLE);
                                                            linearLayoutLTS1.setVisibility(View.VISIBLE);
                                                            linearLayoutLTS2.setVisibility(View.GONE);
                                                            linearLayoutLTS3.setVisibility(View.GONE);
                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                }


                                                addMarkerForNE(arrayListNE);  //testing
//                                                addTestMarkers(); //testing
                                            }
                                        } else {
                                            tvTechType.setText("Not-Connected");
                                            linearLayoutLTS.setVisibility(View.GONE);
                                        }

                                    }



                                    /*JSONArray jsonArray = response.optJSONArray("data");
                                    JSONObject jsonObject = null;
                                    if (jsonArray != null) {

                                        StringBuilder stringBuilderTechType = new StringBuilder();
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            jsonObject = jsonArray.optJSONObject(i);
                                            String tech_type = jsonObject.optString("tech_type");
                                            if (i > 0) {
                                                stringBuilderTechType.append(" + ");
                                            }
                                            stringBuilderTechType.append(tech_type);
                                        }
                                        tvTechType.setText(stringBuilderTechType);
                                    }*/
                                } else {
                                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }
                            }
                            Log.d("rsp", String.valueOf(response));
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pbLoading.setVisibility(View.GONE);
                            Log.d("ERROR", "error => " + error.toString());
                            Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("key", searchKey);
                    params.put("key", "f7b670519ddd4d610de609d20a74a06b");
                    String auth = MethodsUtil.getAuth();
                    params.put("Authorization", auth);

                    return params;
                }

            };
            //Handling timeout, increasing the wait time
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
//                    progressBar.setVisibility(View.GONE);
                    throw error;
                }
            });
            queue.add(getRequest);

        } catch (Exception e) { // for caught any exception during the excecution of the service

            Toast.makeText(MainActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private Bitmap getSmallMarker(int height, int width, int drawable) {
//        int height = 100;
//        int width = 100;
//        Drawable d = getResources().getDrawable(R.drawable.airtelsplash);
//        Bitmap b = ((BitmapDrawable)d).getBitmap();
        Bitmap b = BitmapFactory.decodeResource(getResources(), drawable);
        return Bitmap.createScaledBitmap(b, width, height, false);
    }

    //for current location

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, location -> {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                        LatLng latlng = new LatLng(wayLatitude, wayLongitude);
                        callMapFunction(latlng);
                        //txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                    } else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                });
            }
        }
    }

    private void callMapFunction(LatLng placeLatLng) {
//        placeLatLng = new LatLng(28.4414197474372, 77.004376109904);  //testing
//        placeLatLng = new LatLng(28.611825, 77.215479);  //testing

        if (placeLatLng != null) {
//            mMap.clear();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(placeLatLng, 14));
        } else {
            GeoLocation.getAddress("My Current Location", getApplicationContext(), new GeoHandler());
        }
        DecimalFormat df = new DecimalFormat("#.######");
        if (placeLatLng != null) {
            lat = df.format(placeLatLng.getLatitude());
            lng = df.format(placeLatLng.getLongitude());
        }
        Toast.makeText(MainActivity.this, "Location : " + lat + "/" + lng, Toast.LENGTH_SHORT).show();

        if (lat != null && lng != null) {
            tvTechType.setText("");
            if (NetworkUtils.isNetworkConnected(MainActivity.this)) {
                pbLoading.setVisibility(View.VISIBLE);
                checkRequest(lat, lng);
            } else {
                Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void addMyMarkers() {

        if (mMap!=null)
            mMap.clear();
        LatLng geoPoint = new LatLng();
        MarkerOptions markerOptions = new MarkerOptions();


        LatLng geoPoint1 = new LatLng();
        geoPoint1.setLatitude(Double.parseDouble("24.506413284654165"));
        geoPoint1.setLongitude(Double.parseDouble("78.57778367430478"));

        LatLng geoPoint2 = new LatLng();
        geoPoint2.setLatitude(Double.parseDouble("28.098145049036038"));
        geoPoint2.setLongitude(Double.parseDouble("80.12435476482369"));

        LatLng geoPoint3 = new LatLng();
        geoPoint3.setLatitude(Double.parseDouble("32.79373425082885"));
        geoPoint3.setLongitude(Double.parseDouble("76.87240167217396"));

        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(geoPoint1);
        markerOptions1.title("Marker in Panchkula, India");
        markerOptions1.setSnippet("Snippet");
        mMap.addMarker(markerOptions1);

        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(geoPoint2).title("Marker in Leh, India");
        mMap.addMarker(markerOptions2);

//        markerOptions.setPosition(geoPoint3).title("test3");
        MarkerOptions markerOptions3 = new MarkerOptions();
        markerOptions3.position(geoPoint3).title("Marker in New Delhi, India");
        mMap.addMarker(markerOptions3);
//        mMap.animateCamera(com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newLatLngZoom(placeLatLng, 14));


        /*List<Marker> markers = mMap.getMarkers();
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);*/


//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(geoPoint1).title("Marker in India");
//        markerOptions.position(geoPoint2).title("Marker in India");
//        markerOptions.position(geoPoint3).title("Marker in India");
//        mMap.addMarker(markerOptions);


//        MapmyIndiaMapView mapFragment = (MapmyIndiaMapView) findFragmentById(R.id.map);
//        MarkerOptions markerOptions = new MarkerOptions().title("TEST");
//        View mMapView = mapFragment.getView();
//        Marker marker= new Marker(geoPoint1,"","","");
//        marker.setPosition(geoPoint1);
//        marker.setPosition(geoPoint2);
//        marker.setPosition(geoPoint3);

        /*marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (mMapView != null) {
                mMapView.getOverlay().add(marker);
            }
        }
        mMapView.invalidate();*/

//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(india, 2));
//        imgMarker.setVisibility(View.VISIBLE);
    }
    private void addTestMarkers() {

        if (mMap!=null)
            mMap.clear();
        ArrayList<NEDetails> arrayList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray("[{\"NE_Name\":\"2SU03010401\",\"latitude\":\"28.49814\",\"longitude\":\"77.15939\",\"Total_ports\":7,\"Available_ports\":\"0\",\"Utilized_port\":\"7\"},{\"NE_Name\":\"2SU03010501\",\"latitude\":\"28.49729\",\"longitude\":\"77.16003\",\"Total_ports\":8,\"Available_ports\":\"3\",\"Utilized_port\":\"5\"},{\"NE_Name\":\"2SU03010601\",\"latitude\":\"28.49809\",\"longitude\":\"77.16004\",\"Total_ports\":8,\"Available_ports\":\"0\",\"Utilized_port\":\"8\"},{\"NE_Name\":\"2SU03010701\",\"latitude\":\"28.49735\",\"longitude\":\"77.16035\",\"Total_ports\":8,\"Available_ports\":\"5\",\"Utilized_port\":\"3\"},{\"NE_Name\":\"NMP000811\",\"latitude\":\"28.4981\",\"longitude\":\"77.1586\",\"Total_ports\":2,\"Available_ports\":\"2\",\"Utilized_port\":\"0\"}]");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject neDetails = (JSONObject) jsonArray.get(i);
                NEDetails details = new NEDetails();
                details.setNeName(neDetails.optString("NE_Name"));
                details.setNeLatitude(neDetails.optDouble("latitude"));
                details.setNeLongitude(neDetails.optDouble("longitude"));
                details.setNeTotalPorts(neDetails.optString("Total_ports"));
                details.setNeAvailablePorts(neDetails.optString("Available_ports"));
                details.setNeUtilizedPort(neDetails.optString("Utilized_port"));

                arrayList.add(details);
            }

            addMarkerForNE(arrayList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private void addPolyLines(List<LatLng> latLngList) {
        /*mMap.addPolygon(new PolygonOptions()
                .addAll(latLngList)
                .fillColor(ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null)));*/

        mMap.addPolyline(new PolylineOptions()
                .addAll(latLngList)
                .color(ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null))
                .width(2));
    }



    private void addMarkerForNE(ArrayList<NEDetails> arrNeDetails) {
        if (mMap!=null)
            mMap.clear();
        if (arrNeDetails.size()>0){
            cardInfoMarker.setVisibility(View.VISIBLE);
        }
        MarkerOptions markerOptions = new MarkerOptions();
//        List<LatLng> latLngList = new ArrayList<>();

        List<CustomMarker> customMarkerList = new ArrayList<>();
        JSONArray jsonArray = null;
        loop1: for (int i = 0; i < arrNeDetails.size(); i++) {
            NEDetails neDetails = arrNeDetails.get(i);
            Double lat = neDetails.getNeLatitude();
            Double lng = neDetails.getNeLongitude();

            String resultLatLngNe = lat+lng+"";
            for (int j = 0; j <customMarkerList.size(); j++) {
                CustomMarker customMarker = customMarkerList.get(j);
                Double latThis = customMarker.getLat();
                Double lngThis = customMarker.getLng();
                String resultThis = latThis+lngThis+"";
                if (resultThis.equalsIgnoreCase(resultLatLngNe)){
                    String s = customMarker.getSnippet();
                    /*try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("total_port","Total Ports: "+neDetails.getNeTotalPorts());
                        jsonObject.put("avail_port","Available Ports: " + neDetails.getNeAvailablePorts());
                        jsonObject.put("util_port","Utilized Ports: " + neDetails.getNeUtilizedPort());
                        jsonObject.put("rsu_distance","RSU Distance: " + neDetails.getNeDistanceFromHub());
                        jsonObject.put("type","Type: " + neDetails.getNeType());
                        jsonObject.put("tech","Technology: " + neDetails.getNeTechnology());
                        if (jsonArray!=null)
                            jsonArray.put(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
//                    String desc = "Total Ports: " + neDetails.getNeTotalPorts() + "\n" + "Available Ports: " + neDetails.getNeAvailablePorts() + "\n" + "Utilized Ports: " + neDetails.getNeUtilizedPort() + "\n" + "RSU Distance: " + neDetails.getNeDistanceFromHub() + "\n" + "Type: " + neDetails.getNeType() + "\n" + "Technology: " + neDetails.getNeTechnology();
                    String desc = "<b><big>"+ neDetails.getNeName() + "</big></b><br>"+"<font size=\"-1\">Total Ports: " + neDetails.getNeTotalPorts() + "</font><br>" + "<font size=\"-1\">Available Ports: " + neDetails.getNeAvailablePorts() + "</font><br>" + "<font size=\"-1\">Utilized Ports: " + neDetails.getNeUtilizedPort() + "</font><br>" + "<font size=\"-1\">RSU Distance: " + neDetails.getNeDistanceFromHub() + "</font><br>" + "<font size=\"-1\">Type: " + neDetails.getNeType() + "</font><br>" + "<font size=\"-1\">Technology: " + neDetails.getNeTechnology()+ "</font>";
                    s= s+"<hr>"+desc;
                    customMarker.setSnippet(s);
                    continue loop1;
                }
            }

//            jsonArray = new JSONArray();
            String neTechnology= neDetails.getNeTechnology();
            Icon icon = getCustomIcon(R.drawable.icons8marker48black);
            if (neTechnology!=null && (neTechnology.toLowerCase().contains("vector") || neTechnology.toLowerCase().contains("copper"))){
                icon = getCustomIcon(R.drawable.icons8marker48blue);
            }
            /*try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("total_port","Total Ports: "+neDetails.getNeTotalPorts());
                jsonObject.put("avail_port","Available Ports: " + neDetails.getNeAvailablePorts());
                jsonObject.put("util_port","Utilized Ports: " + neDetails.getNeUtilizedPort());
                jsonObject.put("rsu_distance","RSU Distance: " + neDetails.getNeDistanceFromHub());
                jsonObject.put("type","Type: " + neDetails.getNeType());
                jsonObject.put("tech","Technology: " + neDetails.getNeTechnology());
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/


//            String desc = "Total Ports: " + neDetails.getNeTotalPorts() + "\n" + "Available Ports: " + neDetails.getNeAvailablePorts() + "\n" + "Utilized Ports: " + neDetails.getNeUtilizedPort() + "\n" + "RSU Distance: " + neDetails.getNeDistanceFromHub() + "\n" + "Type: " + neDetails.getNeType() + "\n" + "Technology: " + neDetails.getNeTechnology();
            String desc = "<b><big>"+ neDetails.getNeName() + "</big></b><br>"+"<font size=\"-1\">Total Ports: "
                    + neDetails.getNeTotalPorts() + "</font><br>" + "<font size=\"-1\">Available Ports: "
                    + neDetails.getNeAvailablePorts() + "</font><br>" + "<font size=\"-1\">Utilized Ports: "
                    + neDetails.getNeUtilizedPort() + "</font><br>" + "<font size=\"-1\">RSU Distance: "
                    + neDetails.getNeDistanceFromHub() + "</font><br>" + "<font size=\"-1\">Type: "
                    + neDetails.getNeType() + "</font><br>" + "<font size=\"-1\">Technology: "
                    + neDetails.getNeTechnology()+ "</font>";
            CustomMarker customMarker = new CustomMarker();
            customMarker.setTitle(neDetails.getNeName());
            customMarker.setPosition(new LatLng(lat, lng));
            customMarker.setIcon(icon);
            customMarker.setLat(lat);
            customMarker.setLng(lng);

            /*String desc1="";
            for (int j = 0; j < 5; j++) {

                desc1 = desc1+"\n"+desc;
            }
            customMarker.setSnippet(desc1);*/

//            customMarker.setSnippet(jsonArray.toString());

            customMarker.setSnippet(desc);
            customMarkerList.add(customMarker);

        }

        for (int i = 0; i < customMarkerList.size(); i++) {
            CustomMarker markerC = customMarkerList.get(i);
            markerOptions.position(markerC.getPosition());
            markerOptions.title(markerC.getTitle());
            markerOptions.setSnippet(markerC.getSnippet());
            markerOptions.setIcon(markerC.getIcon());
            mMap.addMarker(markerOptions);
//            Log.e("avn", "markerC.getSnippet(): "+markerC.getSnippet());
        }


        /*for (int i = 0; i < arrNeDetails.size(); i++) {
            NEDetails neDetails = arrNeDetails.get(i);
            Double lat = neDetails.getNeLatitude();
            Double lng = neDetails.getNeLongitude();
            String title = neDetails.getNeName();
            String desc = "Total Ports: " + neDetails.getNeTotalPorts() + "\n" + "Available Ports: " + neDetails.getNeAvailablePorts() + "\n" + "Utilized Ports: " + neDetails.getNeUtilizedPort() + "\n" + "RSU Distance: " + neDetails.getNeDistanceFromHub() + "\n" + "Type: " + neDetails.getNeType() + "\n" + "Technology: " + neDetails.getNeTechnology();

            latLngList.add(new LatLng(lat, lng));

            markerOptions.position(new LatLng(lat, lng));
            markerOptions.title(title);
            markerOptions.setSnippet(desc);
            String neTechnology= neDetails.getNeTechnology();
            Icon icon = getCustomIcon(R.drawable.icons8marker48black);
//            Icon icon = getCustomIconFromBitmap(R.drawable.icons8marker48black);
            if (neTechnology!=null && (neTechnology.toLowerCase().contains("vector") || neTechnology.toLowerCase().contains("copper"))){
                icon = getCustomIcon(R.drawable.icons8marker48blue);
//                icon = getCustomIconFromBitmap(R.drawable.icons8marker48blue);
            }
            markerOptions.setIcon(icon);
            mMap.addMarker(markerOptions);

        }*/

        customInfoWindow();

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            latLngList.sort(Comparator.comparingDouble(LatLng :: getLatitude));
        }
        addPolyLines(latLngList);*/

    }

    private Icon getCustomIcon(int drawable) {
        IconFactory iconFactory = IconFactory.getInstance(this);
        return iconFactory.fromResource(drawable);
    }
    private Icon getCustomIconFromBitmap(int drawable) {
        IconFactory iconFactory = IconFactory.getInstance(this);
        return iconFactory.fromBitmap(getSmallMarker(80,80,drawable));
    }

    private void customInfoWindow() {
        mMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {

                View view = getLayoutInflater().inflate(R.layout.custom_info_window_webview, null);
                WebView webView = view.findViewById(R.id.webView);
                webView.loadData(marker.getSnippet(), "text/html", "utf-8");

//                View view = getLayoutInflater().inflate(R.layout.custom_info_window, null);
//                TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
//                TextView tvDesc = (TextView) view.findViewById(R.id.tvDesc);
//                tvTitle.setText(marker.getTitle());
//                tvDesc.setText(marker.getSnippet());

                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tvDesc.setText(Html.fromHtml(marker.getSnippet(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    tvDesc.setText(Html.fromHtml(marker.getSnippet()));
                }*/

//                tvTitle.setVisibility(View.GONE);
//                spanMethod(marker.getTitle(), marker.getSnippet());

                /*LinearLayout linearLayout = view.findViewById(R.id.llMain);
                View viewC = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                TextView tvTitleC = (TextView) viewC.findViewById(R.id.tvTitle);
                TextView tvDescC = (TextView) viewC.findViewById(R.id.tvDesc);
                tvTitleC.setText(marker.getTitle());
                tvDescC.setText(marker.getSnippet());
                tvTitleC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                    }
                });
                TextView tv = new TextView(MainActivity.this);
                tv.setText("Hello");
                tv.setTextColor(getResources().getColor(R.color.bgColor));
                linearLayout.addView(viewC);
                linearLayout.addView(new TextView(MainActivity.this));*/

                /*RecyclerView recycleView = view.findViewById(R.id.recycleView);
                recycleView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                CustomInfoAdaptor mAdapter = null;
                if (null != arrNeRehabAcitivity) {
                    mAdapter = new CustomInfoAdaptor(this, arrNeRehabAcitivity);
                    recycleview.setAdapter(mAdapter);
                }
                CustomInfoAdaptor.bindListener(new CustomInfoAdaptor.CustomAdapterCheckboxListener() {
                    @Override
                    public void selectedNEActivity(String neActivity) {
                    }
                });*/

                return view;
            }

            private SpannableStringBuilder spanMethod(String tvTitle, String tvDesc) {
                SpannableStringBuilder result = null;
                String[] s = {"Total Ports:", "Available Ports:"};
//                String[] s = {tvTitle.toString()};
                for (String textToBeModified : s) {
                    String strFresh = "";

                    String full = tvDesc;//.getText().toString();
                    if (!full.contains(textToBeModified)){
                        result = new SpannableStringBuilder();
                        return result;
                    }
                    final SpannableStringBuilder sb = new SpannableStringBuilder(full);
                    // Span to set text color to some RGB value textToBeModified
//                final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(158, 158, 158));
//                final ForegroundColorSpan fcs = new ForegroundColorSpan(ResourcesCompat.getColor(getResources(),R.color.mapbox_blue, null));
                    final ForegroundColorSpan fcs = new ForegroundColorSpan(ResourcesCompat.getColor(getResources(),R.color.mapbox_blue, null));
                    // Span to make text bold
                    final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                    // Set the text color for first 4 characters
                    int start = full.indexOf(textToBeModified);
                    int end = start + textToBeModified.length();
                    sb.setSpan(fcs, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    // make them also bold
//                sb.setSpan(bss, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//                    tvDesc.setText(sb);
                    result = sb;
                }
                return result;
            }
        });
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (isContinue) {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    } else {
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(MainActivity.this, location -> {
                            if (location != null) {
                                wayLatitude = location.getLatitude();
                                wayLongitude = location.getLongitude();

                                LatLng latlng = new LatLng(wayLatitude, wayLongitude);
                                callMapFunction(latlng);
                                //txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                            } else {
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
//            super.onBackPressed();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void initListeners() {
        autoSuggestText.addTextChangedListener(this);
        autoSuggestText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (v.getId() == R.id.auto_suggest && hasFocus) {
                    setTextWatcherDisabled(false);
                }
            }
        });
    }

    private void initReferences() {
        imgMarker = findViewById(R.id.imgMarker);
        imgClear = findViewById(R.id.imgClear);
        autoSuggestText = findViewById(R.id.auto_suggest);
        recyclerView = findViewById(R.id.recyclerview);
        card_view_recycler = findViewById(R.id.card_view_recycler);
        mLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setVisibility(View.GONE);
        card_view_recycler.setVisibility(View.GONE);
        imgMarker.setVisibility(View.GONE);


        transparentProgressDialog = new TransparentProgressDialog(this, R.drawable.circle_loader, "");
        handler = new Handler();
    }

    public void selectedPlaceName(String name) {
        if (NetworkUtils.isNetworkConnected(MainActivity.this)) {
            setTextWatcherDisabled(true);
            autoSuggestText.clearFocus();
            autoSuggestText.setText(name);
            imgMarker.setVisibility(View.VISIBLE);
            getGeoCode(name);
        } else {
//            showToast(getString(R.string.pleaseCheckInternetConnection));
            showToast("Please check Internet connection");
        }
    }

    private void getGeoCode(String geocodeText) {
        show();
        MapmyIndiaGeoCoding.builder()
                .setAddress(geocodeText)
                .build().enqueueCall(new Callback<GeoCodeResponse>() {
            @Override
            public void onResponse(Call<GeoCodeResponse> call, retrofit2.Response<GeoCodeResponse> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        List<GeoCode> placesList = response.body().getResults();
                        GeoCode place = placesList.get(0);
                        String add = "Latitude: " + place.latitude + " longitude: " + place.longitude;
                        addMarker(place.latitude, place.longitude);
                        showToast(add);

                        DecimalFormat df = new DecimalFormat("#.######");
                        lat = df.format(place.latitude);
                        lng = df.format(place.longitude);
                    } else {
                        showToast("Not able to get value, Try again.");
                    }
                } else {
                    showToast(response.message());
                }
                hide();
            }

            @Override
            public void onFailure(Call<GeoCodeResponse> call, Throwable t) {
                showToast(t.toString());
                hide();
            }
        });
    }

    private void addMarker(double latitude, double longitude) {
        if (mMap!=null)
            mMap.clear();
        com.mapbox.mapboxsdk.geometry.LatLng placeLatLng = new com.mapbox.mapboxsdk.geometry.LatLng(latitude, longitude);
        com.mapbox.mapboxsdk.annotations.MarkerOptions markerOptions = new com.mapbox.mapboxsdk.annotations.MarkerOptions().position(placeLatLng).title("TEST");
//        mMap.addMarker(markerOptions);
        mMap.animateCamera(com.mapbox.mapboxsdk.camera.CameraUpdateFactory.newLatLngZoom(placeLatLng, 14));
    }

    private void callAutoSuggestApi(String searchString) {
        MapmyIndiaAutoSuggest.builder()
                .query(searchString)
                .build()
                .enqueueCall(new Callback<AutoSuggestAtlasResponse>() {
                    @Override
                    public void onResponse(Call<AutoSuggestAtlasResponse> call, retrofit2.Response<AutoSuggestAtlasResponse> response) {
                        if (response.code() == 200) {
                            if (response.body() != null) {
                                ArrayList<ELocation> suggestedList = response.body().getSuggestedLocations();
                                if (suggestedList.size() > 0) {
                                    if (isTextWatcherDisabled()) {
                                        recyclerView.setVisibility(View.GONE);
                                        card_view_recycler.setVisibility(View.GONE);
                                    } else {
                                        recyclerView.setVisibility(View.VISIBLE);
                                        card_view_recycler.setVisibility(View.VISIBLE);
                                    }
                                    AutoSuggestAdapter autoSuggestAdapter = new AutoSuggestAdapter(suggestedList, name -> {
                                        selectedPlaceName(name);
                                        recyclerView.setVisibility(View.GONE);
                                        card_view_recycler.setVisibility(View.GONE);
                                    });
                                    recyclerView.setAdapter(autoSuggestAdapter);
                                }
                            } else {
                                showToast("Not able to get value, Try again.");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AutoSuggestAtlasResponse> call, Throwable t) {
                        showToast(t.toString());
                    }
                });

    }

    private void show() {
        transparentProgressDialog.show();
    }

    private void hide() {
        transparentProgressDialog.dismiss();
    }

    private void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        handler.postDelayed(() -> {
            recyclerView.setVisibility(View.GONE);
            card_view_recycler.setVisibility(View.GONE);
            if (s.length() < 3)
                recyclerView.setAdapter(null);

            if (s.toString().trim().length() < 2) {
                recyclerView.setAdapter(null);
                return;
            }

            if (s.length() > 2) {
                if (NetworkUtils.isNetworkConnected(MainActivity.this)) {
                    callAutoSuggestApi(s.toString());
                } else {
//                    showToast(getString(R.string.pleaseCheckInternetConnection));
                    showToast("Please check Internet connection");
                }
            }
        }, 300);

    }

    @Override
    public void afterTextChanged(Editable s) {
    }


    private void reverseGeocoderNew(double latitude, double longitude) {
        show();
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String subCity = addresses.get(0).getSubLocality();
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                String subAdminArea = addresses.get(0).getSubAdminArea();
//            Toast.makeText(this, "Address: " + address, Toast.LENGTH_SHORT).show();

                if (null==subCity){
                    subCity="";
                }if (null==city){
                    city="";
                }if (null==state){
                    state="";
                }if (null==country){
                    country="";
                }if (null==postalCode){
                    postalCode="";
                }


                String requiredAddress = subCity + ", " + city + ", " + state + ", " + country + ", " + postalCode;

                setTextWatcherDisabled(true);
                autoSuggestText.clearFocus();
                autoSuggestText.setText(requiredAddress);

                if (autoSuggestTextGoogle!=null)
                    autoSuggestTextGoogle.setText(requiredAddress); //testing

                if (requiredAddress.equalsIgnoreCase(", , , , ") || TextUtils.isEmpty(requiredAddress)){
                    lat = null;
                    lng = null;
                }
            }
            hide();

        } catch (IOException e) {
            hide();
//            e.printStackTrace();
        }


    }
}
