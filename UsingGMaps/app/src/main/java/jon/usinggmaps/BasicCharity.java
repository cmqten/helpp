package jon.usinggmaps;

import com.google.android.gms.maps.model.LatLng;

public class BasicCharity {
    private LatLng latLng;
    private String name;
    private String address;
    private String designationCode;
    private String id;
    private int catagoryCode;
    private String travelTime;


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

    }
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


}
