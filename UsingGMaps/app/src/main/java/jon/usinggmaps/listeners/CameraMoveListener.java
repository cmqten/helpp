package jon.usinggmaps.listeners;
import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class CameraMoveListener implements GoogleMap.OnCameraMoveListener{
    private GoogleMap mMap;
    private MarkerOptions markerOptions;
    private Address address;
    private Geocoder geoCoder;
    private String[] adrs = {
            "5100 Erin Mills Pkwy\tMississauga\tON\tCA\tL5M4Z5",
            "3636 Hawkestone Rd\tMississauga\tON\tCA\tL5C2V2",
            "41 STONEMEADOW DR\tKANATA\tON\tCA\tK2M2J9", "41 STONEMEADOW DR\tKANATA\tON\tCA\tK2M2J9",
            "6 DEERGLEN DR\tBRAMPTON\tON\tCA\tL6R1L9\t", "6 DEERGLEN DR\tBRAMPTON\tON\tCA\tL6R1L9\t",
            "305 Thirteen STREET WEST\tCORNWALL\tON\tCA\tK6J3G7", "305 Thirteen STREET WEST\tCORNWALL\tON\tCA\tK6J3G7",
            "6328 SABLEWOOD PLACE\tOTTAWA\tON\tCA\tK1C7M4", "6328 SABLEWOOD PLACE\tOTTAWA\tON\tCA\tK1C7M4",
            "363 WORTHINGTON AVEte\tRICHMOND HILL\tON\tCA\tL4E4S3", "363 WORTHINGTON AVE\tRICHMOND HILL\tON\tCA\tL4E4S3"
    };
    private ArrayList<MarkerOptions> myList;


    public CameraMoveListener(GoogleMap mMap, Activity activity){
            this.mMap = mMap;
            myList = new ArrayList<MarkerOptions>();
            this.geoCoder = new Geocoder(activity, Locale.getDefault());
            onMapSearch(adrs);


            mMap.setOnMarkerClickListener(new MarkerClickerListener(activity));







    }


    @Override
    public void onCameraMove() {
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        for(MarkerOptions s : myList){
            if( bounds.contains(s.getPosition())){
                s.visible(true);
            }
        }
    }

    public void onMapSearch(String[] adrs) {
       try{
        for(String s :adrs){

            markerOptions = new MarkerOptions();
            address = geoCoder.getFromLocationName(s, 1).get(0);
            markerOptions.position(new LatLng(address.getLatitude(), address.getLongitude()));
            markerOptions.title(s);
            mMap.addMarker(markerOptions);
            markerOptions.visible(false);
            myList.add(markerOptions);
        }
       }catch(IOException e){
       }
    }
}
