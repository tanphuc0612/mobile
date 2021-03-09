package com.example.lightmap.activity;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lightmap.jsonParser.DirectionsJSONParser;
import com.example.lightmap.jsonParser.JSONParser;
import com.example.lightmap.R;
import com.example.lightmap.adapter.DirectionAdapter;
import com.example.lightmap.adapter.DirectionBusAdapter;
import com.example.lightmap.model.Bus.LegBus;
import com.example.lightmap.model.Bus.StepBus;
import com.example.lightmap.model.Leg;
import com.example.lightmap.model.Step;
import com.example.lightmap.util.LatLngInterpolator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.model.TravelMode;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.provider.SettingsSlicesContract.KEY_LOCATION;
import static com.example.lightmap.util.Ads.showAds;
import static com.example.lightmap.util.Constant.DEFAULT_ZOOM;
import static com.example.lightmap.util.Constant.KEY_CAMERA_POSITION;
import static com.example.lightmap.util.Constant.MIN_DISTANCE;
import static com.example.lightmap.util.Constant.MIN_TIME;
import static com.example.lightmap.util.Constant.M_MAX_ENTRIES;
import static com.example.lightmap.util.Constant.mDefaultLocation;
import static com.example.lightmap.util.Utils.getZoomLevel;
import static com.example.lightmap.util.Utils.midPoint;

public class RoutingAcitivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, MapActivityInterface
        , android.location.LocationListener {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private static final int ROUTING_TO = 6;
    private static final int ROUTING_FROM = 5;
    private boolean mLocationPermissionGranted;
    private static final String TAG = RoutingAcitivity.class.getName();
    private GoogleMap mMap;
    ArrayList markerPoints = new ArrayList();
    private ImageButton btnCar, btnBus, btnWalk, btnBicycle;
    private TextView txtFrom, txtTo, txtFrom2, txtTo2;
    LatLng origin, dest, oldLocation;
    ProgressBar progressBar;
    private TravelMode travelMode = TravelMode.DRIVING;
    RecyclerView listViewDirection;
    DirectionAdapter directionAdapter;
    DirectionBusAdapter directionBusAdapter;
    TextView duration, distance, showStep;
    CharSequence strOrigin, strDest;
    Leg leg;
    LegBus legBus;
    SlidingUpPanelLayout sliding_layout;
    private RecyclerView.LayoutManager layoutManager;
    ImageView imgSwitch;
    private Polyline polyline;
    FloatingActionButton btnMyLocation, btnLayers;
    boolean cancelTaskBefore = false;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;
    private LocationManager locationManager;
    Marker marker;
    ImageButton btnBack;
    String addressTitle;
    LinearLayout dragView;
    TextView txtSlide;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routing);
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        initUI();

        setListener();

        showAds(getApplicationContext());

//        slidingLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
//        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    private void setListener() {
        btnCar.setOnClickListener(this);
        btnBus.setOnClickListener(this);
        btnBicycle.setOnClickListener(this);
        btnWalk.setOnClickListener(this);
        txtFrom.setOnClickListener(this);
        txtTo.setOnClickListener(this);
        imgSwitch.setOnClickListener(this);
        btnMyLocation.setOnClickListener(this);
        btnLayers.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        changeIconTransport(travelMode);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        sliding_layout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState.equals(SlidingUpPanelLayout.PanelState.COLLAPSED)) {
                    txtSlide.setText("Show steps");
                }
                if (newState.equals(SlidingUpPanelLayout.PanelState.EXPANDED)) {
                    txtSlide.setText("Show map");
                }
            }
        });
    }

    private void initUI() {
        progressBar = findViewById(R.id.progressBar);
        btnCar = findViewById(R.id.btnCar);
        btnBus = findViewById(R.id.btnBus);
        btnBicycle = findViewById(R.id.btnBicycle);
        btnWalk = findViewById(R.id.btnWalk);
        txtFrom = findViewById(R.id.txtFrom);
        txtTo = findViewById(R.id.txtTo);
        txtFrom2 = findViewById(R.id.txtFrom2);
        txtTo2 = findViewById(R.id.txtTo2);
        listViewDirection = findViewById(R.id.listViewDirection);
        duration = findViewById(R.id.duration);
        distance = findViewById(R.id.distance);
        sliding_layout = findViewById(R.id.sliding_layout);
        txtSlide = findViewById(R.id.txtSlide);
        imgSwitch = findViewById(R.id.imgSwitch);
        btnLayers = findViewById(R.id.btnLayers);
        btnMyLocation = findViewById(R.id.btnMyLocation);
        btnBack = findViewById(R.id.btnBack);
        dragView = findViewById(R.id.dragView);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng hanoi = new LatLng(21.027266, 105.855453);
        //mMap.addMarker(new MarkerOptions().position(hanoi).title("Marker in hanoi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hanoi, DEFAULT_ZOOM));

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    public void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this); //You can also use LocationManager.GPS_PROVIDER and LocationManager.PASSIVE_PROVIDER

            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {

                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                                oldLocation = new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude());
