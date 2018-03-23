package jon.usinggmaps;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class DescriptionsActivity extends AppCompatActivity implements RewardedVideoAdListener, Observer {


    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    // loging data
    private static final String TAG = "descriptionGetter";

    // set a flag for if we are using events as data
    private boolean usingActivities = true;

    TextView charityNameBox;
    TextView textPHP;
    ImageView logoImg;
    String logoLink;
    String summary;
    String gSummary;
    String charity;
    String error;
    ArrayList<String> basicYears;
    ProgressDialog pdLoading;
    // for the image
    Bitmap bmp = null;
    //kenny's stuff
    private int year;
    HashMap<String, String> myMap;
    Context activity;
    ArrayList<String> dates;
    Observer obs;
    // rio's stuff
    FloatingActionButton donoBtn;
    FloatingActionButton webBtn;
    FloatingActionButton fbBtn;
    FloatingActionButton twBtn;
    FloatingActionButton YTBtn;
    FloatingActionButton InstaBtn;

    String donoURL;
    String webURL;
    String fbURL;
    String twURL;
    String YTURL ;
    String InstaURL ;
    private int category;
    private String id;
    private String name;

    private RewardedVideoAd mRewardedVideoAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descriptions);


        getViews();
        Intent fromTypeActivity = getIntent();
        name =  fromTypeActivity.getStringExtra("Name");
        id =  fromTypeActivity.getStringExtra("Id");
        charityNameBox.setText(name);
        //Make call to AsyncRetrieve
        new AsyncRetrieve().execute();
        MobileAds.initialize(this,
                "ca-app-pub-2650389847656790~2722040847");
        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);

        setUpFinancial();

    }

    private void setUpFinancial(){
         /*----------------------On Create for Finance Stuff-----------------*/
        activity = this;
        dates = new ArrayList<>();
        year = 0;
        obs = this;
        category=-1;
        basicYears = new ArrayList<>();
        ProgressDialog myDiag = new ProgressDialog(this);
        FinancialAsync myAsync = new FinancialAsync(id,myDiag,this,null);
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(year!="2018"){new FinancialAsync("101676864RR0001",new ProgressDialog(activity),obs,dates.get(0));}
                category = 0;
                setColorOfButt(R.id.button1);
                setColorOfButtYear(-1);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //if(year!="2017"){}
                category = 1;
                setColorOfButt(R.id.button2);
                setColorOfButtYear(-1);
            }
        });

        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if(year!="2016"){new FinancialAsync("101676864RR0001",new ProgressDialog(activity),obs,dates.get(2));}
                category = 2;
                setColorOfButt(R.id.button3);
                setColorOfButtYear(-1);
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if(year!="2015"){new FinancialAsync("101676864RR0001",new ProgressDialog(activity),obs,dates.get(3));}
                category = 3;
                setColorOfButt(R.id.button4);
                setColorOfButtYear(-1);

            }
        });

        findViewById(R.id.eight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setColorOfButtYear(R.id.eight);
                if(category==-1){category=0;}
                year = 0;
                new FinancialAsync(id,new ProgressDialog(activity),obs,dates.get(0));


            }
        });
        findViewById(R.id.seven).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setColorOfButtYear(R.id.seven);
                if(category==-1){category=0;}
                year = 1;
                new FinancialAsync(id,new ProgressDialog(activity),obs,dates.get(1));


            }
        });
        findViewById(R.id.six).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setColorOfButtYear(R.id.six);
                if(category==-1){category=0;}
                year = 2;
                new FinancialAsync(id,new ProgressDialog(activity),obs,dates.get(2));


            }
        });
        findViewById(R.id.five).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setColorOfButtYear(R.id.five);
                if(category==-1){category=0;}
                year = 3;
                new FinancialAsync(id,new ProgressDialog(activity),obs,dates.get(3));


            }
        });

        findViewById(R.id.four).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setColorOfButtYear(R.id.four);
                if(category==-1){category=0;}
                year = 4;
                new FinancialAsync(id,new ProgressDialog(activity),obs,dates.get(4));


            }
        });

        // call financial data, so we can populate descriptions
        new FinancialAsync(id,new ProgressDialog(activity),obs,null);

    }

    private void getViews(){
        charityNameBox = findViewById(R.id.CharityName);
        textPHP = findViewById(R.id.textView);
        logoImg = findViewById(R.id.logoImg);
        webBtn = findViewById(R.id.webbutton);
        donoBtn = findViewById(R.id.donobutton);
        fbBtn = findViewById(R.id.fbbutton);
        twBtn = findViewById(R.id.twbutton);
        InstaBtn = findViewById(R.id.insta);
        YTBtn = findViewById(R.id.youtube);

    }

    public void setColorOfButt(int id){
        int[] myIds = {R.id.button1, R.id.button2,R.id.button3,R.id.button4};
        for(int ids : myIds){
            Button myBut1 = (Button)findViewById(ids);
            if(ids==id){
                myBut1.setTextColor(getResources().getColor(R.color.Pink));
            }else{
                myBut1.setTextColor(getResources().getColor(R.color.Shadow));
            }
        }

    }
    public void setColorOfButtYear(int id){
        int[] myIds = {R.id.four, R.id.five,R.id.six,R.id.seven,R.id.eight};
        for(int ids : myIds){
            Button myBut1 = (Button)findViewById(ids);
            if(ids==id){
                myBut1.setTextColor(getResources().getColor(R.color.Orange));
            }else{
                myBut1.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }

    }

    public void onWatchAds(View view){
        pdLoading = new ProgressDialog(DescriptionsActivity.this,R.style.MyTheme);
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }


    public void onBack(View view){
        onBackPressed();
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

    public void gotoDonoBtn(View view) {goToUrl (donoURL);}

    public void gotoWebBtn(View view) {goToUrl (webURL);}

    public void gotoFbBtn(View view) {goToUrl (fbURL);}

    public void gotoTwBtn(View view) {goToUrl (twURL);}

    public void gotoInstaBtn(View view) {goToUrl (InstaURL);}

    public void gotoYTBtn(View view) {goToUrl (YTURL);}

    public void getDirections(View view){

        this.startActivity(new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=" + name)));
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
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
             pdLoading.dismiss();
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
    @Override
    public void update(Observable observable, Object o) {
        myMap = (HashMap<String,String>)o;
        String mydates = myMap.get("financialDates");
        mydates = mydates.substring(1,mydates.length()-1);
        String[] dates = mydates.split(", ");
        for(int i = 0 ; i< dates.length;i++){
            this.dates.add(dates[i].substring(1,dates[i].length()-1));
        }
        if(basicYears.isEmpty()){
            for(String s : this.dates){
                basicYears.add(s.substring(0,4));
            }
            int[] myIds = {R.id.eight, R.id.seven,R.id.six,R.id.five,R.id.four};
            int i = 0;
            for(int ids : myIds){
                Button myBut1 = (Button)findViewById(ids);
                myBut1.setText(basicYears.get(i));
                i++;
            }
        }


        switch(category){
            case 0:
                AlertDialog ad = new AlertDialog.Builder(DescriptionsActivity.this).setMessage("Ongoing Programs: "+myMap.get("ongoingPrograms")).create();
                ad.show();
                Display display =((WindowManager)getSystemService(DescriptionsActivity.this.WINDOW_SERVICE)).getDefaultDisplay();
                int width = display.getWidth();
                int height=display.getHeight();
                ad.getWindow().setLayout(width*9/10,height/2);
                break;
            case 1:
                String[] portions= {"revenue_total","revenue_government_funding",
                        "revenue_non_receipted_donations","revenue_receipted_donations"
                        ,"revenue_other"};
                String [] labels = {"Government Funding","Non Receipted Donations",
                        "Receipted Donations","Other","Revenues for year "};
                DrawPie(portions,labels);
                break;
            case 2:
                String [] portions2 = {"expenses_total","expenses_management_and_admin",
                        "expenses_fundraising","expenses_other","expenses_charitable_program"};
                String [] labels2 = {"Management and Admin","Fundraising","Other","Charitable Programs","Expenses for year "};
                DrawPie(portions2,labels2);
                break;
        }

        // update summary with on going data if needed
        if(usingActivities){
            if(!myMap.containsKey("ongoingPrograms")){
                textPHP.setText(gSummary);
            }

            if(!myMap.get("ongoingPrograms").isEmpty()) {
                textPHP.setText(myMap.get("ongoingPrograms"));
            }else{
                textPHP.setText(gSummary);
            }
        }
    }
    public void DrawPie(String[] portions, String[] labels){
        /*Inflates the layout so we can use the PieChart*/
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = inflater.inflate(R.layout.pie_chart,null);
        PieChart myPie = (PieChart)vi.findViewById(R.id.myPie);
        Display display =((WindowManager)getSystemService(DescriptionsActivity.this.WINDOW_SERVICE)).getDefaultDisplay();
        int width = display.getWidth();
        int height=display.getHeight();
        /*------------------------------------------------------------*/
        /*Generates the portions of the pie chart and sets the data*/
        List<PieEntry> entries = new ArrayList<>();
        Float revTot = Float.parseFloat(myMap.get(portions[0]));
        System.out.println(revTot);
        Float revGvnmtFnd = Float.parseFloat(myMap.get(portions[1]));
        Float revNonRecDon = Float.parseFloat(myMap.get(portions[2]));
        Float revRecDon = Float.parseFloat(myMap.get(portions[3]));
        Float revOther = Float.parseFloat(myMap.get(portions[4]));
        //ArrayList<String> newLabels = calculateWidth(labels);
        entries.add(new PieEntry(revGvnmtFnd / revTot,labels[0] + "     |   $"+revGvnmtFnd));
        entries.add(new PieEntry(revNonRecDon / revTot,labels[1] + "     |   $"+revNonRecDon));
        entries.add(new PieEntry(revRecDon / revTot,labels[2] + "     |   $"+revRecDon));
        entries.add(new PieEntry(revOther / revTot, labels[3] + "     |   $"+revOther));
        PieDataSet set = new PieDataSet(entries, "");
        int mycolors[] = {Color.parseColor("#68E861"),Color.parseColor("#61ABE8"),Color.parseColor("#E261E8")
                ,Color.parseColor("#E89F61")};
        set.setColors(mycolors);
        set.setDrawValues(false);
        set.setSliceSpace(5f);
        PieData data = new PieData(set);
        /*-------------------------------------------------------------*/
        /*Sets up graph options---------------------------------------*/
        myPie.setLayoutParams(new LinearLayout.LayoutParams(3*width/4,600));
        myPie.setData(data);
        //myPie.setExtraBottomOffset(70f);
        myPie.setDrawEntryLabels(false);

        Description description = new Description();
        description.setText(labels[4]+ basicYears.get(year));
        myPie.setDescription(description);
        /*----------------------------------------------------------------*/
        /*Sets up the legend------------------------------------*/
        Legend legend = myPie.getLegend();
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setWordWrapEnabled(true);
        legend.setDrawInside(false);
        legend.getCalculatedLineSizes();
        //legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        //legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setXEntrySpace((float)width/2);
        /*-----------------------------------------------------------*/
        //Updates the graph
        myPie.invalidate();
        /*Puts the graph into an alert dialog---------------------------*/
        AlertDialog ad = new AlertDialog.Builder(DescriptionsActivity.this).setView(vi).create();
        ad.show();
        ad.getWindow().setLayout(6*width/7,3*height/5);
        /*-------------------------------------------------------------*/
    }
    public ArrayList<String> calculateWidth(String[] portions){
        int max = 0;
        ArrayList<String> myNewLabels = new ArrayList<>();
        for(String s : portions){
            if(s.length()>max){
                max = s.length();
            }
        }
        for(String s: portions){
            StringBuilder sb = new StringBuilder();
            sb.append(s);
            while(s.length()<max){
                sb.append(" ");
            }
            myNewLabels.add(sb.toString());
        }
        return myNewLabels;
    }
    private class AsyncRetrieve extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(DescriptionsActivity.this,R.style.MyTheme);
        HttpURLConnection conn;
        URL url = null;

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
            String base = "http://72.139.72.18:4000/getData/";
            String encodedCharity;

            try {
                encodedCharity = URLEncoder.encode(charity, "UTF-8");
            }catch(Exception e){
                error = e.toString();
                Log.v(TAG, error);
                return e.toString();
            }

            // add id and charity name
            String myUrl = base + id + "/" + encodedCharity;

            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;

                response = httpclient.execute(new HttpGet(myUrl));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);

                    String rawData = out.toString();

                    // get json out of summary
                    JSONObject data = new JSONObject(rawData);

                    // get server error value
                    if (!data.getString("error").equals("")) {
                        summary = "Server error, Could not get charity";
                        return ("none");
                    }

                    // check if a better summary was gotten
                    if(data.getString("flag").equals("set")){

                        // if it does
                        usingActivities = false;

                        summary = data.getString("SummaryBetter").
                                replace("\n\n", "$*#$").
                                replace("\n","").
                                replace("$*#$", "\n")
                                .replaceAll(" +", " ");
                    }else{

                        // keep the bad google summary for now
                        gSummary = data.getString("Summary");

//                        // if you can't then post the regular google description
//                        String ongoing = myMap.get("ongoingPrograms");
//                        if (!ongoing.isEmpty()){
//                            summary = ongoing;
//                        }
//                        // if you can't then post the regular google description
//                        else{
//                            summary = data.getString("Summary");
//                        }
                    }

                    //logoLink = "https://logo.clearbit.com/" + data.getString("domain")+"?size=500";

                    // check if its a facebook link, before we add size param
                    logoLink = data.getString("Image");
                    if (!logoLink.toLowerCase().contains("scontent")){
                        logoLink = logoLink + "?size=500";
                    }

                    // don't show chimp links
                    if (logoLink.contains("chimp.net")){
                        logoLink = null;
                    }

                    try {
                        // get img from link
                        bmp = BitmapFactory.decodeStream(new URL(logoLink).openConnection().getInputStream());
                    }catch(Exception e){
                        Log.v(TAG, "Error setting logo link: " + e.toString());
                        Log.v(TAG, "Logo link: " + logoLink);
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
                    Log.d("Hi", Arrays.toString(links));
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
                    if (!links[4].equals("None")) {
                        YTURL = links[3];
                        twBtn.setVisibility(View.VISIBLE);
                    }
                    if (!links[5].equals("None")) {
                        InstaURL = links[3];
                        twBtn.setVisibility(View.VISIBLE);
                    }
                }
            }catch (Exception e){
                Log.v(TAG, e.toString());
            }
            textPHP.setText(summary);
            logoImg.setImageBitmap(bmp);
            pdLoading.dismiss();

        }
    }

}