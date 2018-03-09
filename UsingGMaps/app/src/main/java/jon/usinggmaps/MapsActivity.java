package jon.usinggmaps;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import jon.usinggmaps.listeners.MarkerClickerListener;
import jon.usinggmaps.listeners.PlaceSelectListener;

import static android.content.ContentValues.TAG;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        ListingsAdapter.ItemClickListener, OnMapReadyCallback {

    private boolean Community;
    private boolean Education;
    private boolean Health;
    private boolean Religion;
    private boolean Welfare;
    private boolean charitiesSelected;
    private boolean eventsSelected;
    private Intent typeIntent;

    private ArrayList<BasicCharity> basicCharities;
    private ListingsAdapter basicCharitiesAdapter;
    private RecyclerView basicCharitiesView;


    private GoogleMap mMap;
    private String neLat;
    private String neLng;
    private String swLat;
    private String swLng;
    private LatLngBounds bounds;
    private String s[];


    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

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



        basicCharities = new ArrayList<BasicCharity>();
        basicCharitiesView = findViewById(R.id.charitiesView);
        basicCharitiesView.setHasFixedSize(true);


        LinearLayoutManager lm = new LinearLayoutManager(this);
        basicCharitiesView.setLayoutManager(lm);
        basicCharitiesAdapter = new ListingsAdapter(this, basicCharities);
        basicCharitiesView.setAdapter(basicCharitiesAdapter);



    }

    public void redoSearch(View view){
        view.animate().rotationBy(360f);
        bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        neLat = Double.toString(bounds.northeast.latitude);
        neLng = Double.toString(bounds.northeast.longitude);
        swLat = Double.toString(bounds.southwest.latitude);
        swLng = Double.toString(bounds.southwest.longitude);
        basicCharities.clear();
        mMap.clear();
        new AsyncRetrieve().execute();
    }





    @Override
    public void onMapReady(final GoogleMap Map) {
        mMap = Map;
        mMap.setMinZoomPreference(12);
        mMap.setOnMarkerClickListener(new MarkerClickerListener(this));

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectListener(mMap));
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            LocationServices.getFusedLocationProviderClient(this).getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14));
                                MapsActivity.this.basicCharitiesAdapter.setLocation(location);
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

    @Override
    public void onItemClick(View view, int position, String id, String name) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(basicCharities.get(position).getLatLng(), 18));

        // call my activity
        DescriptionsActivity.id = id;
        DescriptionsActivity.name = name;
        Intent startNewActivity = new Intent(this, DescriptionsActivity.class);
        startActivity(startNewActivity);
    }




    private class AsyncRetrieve extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;

        // This method will interact with UI, here display loading message
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        // This method does not interact with UI, You need to pass result to onPostExecute to display
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                String x1;

                url = new URL("http://72.139.72.18/getLongLat.php?x1="+ neLat + "&y1=" + neLng + "&x2=" + swLat + "&y2="+swLng);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");
                // setDoOutput to true as we recieve data from json file
                conn.setDoOutput(true);
            } catch (IOException e1) {
                e1.printStackTrace();
                return e1.toString();
            }
            try {
                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    // Pass data to onPostExecute method
                    return (result.toString());
                } else {
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }
        }

        // This method will interact with UI, display result sent from doInBackground method
        @Override
        protected void onPostExecute(String result) {
            if(!result.isEmpty()){
                String a[] = result.split("~");
                for(String i : a){
                    s = i.split("@@@");

                    LatLng temp = new LatLng(Float.parseFloat(s[9]),Float.parseFloat(s[10]));
                    String adr = s[2] + "\t" + s[3]  + ",\t" + s[4] +",\t"+ s[5] + ",\t" + s[6];
                    basicCharities.add(new BasicCharity(s[0],s[1],adr,s[7],s[8], temp, "N/A"));

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(temp);
                    markerOptions.title(adr);
                    mMap.addMarker(markerOptions);
                }

                basicCharitiesAdapter.notifyDataSetChanged();

            }

        }
    }




}
