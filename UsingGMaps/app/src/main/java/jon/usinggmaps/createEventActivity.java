package jon.usinggmaps;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by kenny on 2018-03-18.
 */

public class createEventActivity extends AppCompatActivity implements View.OnFocusChangeListener{
    private static int RESULT_LOAD_IMAGE = 1;
    EditText txtDateS, txtTimeS, txtDateE, txtTimeE;
    ImageView myImage;
    private int mYear, mMonth, mDay, mHour, mMinute;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        txtDateS=(EditText)findViewById(R.id.EditTextDateS);
        txtTimeS=(EditText)findViewById(R.id.EditTextTimeS);
        txtDateE=(EditText)findViewById(R.id.EditTextDateE);
        txtTimeE=(EditText)findViewById(R.id.EditTextTimeE);
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
}
