package jon.usinggmaps.listeners;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

public class PlaceSelectListener implements PlaceSelectionListener {
    private GoogleMap mMap;
    public PlaceSelectListener(GoogleMap mMap) {
        this.mMap = mMap;
    }

    @Override
    public void onPlaceSelected(Place place) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 18));
    }

    @Override
    public void onError(Status status) {

    }
}
