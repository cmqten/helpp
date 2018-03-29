package jon.usinggmaps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

/**
 * Created by Jon on 2018-03-28.
 */

public class EventInfo extends AppCompatActivity{
    TextView txtDateS, txtTimeS, txtDateE, txtTimeE, txt1, txt2,txt3,txt4,txt9,mySpinner;
    ImageView myImage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_event);
        Intent intent = getIntent();
        txtDateS=(TextView)findViewById(R.id.EditTextDateS);
        txtTimeS=(TextView)findViewById(R.id.EditTextTimeS);
        txtDateE=(TextView)findViewById(R.id.EditTextDateE);
        txtTimeE=(TextView)findViewById(R.id.EditTextTimeE);
        txt1 = (TextView)findViewById(R.id.EventName);//Event Name
        txt2 =(TextView)findViewById(R.id.EditTextName); //Person who made the event
        txt3 =(TextView)findViewById(R.id.EditTextEmail);//Their email
        txt4 =(TextView)findViewById(R.id.EditTextLocation);//Location of Event
        txt9 =(TextView)findViewById(R.id.EditTextFeedbackBody);//Details
        mySpinner = (TextView) findViewById(R.id.eventSpinner);
        myImage = findViewById(R.id.myImage);
        txtDateS.setText(intent.getStringExtra("sDate"));
        txtTimeS.setText(intent.getStringExtra("sTime"));
        txtDateE.setText(intent.getStringExtra("eDate"));
        txtTimeE.setText(intent.getStringExtra("eTime"));
        txt1.setText(intent.getStringExtra("Name"));
        txt2.setText(intent.getStringExtra("pName"));
        txt3.setText(intent.getStringExtra("Email"));
        txt4.setText(intent.getStringExtra("add"));
        txt9.setText(intent.getStringExtra("det"));
        mySpinner.setText(intent.getStringExtra("Cat"));
        txtDateS.setEnabled(false);
        txtTimeS.setEnabled(false);
        txtDateE.setEnabled(false);
        txtTimeE.setEnabled(false);
        txt1.setEnabled(false);
        txt2.setEnabled(false);
        txt3.setEnabled(false);
        txt4.setEnabled(false);
        txt9.setEnabled(false);
        mySpinner.setEnabled(false);
        String arr[] = {intent.getStringExtra("id"),intent.getStringExtra("imageName")};
        new updatePic().execute(arr);
    }
    public void onBack(View view){
        onBackPressed();
    }
    private class updatePic extends AsyncTask<String, String, String> {
        //        ProgressDialog pdLoading = new ProgressDialog(FinancialAsync.this);



        // This method does not interact with UI, You need to pass result to onPostExecute to display
        @Override
        protected String doInBackground(String... params) {
            try {
                // Enter URL address where your php file resides
                String id = params[0];
                String image = params[1];
                FTPClient ftpClient = new FTPClient();
                ftpClient.connect(InetAddress.getByName("107.180.36.95"));
                ftpClient.login("csc301", "shuprio3");

                ftpClient.changeWorkingDirectory(id);
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                ftpClient.enterLocalPassiveMode();
                OutputStream oS = null;
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath()+"/helpp");
                dir.mkdirs();
                File file = new File(dir,"image.jpeg");
                boolean success = false;
                try{
                    oS=new BufferedOutputStream(new FileOutputStream(file));
                    success = ftpClient.retrieveFile(image,oS);
                    return sdCard.getAbsolutePath()+"/helpp/image.jpeg";
                }finally{
                    if(oS!= null){
                        oS.close();
                    }
                    if(ftpClient!=null){ftpClient.logout();ftpClient.disconnect();}
                }
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
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(result,bmOptions);

            //bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
            //imageView.setImageBitmap(bitmap);
            //myImage.setImageBitmap(bitmap);
        }
    }

}