//                                marker = mMap.addMarker(new MarkerOptions()
////                                    .title(addressTitle)
////                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.gps))
//                                        .flat(true)
//                                        .position(new LatLng(mLastKnownLocation.getLatitude(),
//                                                mLastKnownLocation.getLongitude())));
//                                    .snippet(addressDetail));
                                if (getIntent().getBooleanExtra("GoToAddress", false)) {
                                    dest = new LatLng(getIntent().getDoubleExtra("dest-lat", 0),
                                            getIntent().getDoubleExtra("dest-lng", 0));

                                    getCurrentPlaceAndFindRoute();
                                }
                            }

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }


                    }


                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getCurrentPlaceAndFindRoute() {
        PlaceDetectionClient mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        @SuppressWarnings("MissingPermission") final Task<PlaceLikelihoodBufferResponse> placeResult =
                mPlaceDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener
                (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                            // Set the count, handling cases where less than 5 entries are returned.
                            int count;
                            if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                count = likelyPlaces.getCount();
                            } else {
                                count = M_MAX_ENTRIES;
                            }

                            int i = 0;
                            String[] mLikelyPlaceNames = new String[count];
                            String[] mLikelyPlaceAddresses = new String[count];
                            String[] mLikelyPlaceAttributions = new String[count];
                            LatLng[] mLikelyPlaceLatLngs = new LatLng[count];


                            for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                // Build a list of likely places to show the user.
                                mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                        .getAddress();
                                mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                        .getAttributions();
                                mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                i++;
                                if (i > (count - 1)) {
                                    break;
                                }
                            }

                            origin = mLikelyPlaceLatLngs[0];
                            txtTo.setText(getIntent().getStringExtra("addressTitle"));
                            txtFrom.setText(mLikelyPlaceNames[0]);
                            if (readyToFindRoute())
                                findRoute();
                        } else {

                        }
                    }
                });
    }


    void addPoints(LatLng latLng, String type) {

        if (markerPoints.size() > 1) {
            markerPoints.clear();
            mMap.clear();
        }

        // Adding new item to the ArrayList
        markerPoints.add(latLng);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(latLng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        if (type.equals("origin")) {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else {
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
//        if (markerPoints.size() == 1) {
//            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//        } else if (markerPoints.size() == 2) {
//            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//        }

        // Add new marker to the Google Map Android API V2

        mMap.addMarker(options);

        // Checks, whether start and end locations are captured
//        if (markerPoints.size() >= 2) {
//            LatLng origin = (LatLng) markerPoints.get(0);
//            LatLng dest = (LatLng) markerPoints.get(1);
//
//            // Getting URL to the Google Directions API
//            String url = getDirectionsUrl(origin, dest);
//
//            DownloadTask downloadTask = new DownloadTask();
//
//            // Start downloading json data from Google Directions API
//            downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
//        }
    }

    void add2Points(LatLng origin, LatLng dest) {

        if (markerPoints.size() > 0) {
            markerPoints.clear();
            mMap.clear();
        }
        MarkerOptions options = new MarkerOptions();
        options.position(origin);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMap.addMarker(options);

        MarkerOptions options2 = new MarkerOptions();
        options2.position(dest);
        options2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        mMap.addMarker(options2);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtFrom:
                Intent intent = new Intent(RoutingAcitivity.this, SearchingActivity.class);
                intent.putExtra("setTextFrom", true);
                startActivityForResult(intent, ROUTING_FROM);
                break;

            case R.id.txtTo:
                Intent intent2 = new Intent(RoutingAcitivity.this, SearchingActivity.class);
                intent2.putExtra("setTextTo", true);
                startActivityForResult(intent2, ROUTING_TO);
                break;
            case R.id.btnCar:
                travelMode = TravelMode.DRIVING;
                if (readyToFindRoute())
                    findRoute();
                changeIconTransport(travelMode);
                break;
            case R.id.btnBus:
                travelMode = TravelMode.TRANSIT;
                if (readyToFindRoute())
                    findRoute();
                changeIconTransport(travelMode);
                break;
            case R.id.btnBicycle:
                travelMode = TravelMode.BICYCLING;
                if (readyToFindRoute())
                    findRoute();
                changeIconTransport(travelMode);
                break;
            case R.id.btnWalk:
                travelMode = TravelMode.WALKING;
                if (readyToFindRoute())
                    findRoute();
                changeIconTransport(travelMode);
                break;
            case R.id.imgSwitch:
                CharSequence tmp = strOrigin;
                txtTo.setText(tmp);
                txtFrom.setText(strDest);

                LatLng tmp2 = origin;
                origin = dest;
                dest = tmp2;
                if (readyToFindRoute())
                    findRoute();
                break;
            case R.id.btnLayers:
                showDialogChooseLayer(mMap, RoutingAcitivity.this);
                break;
            case R.id.btnMyLocation:
                getDeviceLocation();
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , MIN_TIME, MIN_DISTANCE, this);
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
        }
    }

    public static void showDialogChooseLayer(final GoogleMap mMap, Activity activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_choose_layer, null);
        builder.setView(dialogView);


        LinearLayout layout_default = dialogView.findViewById(R.id.layout_default);
        layout_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });
        LinearLayout layout_satellite = dialogView.findViewById(R.id.layout_satellite);
        layout_satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        });
        LinearLayout layout_terrain = dialogView.findViewById(R.id.layout_terrain);
        layout_terrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    boolean readyToFindRoute() {
        return txtTo.getText() != null && !txtTo.getText().equals("") &&
                txtFrom.getText() != null && !txtFrom.getText().equals("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK) {
            Double lat = data.getDoubleExtra("lat", 0);
            Double lng = data.getDoubleExtra("lng", 0);
            addressTitle = data.getStringExtra("addressTitle");
            LatLng latLng = new LatLng(lat, lng);
            if (requestCode == ROUTING_FROM) {
                origin = latLng;
                strOrigin = addressTitle;
                txtFrom.setText(strOrigin);
                txtFrom2.setText(strOrigin);
                addPoints(origin, "origin");
            } else if (requestCode == ROUTING_TO) {
                dest = latLng;
                strDest = addressTitle;
                txtTo.setText(strDest);
                txtTo2.setText(strDest);
                addPoints(dest, "dest");
            }

            if (readyToFindRoute()) {
                findRoute();
            }

        }
//        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlaceAutocomplete.getPlace(this, data);
//                origin = place.getLatLng();
//                strOrigin = place.getName();
//                txtFrom.setText(strOrigin);
//                txtFrom2.setText(strOrigin);
//                addPoints(origin, "origin");
//                if (readyToFindRoute()) {
//                    findRoute();
//                }
//            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
//                Status status = PlaceAutocomplete.getStatus(this, data);
//                Log.i(TAG, status.getStatusMessage());
//
//            }
//        }
//
//        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_TO) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlaceAutocomplete.getPlace(this, data);
//                dest = place.getLatLng();
//                strDest = place.getName();
//                txtTo.setText(strDest);
//                txtTo2.setText(strDest);
//                addPoints(dest, "dest");
//                if (readyToFindRoute()) {
//                    findRoute();
//                }
//            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
//                Status status = PlaceAutocomplete.getStatus(this, data);
//                Log.i(TAG, status.getStatusMessage());
//
//            }
//        }
    }

    void findRoute() {
        add2Points(origin, dest);
        String url = getDirectionsUrl(origin, dest);
        DownloadTask downloadTask = new DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void changeIconTransport(TravelMode mode) {
        switch (mode) {
            case DRIVING:
                btnCar.setImageDrawable(getDrawable(R.drawable.ic_car_white));
                btnCar.setBackground(getDrawable(R.drawable.bg_transport_selected));

                btnBus.setImageDrawable(getDrawable(R.drawable.ic_bus));
                btnBus.setBackground(getDrawable(R.drawable.bg_transport_unselected));
                btnBicycle.setImageDrawable(getDrawable(R.drawable.ic_bicycle));
                btnBicycle.setBackground(getDrawable(R.drawable.bg_transport_unselected));
                btnWalk.setImageDrawable(getDrawable(R.drawable.ic_walk));
                btnWalk.setBackground(getDrawable(R.drawable.bg_transport_unselected));
                break;
            case TRANSIT:
                btnCar.setImageDrawable(getDrawable(R.drawable.ic_car));
                btnCar.setBackground(getDrawable(R.drawable.bg_transport_unselected));

                btnBus.setImageDrawable(getDrawable(R.drawable.ic_bus_white));
                btnBus.setBackground(getDrawable(R.drawable.bg_transport_selected));

                btnBicycle.setImageDrawable(getDrawable(R.drawable.ic_bicycle));
                btnBicycle.setBackground(getDrawable(R.drawable.bg_transport_unselected));
                btnWalk.setImageDrawable(getDrawable(R.drawable.ic_walk));
                btnWalk.setBackground(getDrawable(R.drawable.bg_transport_unselected));
                break;
            case BICYCLING:
                btnCar.setImageDrawable(getDrawable(R.drawable.ic_car));
                btnCar.setBackground(getDrawable(R.drawable.bg_transport_unselected));
                btnBus.setImageDrawable(getDrawable(R.drawable.ic_bus));
                btnBus.setBackground(getDrawable(R.drawable.bg_transport_unselected));

                btnBicycle.setImageDrawable(getDrawable(R.drawable.ic_bicycle_white));
                btnBicycle.setBackground(getDrawable(R.drawable.bg_transport_selected));

                btnWalk.setImageDrawable(getDrawable(R.drawable.ic_walk));
                btnWalk.setBackground(getDrawable(R.drawable.bg_transport_unselected));
                break;
            case WALKING:
                btnCar.setImageDrawable(getDrawable(R.drawable.ic_car));
                btnCar.setBackground(getDrawable(R.drawable.bg_transport_unselected));
                btnBus.setImageDrawable(getDrawable(R.drawable.ic_bus));
                btnBus.setBackground(getDrawable(R.drawable.bg_transport_unselected));
                btnBicycle.setImageDrawable(getDrawable(R.drawable.ic_bicycle));
                btnBicycle.setBackground(getDrawable(R.drawable.bg_transport_unselected));

                btnWalk.setImageDrawable(getDrawable(R.drawable.ic_walk_white));
                btnWalk.setBackground(getDrawable(R.drawable.bg_transport_selected));
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(RoutingAcitivity.this);

//        float rotation = (float) SphericalUtil.computeHeading(oldLocation, latLng);
//        rotateMarker(marker, latLng, rotation);
//        marker.setRotation(location.getBearing());
        Log.d("SonSOn", location.getBearing() + "");
        oldLocation = latLng;
    }


    private void rotateMarker(final Marker marker, final LatLng destination,
                              final float rotation) {

        if (marker != null) {

            final LatLng startPosition = marker.getPosition();
            final float startRotation = marker.getRotation();

            final LatLngInterpolator latLngInterpolator = new LatLngInterpolator.Spherical();
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.setDuration(3000); // duration 3 second
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, destination);
                        float bearing = computeRotation(v, startRotation, rotation);

                        marker.setRotation(bearing);
                        marker.setPosition(newPosition);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            valueAnimator.start();
        }
    }

    private static float computeRotation(float fraction, float start, float end) {
        float normalizeEnd = end - start; // rotate start to 0
        float normalizedEndAbs = (normalizeEnd + 360) % 360;

        float direction = (normalizedEndAbs > 180) ? -1 : 1; // -1 = anticlockwise, 1 = clockwise
        float rotation;
        if (direction > 0) {
            rotation = normalizedEndAbs;
        } else {
            rotation = normalizedEndAbs - 360;
        }

        float result = fraction * rotation + start;
        return (result + 360) % 360;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public class DownloadTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setProgress(10);
            progressBar.setVisibility(View.VISIBLE);
            txtSlide.setVisibility(View.VISIBLE);
            dragView.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.executeOnExecutor(THREAD_POOL_EXECUTOR, result);

//            GetLegTask getLegTask = new GetLegTask();
//            getLegTask.executeOnExecutor(THREAD_POOL_EXECUTOR, result);
        }
    }

