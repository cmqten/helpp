package jon.usinggmaps.listeners;
import android.app.Activity;
import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import jon.usinggmaps.MapsActivity;

public class CameraMoveListener implements GoogleMap.OnCameraMoveListener{
    private GoogleMap mMap;
    private MarkerOptions markerOptions;
    private Address address;
    private Geocoder geoCoder;
    public String[] adrs = {};

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private ArrayList<MarkerOptions> myList;
    HashMap<String, String> dataHash = new HashMap<>();

    public CameraMoveListener(GoogleMap mMap, Activity activity){
            this.mMap = mMap;
            myList = new ArrayList<MarkerOptions>();
            this.geoCoder = new Geocoder(activity, Locale.getDefault());

            onMapSearch(adrs);


            mMap.setOnMarkerClickListener(new MarkerClickerListener(activity));
    }


    @Override
    public void onCameraMove() {
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        new AsyncRetrieve().execute();

        //Log.d("MSG", bounds.toString());
        for(MarkerOptions s : myList){
            if( bounds.contains(s.getPosition())){
                s.visible(true);
            }
        }
    }

    public void onMapSearch(String[] adrs) {
       try{
        for(String s :adrs){

            markerOptions = new MarkerOptions();
            address = geoCoder.getFromLocationName(s, 1).get(0);
            markerOptions.position(new LatLng(address.getLatitude(), address.getLongitude()));
            markerOptions.title(s);
            mMap.addMarker(markerOptions);
            markerOptions.visible(false);
            myList.add(markerOptions);
        }
       }catch(IOException e){
       }
    }


    private class AsyncRetrieve extends AsyncTask<String, String, String> {
        //ProgressDialog pdLoading = new ProgressDialog(CharityPage.this);
        HttpURLConnection conn;
        URL url = null;

        // This method will interact with UI, here display loading message
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pdLoading.setMessage("\tLoading...");
            //pdLoading.setCancelable(false);
            //pdLoading.show();
        }

        // This method does not interact with UI, You need to pass result to onPostExecute to display
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL("http://72.139.72.18/mysql.php?x1=43.565696&y1=-79.744685&x2=43.604175&y2=-79.603912");

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

            //pdLoading.dismiss();
            adrs = result.toString().split("!");
            //String[] test = {"2630 INLAKE COURT\tMISSISSAUGA\tON\tCA\tL5N2G2"};
            //onMapSearch(adrs);

            Log.d("MSG", adrs[0]);


        }
    }
}
