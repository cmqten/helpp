package csc301.donobutton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CharityPage extends AppCompatActivity {

    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    Button donoBtn; Button webBtn; Button fbBtn; Button twBtn;
    String donoURL = "None"; String webURL = "None"; String fbURL = "None"; String twURL = "None";

    TextView regIdText;
    public static String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity);

        regIdText = (TextView) findViewById(R.id.idtext);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(id + "/name");

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("MSG", "Value is: " + value);
                regIdText.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("FAIL", "Failed to read value.", error.toException());
            }
        });



        webBtn = (Button) findViewById(R.id.webbutton);
        webBtn.setEnabled(false);

        donoBtn = (Button) findViewById(R.id.donobutton);
        donoBtn.setEnabled(false);

        fbBtn = (Button) findViewById(R.id.fbbutton);
        fbBtn.setEnabled(false);

        twBtn = (Button) findViewById(R.id.twbutton);
        twBtn.setEnabled(false);

        //Make call to AsyncRetrieve
        new AsyncRetrieve().execute();
    }


    public void gotoDonoBtn(View view) {goToUrl (donoURL);}
    public void gotoWebBtn(View view) {goToUrl (webURL);}
    public void gotoFbBtn(View view) {goToUrl (fbURL);}
    public void gotoTwBtn(View view) {goToUrl (twURL);}

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    private class AsyncRetrieve extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(CharityPage.this);
        HttpURLConnection conn;
        URL url = null;

        // This method will interact with UI, here display loading message
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        // This method does not interact with UI, You need to pass result to onPostExecute to display
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL("http://72.139.72.18/getLink.php?id=" + id);

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

            String[] links = result.toString().split("!");

            webURL = links[0];
            donoURL = links[1];
            fbURL = links[2];
            twURL = links[3];

            //textPHP.setText(links[1]);
            //result.equals("")

            if(webURL.equals("None")) {
                webBtn.setEnabled(false);
            }else{
                webBtn.setEnabled(true);
            }

            if(donoURL.equals("None")) {
                donoBtn.setEnabled(false);
            }else {
                donoBtn.setEnabled(true);
            }

            if(fbURL.equals("None")) {
                fbBtn.setEnabled(false);
            }else {
                fbBtn.setEnabled(true);
            }

            if(twURL.equals("None")) {
                twBtn.setEnabled(false);
            }else {
                twBtn.setEnabled(true);
            }
        }
    }
}