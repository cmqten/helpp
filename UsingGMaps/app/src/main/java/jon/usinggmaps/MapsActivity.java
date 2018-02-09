package jon.usinggmaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;

import static android.R.attr.radius;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener, OnMapReadyCallback {

    private GoogleMap mMap;
    private Geocoder geoCoder;
    private Address address;
    private int radius;
    //We will get rid of this
    private String[] adrs = {
            "41 STONEMEADOW DR\tKANATA\tON\tCA\tK2M2J9",
            "6 DEERGLEN DR\tBRAMPTON\tON\tCA\tL6R1L9\t",
            "305 Thirteen STREET WEST\tCORNWALL\tON\tCA\tK6J3G7",
            "6328 SABLEWOOD PLACE\tOTTAWA\tON\tCA\tK1C7M4",
            "363 WORTHINGTON AVE\tRICHMOND HILL\tON\tCA\tL4E4S3",
            "2682 EGLITON AVENUE           P.O. BOX 44577\tSCARBOROUGH\tON\tCA\tM1K5K2"
    };

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
            geoCoder = new Geocoder(this);
            mMap = googleMap;
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);

            //We are getting the last know location of the device, which is often the current location
            //Right now this is just making a single request when the map is ready so we can open up to where the user currently is
            LocationServices.getFusedLocationProviderClient(this).getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(final Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                           mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),18));
                           final CircleOptions circleop = new CircleOptions();
                           circleop.center(new LatLng(location.getLatitude(), location.getLongitude()));
                           circleop.strokeWidth(0f).fillColor(0x550000FF);
                           circleop.visible(false);
                            final Circle mycirc = mMap.addCircle(circleop);
                            SeekBar mySeek = findViewById(R.id.seekbar);

                            mySeek.setMax(15000);
                            mySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                @Override
                                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                                    mycirc.setRadius(i);
                                    System.out.println(mycirc.getRadius());
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {

                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {

                                }
                            });
                        }
                        else {
                            Toast.makeText(MapsActivity.this, "Cannot get Current location ", Toast.LENGTH_SHORT).show();
                        }
                    }
            });
        }

        for(String s : adrs){
            try {
                onMapSearch(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }
    public void onMapSearch(String adrs) throws IOException {
        address = geoCoder.getFromLocationName(adrs, 1).get(0);
        mMap.addMarker(new MarkerOptions().position(new LatLng(address.getLatitude(), address.getLongitude())).title("Marker"));
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }


}
