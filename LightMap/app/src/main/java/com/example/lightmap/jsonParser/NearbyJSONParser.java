package com.example.lightmap.jsonParser;

import com.example.lightmap.model.Address;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NearbyJSONParser {

    public Address[] parseToAddress(JSONObject jsonObject) throws JSONException {
        if (jsonObject.getString("status").equals("OK")) {

            JSONArray jResult = jsonObject.getJSONArray("results");

            Address[] addresses = new Address[jResult.length()];

            for (int i = 0; i < jResult.length(); i++) {
                addresses[i] = new Address();
                addresses[i].setAddressTitle(jResult.getJSONObject(i).getString("name"));
                addresses[i].setAddressDetail(jResult.getJSONObject(i).getString("vicinity"));

                double lat = Double.parseDouble(jResult.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lat"));
                double lng = Double.parseDouble(jResult.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getString("lng"));

                addresses[i].setLatLng(new com.google.android.gms.maps.model.LatLng(lat, lng));
            }
            return addresses;
        }
        return null;
    }

}
