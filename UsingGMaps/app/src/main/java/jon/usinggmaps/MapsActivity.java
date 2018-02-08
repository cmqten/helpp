package jon.usinggmaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private Double latitude;
    private Double longitude;
    private Geocoder geocoder;
    private Address address;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            geocoder = new Geocoder(this);
            mMap = googleMap;
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);

            //We are getting the last know location of the device, which is often the current location
            //Right now this is just making a single request when the map is ready so we can open up to where the user currently is
            LocationServices.getFusedLocationProviderClient(this).getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            LatLng sydney = new LatLng(latitude, longitude);
                            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,18));
                        }
                        else {
                            Toast.makeText(MapsActivity.this, "Cannot get Current location ", Toast.LENGTH_SHORT).show();
                        }
                    }
            });
        }

        String[] adrs = {
                "41 STONEMEADOW DR\tKANATA\tON\tCA\tK2M2J9",
                "6 DEERGLEN DR\tBRAMPTON\tON\tCA\tL6R1L9\t",
                "305 Thirteen STREET WEST\tCORNWALL\tON\tCA\tK6J3G7",
                "6328 SABLEWOOD PLACE\tOTTAWA\tON\tCA\tK1C7M4",
                "363 WORTHINGTON AVE\tRICHMOND HILL\tON\tCA\tL4E4S3"
        };

        for(String s : adrs){
            try {
                onMapSearch(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }
    public void onMapSearch(String adrs) throws IOException {
        address = geocoder.getFromLocationName(adrs, 1).get(0);
        latLng = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

    }




    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }


}
