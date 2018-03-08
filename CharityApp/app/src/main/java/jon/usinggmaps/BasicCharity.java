package jon.usinggmaps;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class BasicCharity {
    private LatLng latLng;
    private String name;
    private String address;
    private String designationCode;
    private String id;
    private int catagoryCode;
    private String travelTime;
    private Bitmap Logo;


    public BasicCharity(String id, String name, String address,
                        String catagoryCode, String designationCode,
                        LatLng latLng, String travelTime){

        this.id = id;
        this.name = name;
        this.address = address ;
        this.catagoryCode = Integer.parseInt(catagoryCode);
        this.designationCode = designationCode;
        this.latLng = latLng;
        this.travelTime = travelTime;
        this.Logo = null;

    }

    public String getId() { return id; }

    public String getName(){
        return name;
    }

    public LatLng getLatLng(){
        return this.latLng;
    }

    public String getTravelTime(){
        return this.travelTime;
    }
    public void setTravelTime(String travelTime){this.travelTime = travelTime;}
    public Bitmap getLogo(){
        return this.Logo;
    }
    public void setLogo(Bitmap Logo){
        this.Logo = Logo;
    }


}
