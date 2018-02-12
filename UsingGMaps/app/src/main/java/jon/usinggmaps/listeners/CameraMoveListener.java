package jon.usinggmaps.listeners;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class CameraMoveListener implements GoogleMap.OnCameraMoveListener{
    private GoogleMap mMap;
    private MarkerOptions markerOptions;


    public CameraMoveListener(GoogleMap mMap){
        this.mMap = mMap;
    }
    @Override
    public void onCameraMove() {
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        //query and return a datastructure of all the charities that fall within thses latlngBounds

        //bounds.contains(marker.getPosition())
        //onMapSearch();

    }

    public void onMapSearch(ArrayList<String> adrs) {
//        for(String s :adrs){
//
//            markerOptions = new MarkerOptions();
//            markerOptions.position(new LatLng(address.getLatitude(), address.getLongitude()));
//            markerOptions.title();
//            mMap.addMarker(markerOptions);
//        }
    }
}
