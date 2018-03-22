package jon.usinggmaps;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;

import android.graphics.Bitmap;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import static jon.usinggmaps.FinancialAsync.CONNECTION_TIMEOUT;
import static jon.usinggmaps.FinancialAsync.READ_TIMEOUT;
import static junit.framework.Assert.assertEquals;

/**
 * Created by kenny on 2018-03-18.
 */

public class createEventActivity extends AppCompatActivity implements View.OnFocusChangeListener{
    private static int RESULT_LOAD_IMAGE = 1;
    EditText txtDateS, txtTimeS, txtDateE, txtTimeE, txt1, txt2,txt3,txt4,txt9;
    ImageView myImage;
    private int mYear, mMonth, mDay, mHour, mMinute;
    Spinner mySpinner;
    Uri globUrl;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        txtDateS=(EditText)findViewById(R.id.EditTextDateS);
        txtTimeS=(EditText)findViewById(R.id.EditTextTimeS);
        txtDateE=(EditText)findViewById(R.id.EditTextDateE);
        txtTimeE=(EditText)findViewById(R.id.EditTextTimeE);
        txt1 = (EditText)findViewById(R.id.EventName);//Event Name
        txt2 =(EditText)findViewById(R.id.EditTextName); //Person who made the event
        txt3 =(EditText)findViewById(R.id.EditTextEmail);//Their email
        txt4 =(EditText)findViewById(R.id.EditTextLocation);//Location of Event
        txt9 =(EditText)findViewById(R.id.EditTextFeedbackBody);//Details
        mySpinner = (Spinner)findViewById(R.id.eventSpinner);
        txtDateS.setOnFocusChangeListener(this);
        txtTimeS.setOnFocusChangeListener(this);
        txtDateE.setOnFocusChangeListener(this);
        txtTimeE.setOnFocusChangeListener(this);

        myImage = findViewById(R.id.myImage);
        myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);

            }
        });

    }

    public void removePic(View view){
        myImage.setImageDrawable(null);
        myImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_alt_black_24dp));
        Button myBut = (Button)view;
        myBut.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        myImage.setImageDrawable(null);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            globUrl = uri;

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                myImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(myImage.getDrawable() != null){
            Button myBut = findViewById(R.id.removePic);
            myBut.setVisibility(View.VISIBLE);
        }else{
            myImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_alt_black_24dp));

        }
    }
    /*Checks when the focus changes between the date and time fields*/
    @Override
    public void onFocusChange(View view, boolean b) {
        if(b){
            if (view == txtDateS || view == txtDateE) {

                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                final EditText myText = (EditText)view;

                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                myText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
            if (view == txtTimeS || view == txtTimeE) {

                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                final EditText myText = (EditText)view;

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                myText.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        }
    }
    public void createEvent(View v){
        //Log.e("tag",);
        System.out.println("This is txt1 "+txt1.getText().toString());
        if(txt1.getText().toString().matches("")
        ||txt2.getText().toString().matches("")
        ||txt3.getText().toString().matches("")
        ||txt4.getText().toString().matches("")
        ||txtDateS.getText().toString().matches("")
        ||txtDateE.getText().toString().matches("")
        ||txtTimeS.getText().toString().matches("")
        ||txtTimeE.getText().toString().matches("")
        ||txt9.getText().toString().matches("")
        ){
            Toast.makeText(this, "Please enter all the fields for this event! An image is optional",
                    Toast.LENGTH_LONG).show();
        }else{
            if(myImage.getDrawable() != null){

            }
            updateDB myUDB = new updateDB();
            myUDB.execute();
        }


    }
    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }
    /*
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }*/
    private class updateDB extends AsyncTask<String, String, String> {
        //        ProgressDialog pdLoading = new ProgressDialog(FinancialAsync.this);
        HttpURLConnection conn;
        URL url = null;


        // This method does not interact with UI, You need to pass result to onPostExecute to display
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                url = new URL("http://72.139.72.18/301/events/eventInsert.php?user_id=333&event_name="+txt1.getText().toString()
                        +"&person_name="+txt2.getText().toString()+"&email="+txt3.getText().toString()
                        +"&location="+txt4.getText().toString()
                        +"&start_date="+txtDateS.getText().toString()+"&end_date="+txtDateE.getText().toString()
                        +"&start_time="+txtTimeS.getText().toString()+"&end_time="+txtTimeE.getText().toString()
                        +"&details="+txt9.getText().toString()+"&cat="+mySpinner.getSelectedItem().toString()+"&image=None");

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
            updatePic updatePC = new updatePic();
            updatePC.execute(result);

        }
    }



    private class updatePic extends AsyncTask<String, String, String> {
        //        ProgressDialog pdLoading = new ProgressDialog(FinancialAsync.this);



        // This method does not interact with UI, You need to pass result to onPostExecute to display
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                String result = params[0];
                FTPClient ftpClient = new FTPClient();
                ftpClient.connect(InetAddress.getByName("107.180.36.95"));
                ftpClient.login("csc301", "shuprio3");
                ftpClient.makeDirectory(result);
                ftpClient.changeWorkingDirectory(result);
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                BufferedInputStream buffIn = null;
                ///System.out.println("AHHHHHHHHHHHHHHHHHHHH"+globUrl.getPath());
                InputStream inputStream = getContentResolver().openInputStream(globUrl);

                buffIn = new BufferedInputStream(inputStream);
                //Log.e("tag",globUrl.getPath());

                ftpClient.enterLocalPassiveMode();

                String myUri = globUrl.toString();
                String[]myArr = myUri.split("/");
                ftpClient.storeFile(myArr[myArr.length-1], buffIn);
                buffIn.close();
                ftpClient.logout();
                ftpClient.disconnect();
                return "Success";
                //

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
        }

        // This method will interact with UI, display result sent from doInBackground method
        @Override
        protected void onPostExecute(String result) {

            Toast.makeText(createEventActivity.this, "Event Created!",
                    Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
