package com.example.lightmap.model;

import com.google.android.gms.maps.model.LatLng;

public class Address {
    private String addressTitle;
    private String addressDetail;
    private LatLng latLng;
    private String placeId;

    public Address(String addressTitle, String addressDetail, LatLng latLng) {
        this.addressTitle = addressTitle;
        this.addressDetail = addressDetail;
        this.latLng = latLng;
    }

    public Address() {

    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String id) {
        this.placeId = id;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getAddressTitle() {
        return addressTitle;
    }

    public void setAddressTitle(String addressTitle) {
        this.addressTitle = addressTitle;
    }

    public String getAddressDetail() {
        return addressDetail;
    }

    public void setAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }


}
