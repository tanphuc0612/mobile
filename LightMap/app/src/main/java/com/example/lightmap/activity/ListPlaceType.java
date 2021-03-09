package com.example.lightmap.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.lightmap.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.lightmap.util.Ads.showAds;

public class ListPlaceType extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.layoutRestaurant)
    LinearLayout layoutRestaurant;
    @BindView(R.id.layoutBar)
    LinearLayout layoutBar;
    @BindView(R.id.layoutCoffee)
    LinearLayout layoutCoffee;
    @BindView(R.id.layoutDelivery)
    LinearLayout layoutDelivery;
    @BindView(R.id.layoutPark)
    LinearLayout layoutPark;
    @BindView(R.id.layoutGym)
    LinearLayout layoutGym;
    @BindView(R.id.layoutArt)
    LinearLayout layoutArt;
    @BindView(R.id.layoutStadium)
    LinearLayout layoutStadium;
    @BindView(R.id.layoutNightlife)
    LinearLayout layoutNightlife;
    @BindView(R.id.layoutAmusementPark)
    LinearLayout layoutAmusementPark;
    @BindView(R.id.layoutMovie)
    LinearLayout layoutMovie;
    @BindView(R.id.layoutMuseum)
    LinearLayout layoutMuseum;
    @BindView(R.id.layoutLibrary)
    LinearLayout layoutLibrary;
    @BindView(R.id.layoutGroceries)
    LinearLayout layoutGroceries;
    @BindView(R.id.layoutBook)
    LinearLayout layoutBeauty;
    @BindView(R.id.layoutCarDealer)
    LinearLayout layoutCarDealer;
    @BindView(R.id.layoutHomeGarden)
    LinearLayout layoutHomeGarden;
    @BindView(R.id.layoutClothing)
    LinearLayout layoutApparel;
    @BindView(R.id.layoutShoppingCenter)
    LinearLayout layoutShoppingCenter;
    @BindView(R.id.layoutElectronics)
    LinearLayout layoutElectronics;
    @BindView(R.id.layoutJelwery)
    LinearLayout layoutJelwery;
    @BindView(R.id.layoutConvenienceStore)
    LinearLayout layoutConvenienceStore;
    @BindView(R.id.layoutHotel)
    LinearLayout layoutHotel;
    @BindView(R.id.layoutAtm)
    LinearLayout layoutAtm;
    @BindView(R.id.layoutBeautySalon)
    LinearLayout layoutBeautySalon;
    @BindView(R.id.layoutPharmacy)
    LinearLayout layoutPharmacy;
    @BindView(R.id.layoutBank)
    LinearLayout layoutBank;
    @BindView(R.id.layoutDryClean)
    LinearLayout layoutDryClean;
    @BindView(R.id.layoutGas)
    LinearLayout layoutGas;
    @BindView(R.id.layoutHospital)
    LinearLayout layoutHospital;
    @BindView(R.id.layoutParking)
    LinearLayout layoutParking;
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    SparseArray<String> stringSparseArray = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_place_type);

        ButterKnife.bind(this);

        showAds(getApplicationContext());

        btnBack.setOnClickListener(this);

        layoutRestaurant.setOnClickListener(this);
        layoutBar.setOnClickListener(this);
        layoutCoffee.setOnClickListener(this);
        layoutDelivery.setOnClickListener(this);

        layoutPark.setOnClickListener(this);
        layoutGym.setOnClickListener(this);
        layoutArt.setOnClickListener(this);
        layoutStadium.setOnClickListener(this);
        layoutNightlife.setOnClickListener(this);
        layoutAmusementPark.setOnClickListener(this);
        layoutMovie.setOnClickListener(this);
        layoutMuseum.setOnClickListener(this);
        layoutLibrary.setOnClickListener(this);

        layoutGroceries.setOnClickListener(this);
        layoutBeauty.setOnClickListener(this);
        layoutCarDealer.setOnClickListener(this);
        layoutHomeGarden.setOnClickListener(this);
        layoutApparel.setOnClickListener(this);
        layoutShoppingCenter.setOnClickListener(this);
        layoutElectronics.setOnClickListener(this);
        layoutJelwery.setOnClickListener(this);
        layoutConvenienceStore.setOnClickListener(this);

        layoutHotel.setOnClickListener(this);
        layoutAtm.setOnClickListener(this);
        layoutBeautySalon.setOnClickListener(this);
        layoutPharmacy.setOnClickListener(this);
        layoutBank.setOnClickListener(this);
        layoutDryClean.setOnClickListener(this);
        layoutGas.setOnClickListener(this);
        layoutHospital.setOnClickListener(this);
        layoutParking.setOnClickListener(this);


        stringSparseArray.append(R.id.layoutRestaurant, "restaurant");
        stringSparseArray.append(R.id.layoutBar, "bar");
        stringSparseArray.append(R.id.layoutCoffee, "cafe");
        stringSparseArray.append(R.id.layoutDelivery, "meal_delivery");

        stringSparseArray.append(R.id.layoutPark, "park");
        stringSparseArray.append(R.id.layoutGym, "gym");
        stringSparseArray.append(R.id.layoutArt, "art_gallery");
        stringSparseArray.append(R.id.layoutStadium, "stadium");
        stringSparseArray.append(R.id.layoutNightlife, "night_club");
        stringSparseArray.append(R.id.layoutAmusementPark, "amusement_park");
        stringSparseArray.append(R.id.layoutMovie, "movie_theater");
        stringSparseArray.append(R.id.layoutMuseum, "museum");
        stringSparseArray.append(R.id.layoutLibrary, "library");

        stringSparseArray.append(R.id.layoutGroceries, "department_store");
        stringSparseArray.append(R.id.layoutBook, "book_store");
        stringSparseArray.append(R.id.layoutCarDealer, "car_dealer");
        stringSparseArray.append(R.id.layoutHomeGarden, "home_goods_store");
        stringSparseArray.append(R.id.layoutClothing, "clothing_store");
        stringSparseArray.append(R.id.layoutShoppingCenter, "shopping_mall");
        stringSparseArray.append(R.id.layoutElectronics, "electronics_store");
        stringSparseArray.append(R.id.layoutJelwery, "jewelry_store");
        stringSparseArray.append(R.id.layoutConvenienceStore, "convenience_store");

        stringSparseArray.append(R.id.layoutHotel, "lodging");
        stringSparseArray.append(R.id.layoutAtm, "atm");
        stringSparseArray.append(R.id.layoutBeautySalon, "beauty_salon");
        stringSparseArray.append(R.id.layoutPharmacy, "pharmacy");
        stringSparseArray.append(R.id.layoutBank, "bank");
        stringSparseArray.append(R.id.layoutDryClean, "laundry");
        stringSparseArray.append(R.id.layoutGas, "gas_station");
        stringSparseArray.append(R.id.layoutHospital, "hospital");
        stringSparseArray.append(R.id.layoutParking, "parking");

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnBack) {
            onBackPressed();
            return;
        }
        Intent intent = new Intent(ListPlaceType.this, FindNearByActivity.class);
        matchIdWithKey(stringSparseArray.get(v.getId()), intent);

    }

    void matchIdWithKey(String key, Intent intent) {
        intent.putExtra("place_type", key);
        startActivity(intent);
    }
}
