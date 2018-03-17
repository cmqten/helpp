package jon.usinggmaps;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by garyliton on 2018-03-15.
 */

public class User {
    private String fname;
    private String lname;
    private  String uid;
    //private FirebaseUser id;

    public User(){

    }

    public User( String fname, String lname, String uid){
        //this.id = id;
        this.uid = uid;
        this.fname = fname;
        this.lname = lname;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public String getUid() {
        return uid;
    }
//    public FirebaseUser getId() {
//        return id;
//    }
}
