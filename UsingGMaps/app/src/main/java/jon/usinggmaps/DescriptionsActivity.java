package jon.usinggmaps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class DescriptionsActivity extends AppCompatActivity implements RewardedVideoAdListener {


    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    // loging data
    private static final String TAG = "descriptionGetter";

    TextView charityNameBox;
    TextView textPHP;
    ImageView logoImg;
    String logoLink;
    String summary;
    String title;
    String charity;
    String error;

    // for the image
    Bitmap bmp = null;

    // rio's stuff
    FloatingActionButton donoBtn;
    FloatingActionButton webBtn;
    FloatingActionButton fbBtn;
    FloatingActionButton twBtn;

    String donoURL;
    String webURL;
    String fbURL;
    String twURL ;

    public static String id;
    public static String name;

    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptions);


        charityNameBox = findViewById(R.id.CharityName);
        textPHP = findViewById(R.id.textView);
        logoImg = findViewById(R.id.logoImg);
        webBtn = findViewById(R.id.webbutton);
        donoBtn = findViewById(R.id.donobutton);
        fbBtn = findViewById(R.id.fbbutton);
        twBtn = findViewById(R.id.twbutton);

        charityNameBox.setText(name);

        //Make call to AsyncRetrieve
        new AsyncRetrieve().execute();

        // set the charity that my function needs to get
        this.charity = name;

        MobileAds.initialize(this,
                "ca-app-pub-2650389847656790~2722040847");

        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);


    }

    public void onWatchAds(View view){
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
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

            // set img
            if (bmp != null) {
                logoImg.setImageBitmap(bmp);
            }

        }else{
            textPHP.setText(err);
        }
    }



    @Override
    public void onRewarded(RewardItem reward) {
        Toast.makeText(this, "onRewarded! currency: " + reward.getType() + "  amount: " +
                reward.getAmount(), Toast.LENGTH_SHORT).show();
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdClosed() {
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
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
                url = new URL("http://72.139.72.18/301/getLink.php?id=" + id);

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
            String encodedCharity;

            try {
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

                    summary = data.getString("summary");

                    Log.d("Hi", data.getString("domain"));
                    logoLink = "https://logo.clearbit.com/" + data.getString("domain")+"?size=500";

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
            try {
                if(!result.isEmpty()){
                    String[] links = result.split("!");
                    if (!links[0].equals("None")) {

                        webURL = links[0];
                        webBtn.setVisibility(View.VISIBLE);
                    }
                    if (!links[1].equals("None")) {
                        donoURL = links[1];
                        donoBtn.setVisibility(View.VISIBLE);
                    }
                    if (!links[2].equals("None")) {
                        fbURL = links[2];
                        fbBtn.setVisibility(View.VISIBLE);
                    }
                    if (!links[3].equals("None")) {
                        twURL = links[3];
                        twBtn.setVisibility(View.VISIBLE);
                    }
                }
            }catch (Exception e){
                Log.v(TAG, e.toString());
            }

            // call my post execute
            displaySummary(error);
            pdLoading.dismiss();

            // get financial data
            //financialAsync = new FinancialAsync("101676864RR0001", pdLoading);


        }
    }
}