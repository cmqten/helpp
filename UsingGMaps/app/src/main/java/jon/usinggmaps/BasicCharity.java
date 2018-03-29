package jon.usinggmaps;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class BasicCharity {
    private LatLng latLng;
    private String name;
    private String address;
    private String designationCode;
    private String id;
    private String catagoryCode;
    private String travelTime;
    private String imageName;
    private Bitmap Logo;
    private String sDate;
    private String sTime;
    private String eDate;
    private String eTime;
    private String det;
    private String email;
    private String personName;
    public BasicCharity(String id, String name, String address,
                        String catagoryCode, String designationCode,
                        LatLng latLng, String travelTime){

        this.id = id;
        this.name = name;
        this.address = address ;
        this.catagoryCode = catagoryCode;
        this.designationCode = designationCode;
        this.latLng = latLng;
        this.travelTime = travelTime;

        this.Logo = null;

    }

    public String getId() { return id; }

    public String getName(){
        return name;
    }
    public void setImageName(String imageName){this.imageName =imageName;}
    public String getImageName(){return this.imageName;}

    public LatLng getLatLng(){
        return this.latLng;
    }
    public String getAdd(){return this.address;}
    public String getCat(){return this.catagoryCode;}
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

    public String getsDate(){return this.sDate;}
    public String geteDate(){return this.eDate;}
    public void setsDate(String sdate){this.sDate = sdate;}
    public void seteDate(String edate){this.eDate = edate;}
    public String getsTime(){return this.sTime;}
    public String geteTime(){return this.eTime;}
    public void setsTime(String stime){this.sTime = stime;}
    public void seteTime(String etime){this.eTime = etime;}
    public void setDet(String det){this.det = det;}
    public String getDet(){return this.det;}
    public void setEmail(String email){this.email = email;}
    public String getEmail(){return this.email;}
    public void setpName(String pName){this.personName = pName;}
    public String getpName(){return this.personName;}
}