//    public class GetLegTask extends AsyncTask<String, Integer, Leg> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressBar.setProgress(70);
//        }
//
//        @Override
//        protected Leg doInBackground(String... strings) {
//            JSONObject jsonObject;
//            try {
//                jsonObject = new JSONObject(strings[0]);
//                JSONParser jsonParser = new JSONParser();
//                jsonParser.setDest(String.valueOf(strDest));
//                jsonParser.setOrigin(String.valueOf(strOrigin));
//                return jsonParser.parseToLeg(jsonObject);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Leg leg) {
//            super.onPostExecute(leg);
//
//            List<Step> stepList;
//            if (leg != null) {
//                progressBar.setProgress(100);
//                stepList = Arrays.asList(leg.getSteps());
//                directionAdapter = new DirectionAdapter(getApplicationContext(), stepList);
//                layoutManager = new LinearLayoutManager(getApplicationContext());
//                listViewDirection.setLayoutManager(layoutManager);
//                listViewDirection.setAdapter(directionAdapter);
//                duration.setText(leg.getDuration());
//                distance.setText(leg.getDistance());
//            } else {
//                Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_LONG).show();
//            }
//            progressBar.setVisibility(View.GONE);
//        }
//    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setProgress(50);
        }

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                //draw routes
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jObject);

                //Get leg or legBus
                JSONParser jsonParser = new JSONParser();
                jsonParser.setDest(String.valueOf(strDest));
                jsonParser.setOrigin(String.valueOf(strOrigin));
                switch (travelMode) {
                    case TRANSIT:
                        legBus = jsonParser.parseToLegBus(jObject);
                        break;
                    default:
                        leg = jsonParser.parseToLeg(jObject);
                        break;
                }
