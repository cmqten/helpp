package jon.usinggmaps.listeners;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MarkerClickerListener implements GoogleMap.OnMarkerClickListener {
    private  Activity activity;
    public MarkerClickerListener(Activity activity){
        this.activity = activity;
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=" + marker.getTitle()));
        activity.startActivity(intent);

        return false;
    }
}
