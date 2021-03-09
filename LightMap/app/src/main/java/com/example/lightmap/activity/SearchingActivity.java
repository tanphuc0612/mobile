package com.example.lightmap.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.example.lightmap.R;
import com.example.lightmap.adapter.AddressHistoryAdapter;
import com.example.lightmap.adapter.SuggestAdapter;
import com.example.lightmap.jsonParser.PlaceSuggestParser;
import com.example.lightmap.model.Address;
import com.example.lightmap.task.AfterGetLatLng;
import com.example.lightmap.task.AsyncResponse;
import com.example.lightmap.task.GetTask;
import com.example.lightmap.util.MyDatabaseHelper;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.lightmap.util.Constant.ID_ADDRESS_HISTORY;
import static com.example.lightmap.util.Constant.M_MAX_ENTRIES;

public class SearchingActivity extends AppCompatActivity implements View.OnClickListener, AsyncResponse, AfterGetLatLng {
    @BindView(R.id.layoutSearch)
    LinearLayout layoutSearch;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.lvSuggest)
    ListView lvSuggest;
    @BindView(R.id.layoutMyPosition)
    RelativeLayout layoutMyPosition;
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
    @BindView(R.id.lvHistory)
    ListView lvHistory;
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    Context context;
    MyDatabaseHelper myDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searching);
        ButterKnife.bind(this);
        if (getIntent().getStringExtra("action") != null) {
            action = getIntent().getStringExtra("action");
        }

        initUI();
        setAdapter();
        setListener();
    }


    private void initUI() {
        context = getApplicationContext();
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
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

                String query = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeId + "&key=" + getResources().getString(R.string.google_maps_key2);
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
                    String query = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + etSearch.getText().toString() + "&key=" + getResources().getString(R.string.google_maps_key2);
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
                getMyPosition();
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
            Log.d("SONSONSON", "yes");
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

    private void getMyPosition() {
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
                            mLikelyPlaceNames = new String[count];
                            mLikelyPlaceAddresses = new String[count];
                            mLikelyPlaceAttributions = new String[count];
                            mLikelyPlaceLatLngs = new LatLng[count];

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
                            addressList = new ArrayList<>();
                            for (int j = 0; j < mLikelyPlaceNames.length; j++) {

                                addressList.add(new com.example.lightmap.model.Address(
                                        mLikelyPlaceNames[j], mLikelyPlaceAddresses[j], mLikelyPlaceLatLngs[j]));
//                                Log.i(TAG, mLikelyPlaceAddresses[j]);
                            }

                            Address address = addressList.get(0);

                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("lat", address.getLatLng().latitude);
                            returnIntent.putExtra("lng", address.getLatLng().longitude);
                            returnIntent.putExtra("addressTitle", address.getAddressTitle());
                            returnIntent.putExtra("addressDetail", address.getAddressDetail());
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();

                            // Show a dialog offering the user the list of likely places, and add a
                            // marker at the selected place.

//                                openPlacesDialog();

                        } else {
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_CANCELED, returnIntent);
                            finish();
//                            Log.e(TAG, "Exception: %s", task.getException());
                        }
                    }
                });
    }
}
