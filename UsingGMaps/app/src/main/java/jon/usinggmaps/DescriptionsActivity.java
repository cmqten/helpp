package jon.usinggmaps;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.net.URLEncoder;

public class DescriptionsActivity extends AppCompatActivity {


    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    // loging data
    private static final String TAG = "descriptionGetter";

    TextView charityNameBox;
    TextView textPHP;
    ImageView logoImg;
    String logoLink;
    String summary = "";
    String title = "";
    String charity = "Charity name here";
    String error = "";

    // for the image
    Bitmap bmp = null;

    // rio's stuff
    Button donoBtn;
    Button webBtn;
    Button fbBtn;
    Button twBtn;

    String donoURL = "None";
    String webURL = "None";
    String fbURL = "None";
    String twURL = "None";

    public static String id;
    public static String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        charityNameBox = findViewById(R.id.CharityName);
        textPHP = findViewById(R.id.textView);
        logoImg = findViewById(R.id.logoImg);

        // use the id and rio's database to get other data
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

        // set the charity that my function needs to get
        this.charity = name;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void setCharityNameBox(String text){
        charityNameBox.setText(text);
    }

    // rio's button functions
    public void gotoDonoBtn(View view) {goToUrl (donoURL);}
    public void gotoWebBtn(View view) {goToUrl (webURL);}
    public void gotoFbBtn(View view) {goToUrl (fbURL);}
    public void gotoTwBtn(View view) {goToUrl (twURL);}

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

    public void displaySummary(String err){
        if (err == null) {
            textPHP.setText(summary);
            setCharityNameBox(title);

            // set img
            if (bmp != null) {
                logoImg.setImageBitmap(bmp);
            }

        }else{
            textPHP.setText(err);
        }
    }

    private class AsyncRetrieve extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(DescriptionsActivity.this);
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

            // rio's stuff
            StringBuilder result;

            try {
                // Enter URL address where your php file resides
                url = new URL("http://72.139.72.18/getLink.php?id=" + id);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                Log.v(TAG, e.toString());
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
                // TODO Auto-generated catch block
                e1.printStackTrace();
                Log.v(TAG, e1.toString());
                return e1.toString();
            }
            try {
                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                } else {

                    Log.v(TAG, "unsuccessful");
                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.v(TAG, e.toString());
                return e.toString();
            }finally {
                conn.disconnect();
            }


            // my stuff
            String base = "http://6hax.ca:3000/search/";
            //String baseLocal = "http://142.1.2.13:3000/search/";
            String encodedCharity = "";

            try {

                // get the host of given link
//                URI uri = new URI(charity);
//                String domain = uri.getHost();

                encodedCharity = URLEncoder.encode(charity, "UTF-8");
            }catch(Exception e){
                error = e.toString();
                return e.toString();
            }

            String myUrl = base + encodedCharity;

            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;

                response = httpclient.execute(new HttpGet(myUrl));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);

                    String rawData = "";
                    rawData = out.toString();

                    // get json out of summary
                    JSONObject data = new JSONObject(rawData);

                    // get server error value
                    if (!data.getString("error").equals("")) {
                        summary = "Server error, Could not get charity";
                        return ("none");
                    }

                    title = data.getString("title");

                    summary += data.getString("summary");

                    summary += "\n";

                    //summary +="\nLogo Link:\n";
                    logoLink = "https://logo.clearbit.com/" + data.getString("domain");
                    //summary += logoLink;

                    try {
                        // get img from link
                        URL url = new URL(logoLink);
                        bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    }catch(Exception e){
                        Log.v(TAG, "Error setting logo link: " + e.toString());
                        Log.v(TAG, "Logo link: " + logoLink.toString());
                    }

                    out.close();

                } else {
                    //Closes the connection.
                    summary = "Could not connect to server, Could not get charity";
                    Log.v(TAG, "Could not connect to server, Could not get charity");
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }

            }catch (Exception e) {
                // TODO Auto-generated catch block
                summary = "An error occurred parsing, Could not get charity";
                Log.v(TAG, e.toString());
                return e.toString();


            }

            // Pass data to onPostExecute method
            return (result.toString());
        }

        // This method will interact with UI, display result sent from doInBackground method
        @Override
        protected void onPostExecute(String result) {

            pdLoading.dismiss();

            Log.v(TAG, result);
            try {
                String[] links = result.split("!");

                if (result.equals("")) {
                    return;
                }

                webURL = links[0];
                donoURL = links[1];
                fbURL = links[2];
                twURL = links[3];

                //textPHP.setText(links[1]);
                //result.equals("")

                if (webURL.equals("None")) {
                    webBtn.setEnabled(false);
                } else {
                    webBtn.setEnabled(true);
                }

                if (donoURL.equals("None")) {
                    donoBtn.setEnabled(false);
                } else {
                    donoBtn.setEnabled(true);
                }

                if (fbURL.equals("None")) {
                    fbBtn.setEnabled(false);
                } else {
                    fbBtn.setEnabled(true);
                }

                if (twURL.equals("None")) {
                    twBtn.setEnabled(false);
                } else {
                    twBtn.setEnabled(true);
                }

            }catch (Exception e){
                Log.v(TAG, e.toString());
            }


            // call my post execute
            if(error != "") {
                displaySummary(error);
            }else {
                displaySummary(null);
            }

            pdLoading.dismiss();

            // get financial data
            //financialAsync = new FinancialAsync("101676864RR0001", pdLoading);

            summary = "";

        }
    }
}