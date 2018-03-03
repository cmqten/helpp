package jon.usinggmaps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import jon.usinggmaps.listeners.CameraMoveListener;
import jon.usinggmaps.listeners.LocationSuccessListener;
import jon.usinggmaps.listeners.PlaceSelectListener;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback {

    private boolean Community;
    private boolean Education;
    private boolean Health;
    private boolean Religion;
    private boolean Welfare;
    private boolean charitiesSelected;
    private boolean eventsSelected;
    private Intent typeIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        typeIntent = getIntent();
        charitiesSelected = typeIntent.getBooleanExtra("charitiesSelected",true);
        eventsSelected = typeIntent.getBooleanExtra("eventsSelected",true);
        Community = typeIntent.getBooleanExtra("Community",true);
        Education = typeIntent.getBooleanExtra("Education",true);
        Health = typeIntent.getBooleanExtra("Health",true);
        Religion = typeIntent.getBooleanExtra("Religion",true);
        Welfare = typeIntent.getBooleanExtra("Welfare",true);



    }
    @Override
    public void onMapReady(final GoogleMap mMap) {
        CameraMoveListener myCam = new CameraMoveListener(mMap,this);
        mMap.setOnCameraMoveListener(myCam);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectListener(mMap));



        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);

            LocationServices.getFusedLocationProviderClient(this).getLastLocation()
                    .addOnSuccessListener(this,new LocationSuccessListener(this,mMap) );
        }
    }




    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

}
