package jon.locationapis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

public class MainActivity extends AppCompatActivity {

    TextView placeNameText;
    TextView placeAddressText;
    WebView attributionText;
    Button getPlaceButton;
    private final static int PLACE_PICKER_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        placeNameText = (TextView) findViewById(R.id.tvPlaceName);
        placeAddressText = (TextView) findViewById(R.id.tvPlaceAddress);
        getPlaceButton = (Button) findViewById(R.id.btGetPlace);

        getPlaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    Intent intent = builder.build(MainActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request it is that we're responding to
        if (requestCode == PLACE_PICKER_REQUEST){
            // Make sure the request was successful
            if (resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(MainActivity.this, data);
                placeNameText.setText(place.getName());
                placeAddressText.setText(place.getAddress());

            }
        }
    }
}