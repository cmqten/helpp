package jon.usinggmaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import jon.usinggmaps.listeners.CameraMoveListener;
import jon.usinggmaps.listeners.LocationSuccessListener;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback {
    private SupportMapFragment mapFragment;
    private LocationSuccessListener locationSuccessListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);

        this.mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        this.mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(final GoogleMap mMap) {
        mMap.setOnCameraMoveListener(new CameraMoveListener(mMap));
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);

            locationSuccessListener = new LocationSuccessListener(this,mMap);
            LocationServices.getFusedLocationProviderClient(this).getLastLocation()
                    .addOnSuccessListener(this,locationSuccessListener );
        }
    }
    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

}
