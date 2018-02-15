package jon.usinggmaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

import jon.usinggmaps.listeners.CameraMoveListener;
import jon.usinggmaps.listeners.LocationSuccessListener;
import jon.usinggmaps.listeners.PlaceSelectListener;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


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

        //This code launches google maps on the persons phone and gives directions to the pasted in location
//        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                Uri.parse("google.navigation:q=24 Amethyst Dr, Richmond Hill, Ontario"));
//        startActivity(intent);


    }




    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

}