//                if (cancelTaskBefore) {
//                    cancelTaskBefore = false;
//                    return null;
//                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            progressBar.setProgress(100);
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            if (result != null) {
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList();
                    lineOptions = new PolylineOptions();

                    List<HashMap<String, String>> path = result.get(i);

                    for (int j = 0; j < path.size(); j++) {
                        HashMap point = path.get(j);

                        double lat = Double.parseDouble(String.valueOf(point.get("lat")));
                        double lng = Double.parseDouble(String.valueOf(point.get("lng")));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    lineOptions.addAll(points);
                    lineOptions.width(13);
                    lineOptions.color(Color.parseColor("#FF4597FF"));
                    lineOptions.geodesic(true);

                }

// Drawing polyline in the Google Map for the i-th route
                if (lineOptions == null) {
                    Toast.makeText(getApplicationContext(), "This travel mode is not available", Toast.LENGTH_LONG).show();
                    return;
                }

                if (polyline != null)
                    polyline.remove();

                polyline = mMap.addPolyline(lineOptions);

//                CameraPosition cameraPosition = new CameraPosition.Builder()
//                        .target(midPoint(origin.latitude, origin.longitude, dest.latitude, dest.longitude))
////                        .zoom(14)
////                        .bearing(angleBteweenCoordinate(origin.latitude, origin.longitude, dest.latitude, dest.longitude))
//                        .build();
//
//                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                float[] results = new float[1];
                Location.distanceBetween(origin.latitude, origin.longitude, dest.latitude, dest.longitude, results);
                double dummy_radius = results[0];
                double circleRad = dummy_radius * 1;//multiply by 1000 to make units in KM

                float zoomLevel = getZoomLevel(circleRad);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(midPoint(origin.latitude, origin.longitude, dest.latitude, dest.longitude), zoomLevel));

            }


            //Leg:
            if (!travelMode.equals(TravelMode.TRANSIT)) {
                if (leg != null) {
                    List<Step> stepList;
                    stepList = Arrays.asList(leg.getSteps());
                    directionAdapter = new DirectionAdapter(getApplicationContext(), stepList);
                    listViewDirection.setLayoutManager(layoutManager);
                    listViewDirection.setAdapter(directionAdapter);
                    duration.setText(leg.getDuration());
                    distance.setText(leg.getDistance());
                } else {
                    Toast.makeText(getApplicationContext(), "Error finding way", Toast.LENGTH_LONG).show();
                }
            }
            //LegBus:
            else {
                if (legBus != null) {
                    List<StepBus> stepBusList;
                    stepBusList = Arrays.asList(legBus.getStepBuses());
                    directionBusAdapter = new DirectionBusAdapter(getApplicationContext(), stepBusList);
                    listViewDirection.setLayoutManager(layoutManager);
                    listViewDirection.setAdapter(directionBusAdapter);
                    duration.setText(legBus.getDuration());
                    distance.setText(legBus.getDistance());
                } else {
                    Toast.makeText(getApplicationContext(), "Error finding way2", Toast.LENGTH_LONG).show();
                }
            }

            progressBar.setVisibility(View.GONE);
        }

    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=" + travelMode;

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getResources().getString(R.string.google_maps_key2);

        return url;
    }

    public static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


}
