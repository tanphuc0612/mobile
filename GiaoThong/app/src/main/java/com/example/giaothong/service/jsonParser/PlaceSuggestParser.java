package com.example.giaothong.service.jsonParser;

import com.example.giaothong.data.Address;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PlaceSuggestParser {
    public List<Address> parse(String input) throws JSONException {
        List<Address> list = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(input);
        if (jsonObject.get("status").equals("OK")) {
            JSONArray jsonArray = jsonObject.getJSONArray("predictions");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                Address address = new Address();
                address.setAddressTitle(jsonObject1.getString("description"));
                address.setPlaceId(jsonObject1.getString("place_id"));
                list.add(address);
            }
        }
        return list;
    }

    public Address parseToAddress(String input) throws JSONException {
        JSONObject jsonObject = new JSONObject(input);
        Address address = null;
        if (jsonObject.get("status").equals("OK")) {
            JSONObject jsonObject1 = jsonObject.getJSONObject("result");
            String addressTitle = jsonObject1.getString("name");
            String addressDetail = jsonObject1.getString("formatted_address");
            JSONObject jsonLatLng = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
            Double lat = Double.valueOf(jsonLatLng.getString("lat"));
            Double lng = Double.valueOf(jsonLatLng.getString("lng"));
            String placeId = jsonObject1.getString("place_id");

            address = new Address();
            address.setPlaceId(placeId);
            address.setAddressTitle(addressTitle);
            address.setAddressDetail(addressDetail);
            address.setLatLng(new LatLng(lat, lng));
        }
        return address;
    }
}
