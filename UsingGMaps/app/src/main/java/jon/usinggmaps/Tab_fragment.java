package jon.usinggmaps;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class Tab_fragment extends Fragment  {

    private ArrayList<BasicCharity> basicCharities;
    private ListingsAdapter basicCharitiesAdapter;

    private GoogleMap mMap;
    private String neLat;
    private String neLng;
    private String swLat;
    private String swLng;
    private  boolean isEvents;
    private boolean mapSet = false;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private View tab;
    private String queryURL;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tab = inflater.inflate(R.layout.fragment_tab_,container,false);
        return tab;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        basicCharities = new ArrayList<>();
        RecyclerView basicCharitiesView = tab.findViewById(R.id.charitiesView);
        basicCharitiesView.setHasFixedSize(true);


        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        basicCharitiesView.setLayoutManager(lm);
        basicCharitiesAdapter = new ListingsAdapter(getContext(), basicCharities);
        basicCharitiesView.setAdapter(basicCharitiesAdapter);

    }

    public void setQueryURL(String queryURL){
        this.queryURL = queryURL;
    }


    public void setMapValues(GoogleMap mMap, String neLat, String neLng, String swLat, String swLng, boolean isEvents){
        this.mMap = mMap;
        this.neLat = neLat;
        this.neLng = neLng;
        this.swLat = swLat;
        this.swLng = swLng;
        mapSet = true;
        this.isEvents = isEvents;


    }

    public void runSearch(){
        basicCharities.clear();
        basicCharitiesAdapter.notifyDataSetChanged();
        mMap.clear();
        new AsyncRetrieve().execute();
    }

    private class AsyncRetrieve extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        ProgressDialog pdLoading = new ProgressDialog(getContext(),R.style.MyTheme);

        URL url;

        // This method will interact with UI, here display loading message
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        // This method does not interact with UI, You need to pass result to onPostExecute to display
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides

                if(isEvents) {
                    url = new URL("http://shuprio.com/301/events/eventGet.php?uid=333&type=" +queryURL);
                    //url = new URL("http://shuprio.com/301/events/eventGet.php?uid=All&type=" +queryURL+ "&x1="+ neLat + "&y1=" + neLng + "&x2=" + swLat + "&y2="+swLng);
                }
                else {url = new URL("http://shuprio.com/301/getLongLat.php?type=" +queryURL+ "&x1="+ neLat + "&y1=" + neLng + "&x2=" + swLat + "&y2="+swLng);}

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
            pdLoading.dismiss();
            Log.e("sup",result);
            if(!result.isEmpty()){
                String a[] = result.split("~");

                for(String i : a){
                    String s[] = i.split("@@@");
                    LatLng temp;

                    if(isEvents){
                        Log.e("blahblah",result);
                        //id,user_id,name, person name, email, adrs, startd, end d, start t, end t, dets, cat, im, lat, lng
                        Log.e("Lat",s[13]);
                        Log.e("Lat",s[14]);

                        temp = new LatLng(Float.parseFloat(s[13]),Float.parseFloat(s[14]));
                        BasicCharity myBA = new BasicCharity(s[0],s[2],s[5],s[11],"", temp, "N/A");
                        myBA.setImageName(s[12]);
                        myBA.seteDate(s[7]);
                        myBA.setsDate(s[6]);
                        myBA.seteTime(s[9]);
                        myBA.setsTime(s[8]);
                        myBA.setDet(s[10]);
                        myBA.setEmail(s[4]);
                        myBA.setpName(s[3]);
                        basicCharities.add(myBA);
                    }
                    else{
                        temp = new LatLng(Float.parseFloat(s[9]),Float.parseFloat(s[10]));
                        String adr = s[2] + "\t" + s[3]  + ",\t" + s[4] +",\t"+ s[5] + ",\t" + s[6];
                        basicCharities.add(new BasicCharity(s[0],s[1],adr,s[7],s[8], temp, "N/A"));
                    }



                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(temp);
                    markerOptions.title(s[1]);
                    mMap.addMarker(markerOptions);
                }

                basicCharitiesAdapter.notifyDataSetChanged();



            }

        }
    }




}