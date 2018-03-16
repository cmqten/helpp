package jon.usinggmaps;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import jon.usinggmaps.listeners.PlaceSelectListener;

import static android.content.ContentValues.TAG;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        ListingsAdapter.ItemClickListener, OnMapReadyCallback {

    public static Location location;
    private GoogleMap mMap;
    private ArrayList<Tab_fragment> charityTypes;
    private String[] charityTypesNames = {"All", "Commuity", "Education", "Health", "Religion", "Welfare"};

    private final int numberOfCharityTypes = 6;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        charityTypes = new ArrayList<Tab_fragment>();
        for(int i = 0; i< numberOfCharityTypes; i ++){
            Tab_fragment temp = new Tab_fragment();
            charityTypes.add(temp);
            temp.setQueryURL("");
            adapter.addFragment(temp, charityTypesNames[i]);
        }

        ViewPager mViewPager =  findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                currentPosition = position;
                charityTypes.get(position).runSearch();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }



    public void redoSearch(View view){
        view.animate().rotationBy(360f);
        searchNearby();

    }

    @Override
    public void onMapReady(final GoogleMap Map) {
        mMap = Map;
        mMap.setMinZoomPreference(15);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectListener(mMap));
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            LocationServices.getFusedLocationProviderClient(this).getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location userlocation) {
                            if (userlocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userlocation.getLatitude(), userlocation.getLongitude()), 14));
                                location = userlocation;
                                searchNearby();
                            }
                            else {
                                GoogleApiClient googleApiClient = new GoogleApiClient.Builder(MapsActivity.this).addApi(LocationServices.API).build();
                                googleApiClient.connect();

                                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(LocationRequest.create());
                                builder.setAlwaysShow(true);


                                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
                                        .setResultCallback(new ResultCallback<LocationSettingsResult>() {
                                            @Override
                                            public void onResult(LocationSettingsResult result) {
                                                try {
                                                    result.getStatus().startResolutionForResult(MapsActivity.this, 0x1);
                                                } catch (IntentSender.SendIntentException e) {
                                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                                }
                                            }
                                        });
                            }

                        }
                    });
        }

    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }


    private void searchNearby(){
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        for(Tab_fragment tab_fragment : charityTypes){
            tab_fragment.setMapValues(mMap,
                    Double.toString(bounds.northeast.latitude),
                    Double.toString(bounds.northeast.longitude),
                    Double.toString(bounds.southwest.latitude),
                    Double.toString(bounds.southwest.longitude));
        }
        charityTypes.get(currentPosition).runSearch();
    }
    @Override
    public void onItemClick(View view, int position, String id, String name) {
        Intent startNewActivity = new Intent(this, DescriptionsActivity.class);
        startNewActivity.putExtra("Id", id);
        startNewActivity.putExtra("Name", name);
        startActivity(startNewActivity);
    }
}
