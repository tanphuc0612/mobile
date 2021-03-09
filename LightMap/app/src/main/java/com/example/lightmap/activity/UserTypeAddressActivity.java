package com.example.lightmap.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.lightmap.R;
import com.example.lightmap.fragment.PlaceAutocompleteFragment2;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class UserTypeAddressActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = UserTypeAddressActivity.class.getName();
    TextView editTextSearch;
    ImageButton btnBack; int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_address);
        btnBack = findViewById(R.id.btnBack);
        editTextSearch = findViewById(R.id.editTextSearch);

        btnBack.setOnClickListener(this);
        editTextSearch.setOnClickListener(this);

        PlaceAutocompleteFragment2 autocompleteFragment = (PlaceAutocompleteFragment2)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnBack:
                onBackPressed();
                break;
            case R.id.editTextSearch:
                break;
        }
    }
}
