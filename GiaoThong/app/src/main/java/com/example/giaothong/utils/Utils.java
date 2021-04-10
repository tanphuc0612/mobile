package com.example.giaothong.utils;

import com.google.android.gms.maps.model.LatLng;

public class Utils {
    public static LatLng midPoint(double lat1, double long1, double lat2, double long2) {
        return new LatLng((lat1 + lat2) / 2, (long1 + long2) / 2);
    }

    public static float angleBteweenCoordinate(double lat1, double long1, double lat2,
                                               double long2) {

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng;

        return (float) brng;
    }

    public static int getZoomLevel(double radius){
        double scale = radius / 500;
        return ((int) (16 - Math.log(scale) / Math.log(2)));
    }
}