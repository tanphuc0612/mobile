package com.example.lightmap.util;

import com.google.android.gms.maps.model.LatLng;

public class Constant {
    public static final int DEFAULT_ZOOM = 16;
    public static final LatLng mDefaultLocation = new LatLng(21.027266, 105.855453);
    // Keys for storing activity state.
    public static final String KEY_CAMERA_POSITION = "camera_position";
    public static final String KEY_LOCATION = "location";
    // Used for selecting the current place.
    public static final int M_MAX_ENTRIES = 8;
    public static final long MIN_TIME = 400;
    public static final float MIN_DISTANCE = 1000;
    public static final String ID_ADDRESS_HISTORY = "id_address_history";
}
