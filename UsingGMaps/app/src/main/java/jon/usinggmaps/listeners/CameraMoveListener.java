package jon.usinggmaps.listeners;
import android.app.Activity;
import android.location.Address;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class CameraMoveListener implements GoogleMap.OnCameraMoveListener{
    private GoogleMap mMap;
    private MarkerOptions markerOptions;
    private Address address;



    public CameraMoveListener(GoogleMap mMap, Activity activity){
            this.mMap = mMap;
            mMap.setOnMarkerClickListener(new MarkerClickerListener(activity));







    }


    @Override
    public void onCameraMove() {
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;


    }
}
