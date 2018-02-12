package jon.usinggmaps.listeners;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.IntentSender;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import jon.usinggmaps.MapsActivity;

import static android.content.ContentValues.TAG;

public class LocationSuccessListener implements OnSuccessListener<Location> {

    private GoogleMap mMap;
    private Activity mapsActivity;
    private AlertDialog dialog ;

    public LocationSuccessListener(MapsActivity mapsActivity, GoogleMap mMap){
        this.mMap = mMap;
        this.mapsActivity = mapsActivity;
    }

    @Override
    public void onSuccess(Location location) {
            if (location != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(location.getLatitude(), location.getLongitude()), 18));
            }
            else {
                GoogleApiClient googleApiClient = new GoogleApiClient.Builder(mapsActivity).addApi(LocationServices.API).build();
                googleApiClient.connect();

                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(LocationRequest.create());
                builder.setAlwaysShow(true);

                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
                        .setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(LocationSettingsResult result) {
                        try {
                            result.getStatus().startResolutionForResult(mapsActivity, 0x1);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                    }
                });

            }
    }



}
