package jon.usinggmaps;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


/**
 * Created by umoluedm on 06/03/18.
 * This function gets financial data.
 */

public class FinancialAsync extends Observable {
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    // loging data
    private static final String TAG = "descriptionGetter";

    // global variables
    String charityID;
    HashMap<String, String> dataHash;
    ProgressDialog pdLoading;
    Observer observer;
    String date;
    public FinancialAsync(String charityID, ProgressDialog pdLoading, Observer observer, String date){

        // global variables
        //this.charityID = "101676864RR0001";
        this.charityID = charityID;
        this.dataHash = new HashMap<>();

        // add observer
        this.observer = observer;
        this.date=date;
        // call on an update
        //Log.d("MSG", dataHash.get("ongoingPrograms"));

        this.pdLoading = pdLoading;

        // call on create
        new AsyncRetrieve().execute();

    }

    private class AsyncRetrieve extends AsyncTask<String, String, String> {
//        ProgressDialog pdLoading = new ProgressDialog(FinancialAsync.this);
        HttpURLConnection conn;
        URL url = null;

        // This method will interact with UI, here display loading message
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("\tGetting data...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        // This method does not interact with UI, You need to pass result to onPostExecute to display
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides


                if(date==null){
                    url = new URL("http://72.139.72.18/301/getData.php/?id=" + charityID);
                }else{url = new URL("http://72.139.72.18/301/getData.php/?id=" + charityID+ "&date=" + date);}
                //

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

            try {
                if (!result.isEmpty()) {
                    String[] data = result.toString().split("!");
                    for (String s : data) {
                        String[] split = s.split(":");
                        //dataHash.clear();
                        dataHash.put(split[0], split[1]);
                    }
                    notifyObservers();
                }
            } catch (Exception e) {
                Log.v(TAG, e.toString());
            }
        }
    }

    @Override
    public void notifyObservers() {
        super.notifyObservers();
        observer.update(this, dataHash);
    }
}
