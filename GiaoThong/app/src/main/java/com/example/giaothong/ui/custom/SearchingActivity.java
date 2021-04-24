package com.example.giaothong.ui.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.giaothong.R;
import com.example.giaothong.data.Address;
import com.example.giaothong.di.AddressHistoryAdapter;
import com.example.giaothong.di.SuggestAdapter;
import com.example.giaothong.service.jsonParser.PlaceSuggestParser;
import com.example.giaothong.task.AfterGetLatLng;
import com.example.giaothong.task.AsyncResponse;
import com.example.giaothong.task.GetTask;
import com.example.giaothong.utils.MyDatabaseHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static android.provider.SettingsSlicesContract.KEY_LOCATION;
import static com.example.giaothong.utils.Constants.ID_ADDRESS_HISTORY;

public class SearchingActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponse, AfterGetLatLng {
    LinearLayout layoutSearch;
    EditText etSearch;
    ListView lvSuggest;
    RelativeLayout layoutMyPosition;
    ListView lvHistory;
    ImageButton btnBack;
    ProgressBar progressBar;
    SuggestAdapter suggestAdapter;
    PlaceSuggestParser placeSuggestParser;
    List<Address> addressList = new ArrayList<>();
    String addressTitle;
    String action;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;
    private PlaceDetectionClient mPlaceDetectionClient;
    private AddressHistoryAdapter addressHistoryAdapter;
    Context context;
    MyDatabaseHelper myDatabaseHelper;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        if (getIntent().getStringExtra("action") != null) {
            action = getIntent().getStringExtra("action");
        }
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        initUI();
        setAdapter();
        setListener();
    }

    private void initUI() {
        layoutSearch = findViewById(R.id.layoutSearch);
        etSearch = findViewById(R.id.etSearch);
        lvSuggest = findViewById(R.id.lvSuggest);
        layoutMyPosition = findViewById(R.id.layoutMyPosition);
        lvHistory = findViewById(R.id.lvHistory);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);

        context = getApplicationContext();
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this);
        placeSuggestParser = new PlaceSuggestParser();

        myDatabaseHelper = new MyDatabaseHelper(getApplicationContext());
        addressHistoryAdapter = new AddressHistoryAdapter(getApplicationContext(), myDatabaseHelper.getAllHistories());

        if (getIntent().getBooleanExtra("setTextFrom", false)) {
            etSearch.setHint("From");
        }
        if (getIntent().getBooleanExtra("setTextTo", false)) {
            etSearch.setHint("To");
        }
    }

    private void setAdapter() {
        lvHistory.setAdapter(addressHistoryAdapter);
    }

    private void setListener() {
        layoutSearch.setOnClickListener(this);
        layoutMyPosition.setOnClickListener(this);
        btnBack.setOnClickListener(this);

        lvSuggest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                String placeId = addressList.get(position).getPlaceId();
                addressTitle = addressList.get(position).getAddressTitle();
                GetTask getTask = new GetTask(progressBar);

                String query = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=" + getResources().getString(R.string.google_maps_key);
                getTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
                getTask.afterGetLatLng = SearchingActivity.this;
            }
        });
        lvHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                Address address = myDatabaseHelper.getAllHistories().get(position);
                if (address != null) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("lat", address.getLatLng().latitude);
                    returnIntent.putExtra("lng", address.getLatLng().longitude);
                    returnIntent.putExtra("addressTitle", address.getAddressTitle());
                    returnIntent.putExtra("addressDetail", address.getAddressDetail());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etSearch.getText().toString().equals("")) {
                    lvSuggest.setVisibility(View.GONE);
                    lvHistory.setVisibility(View.VISIBLE);
                    layoutMyPosition.setVisibility(View.VISIBLE);
                } else if (etSearch.getText().toString().length() >= 1) {
                    if (lvSuggest.getVisibility() == View.GONE)
                        lvSuggest.setVisibility(View.VISIBLE);
                    lvHistory.setVisibility(View.GONE);
                    layoutMyPosition.setVisibility(View.GONE);

                    GetTask getTask = new GetTask(progressBar);
                    String query = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + etSearch.getText().toString() + "&key=" + getResources().getString(R.string.google_maps_key);
                    getTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, query);
                    getTask.asyncResponse = SearchingActivity.this;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutMyPosition:
                progressBar.setVisibility(View.VISIBLE);
                getDeviceLocation();
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
        }
    }

    @Override
    public void processFinish(String output) {
        try {
            addressList = placeSuggestParser.parse(output);
            suggestAdapter = new SuggestAdapter(addressList, getApplicationContext());
            lvSuggest.setAdapter(suggestAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void goToRouting(String output) {
        try {
            Address address = placeSuggestParser.parseToAddress(output);
            if (address != null) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("lat", address.getLatLng().latitude);
                returnIntent.putExtra("lng", address.getLatLng().longitude);
                returnIntent.putExtra("addressTitle", address.getAddressTitle());
                returnIntent.putExtra("addressDetail", address.getAddressDetail());
                setResult(Activity.RESULT_OK, returnIntent);


                // add to history
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                int pos = sp.getInt(ID_ADDRESS_HISTORY, -1);
                pos++;
                MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
                myDatabaseHelper.addAddress(address);

                SharedPreferences.Editor editor = sp.edit();
                editor.putInt(ID_ADDRESS_HISTORY, pos);
                editor.apply();

                finish();

            } else {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void getDeviceLocation() {
        try {
            @SuppressWarnings("MissingPermission") Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        mLastKnownLocation = task.getResult();
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("lat", mLastKnownLocation.getLatitude());
                        returnIntent.putExtra("lng", mLastKnownLocation.getLongitude());
                        returnIntent.putExtra("addressTitle", "current place");
                        setResult(Activity.RESULT_OK, returnIntent);
                    } else {
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_CANCELED, returnIntent);
                    }
                    finish();
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}
