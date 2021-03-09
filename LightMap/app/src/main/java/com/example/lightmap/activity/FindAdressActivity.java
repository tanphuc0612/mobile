package com.example.lightmap.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lightmap.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.provider.SettingsSlicesContract.KEY_LOCATION;
import static com.example.lightmap.activity.RoutingAcitivity.showDialogChooseLayer;
import static com.example.lightmap.util.Ads.showAds;
import static com.example.lightmap.util.Constant.DEFAULT_ZOOM;
import static com.example.lightmap.util.Constant.KEY_CAMERA_POSITION;
import static com.example.lightmap.util.Constant.MIN_DISTANCE;
import static com.example.lightmap.util.Constant.MIN_TIME;
import static com.example.lightmap.util.Constant.mDefaultLocation;

public class FindAdressActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, MapActivityInterface
        , android.location.LocationListener {
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE_SEARCH = 2;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int FIND_ADDRESS = 0;
    private boolean mLocationPermissionGranted;
    private static final String TAG = RoutingAcitivity.class.getName();
    private GoogleMap mMap;
    ArrayList markerPoints = new ArrayList();
    LatLng origin, dest, oldLocation;
    FloatingActionButton btnMyLocation, btnLayers;
    ImageButton btnBack;
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    private CameraPosition mCameraPosition;
    private LocationManager locationManager;
    Marker marker;
    TextView txtSearch;
    TextView txtAddressTitle, txtAddressDetail;
    private boolean isShare = false;
    String linkShare = null;
    @BindView(R.id.txtShare)
    TextView txtShare;
    @BindView(R.id.layoutBottom)
    LinearLayout layoutBottom;
    @BindView(R.id.txtGoToAddress)
    TextView txtGoToAddress;
    LatLng latLng;
    String addressTitle, addressDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_adress);
        ButterKnife.bind(this);
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

    private void setListener() {
        btnMyLocation.setOnClickListener(this);
        btnLayers.setOnClickListener(this);
        txtSearch.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        txtShare.setOnClickListener(this);
        txtGoToAddress.setOnClickListener(this);
    }

    private void initUI() {
        btnLayers = findViewById(R.id.btnLayers);
        btnMyLocation = findViewById(R.id.btnMyLocation);
        btnBack = findViewById(R.id.btnBack);
        txtSearch = findViewById(R.id.txtSearch);
        txtAddressTitle = findViewById(R.id.addressTitle);
        txtAddressDetail = findViewById(R.id.addressDetail);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates(FindAdressActivity.this);

        float rotation = (float) SphericalUtil.computeHeading(oldLocation, latLng);
//        rotateMarker(marker, latLng, rotation);
//        marker.setRotation(location.getBearing());
        Log.d("SonSOn", location.getBearing() + "");
        oldLocation = latLng;
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnLayers:
                showDialogChooseLayer(mMap, FindAdressActivity.this);
                break;
            case R.id.btnMyLocation:
                getDeviceLocation();
//                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , MIN_TIME, MIN_DISTANCE, this);
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.txtSearch:
                Intent intent = new Intent(FindAdressActivity.this, SearchingActivity.class);
                startActivityForResult(intent, FIND_ADDRESS);
//                try {
//                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
//                            .build(this);
//
//                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_SEARCH);
//                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
//                    // TODO: Handle the error.
//                }
                break;

            case R.id.txtShare:
                isShare = true;
                if (linkShare != null)
                    intentShare();
                break;
            case R.id.txtGoToAddress:
                if (latLng != null) {
                    Intent intent1 = new Intent(FindAdressActivity.this, RoutingAcitivity.class);
                    intent1.putExtra("GoToAddress", true);
                    intent1.putExtra("dest-lat", latLng.latitude);
                    intent1.putExtra("dest-lng", latLng.longitude);
                    intent1.putExtra("addressTitle", addressTitle);
                    startActivity(intent1);
                }
                break;

        }

    }

    private void intentShare() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, linkShare);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share_with)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == RESULT_OK && requestCode == FIND_ADDRESS) {
            Double lat = data.getDoubleExtra("lat", 0);
            Double lng = data.getDoubleExtra("lng", 0);
            latLng = new LatLng(lat, lng);

            addressTitle = data.getStringExtra("addressTitle");
            addressDetail = data.getStringExtra("addressDetail");

            txtSearch.setText(addressTitle);
            txtAddressTitle.setText(addressTitle);
            txtAddressDetail.setText(addressDetail);
            addPoint(latLng);


            linkShare = addressTitle + "  " + addressDetail + "  " + "https://www.google.com/maps/?q=" + lat + "," + lng;

            layoutBottom.setVisibility(View.VISIBLE);
        } else {
            layoutBottom.setVisibility(View.GONE);
        }

    }

    void addPoint(LatLng latLng) {

        if (markerPoints.size() > 1) {
            markerPoints.clear();
            mMap.clear();
        }

        // Adding new item to the ArrayList
        markerPoints.add(latLng);

        // Creating MarkerOptions
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        mMap.addMarker(options);

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
                                marker = mMap.addMarker(new MarkerOptions()
                                    .title(addressTitle)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_red))
                                        .flat(true)
                                        .position(new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude())));
//                                    .snippet(addressDetail));
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

    @Override
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng hanoi = new LatLng(21.027266, 105.855453);
        //mMap.addMarker(new MarkerOptions().position(hanoi).title("Marker in hanoi"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hanoi, 16));

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
}
