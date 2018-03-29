package jon.usinggmaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth firebaseAuth;
    TextView textViewUserEmail;
    Button buttonLogout;
//    Button buttonApp;

    EditText editTextFname;
    EditText editTextLname;
    Button buttonUpdate;

    DatabaseReference databaseReferenceUsers;
    DatabaseReference databaseReference;
    String currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null){
            // user is not logged in
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        this.currentUser = firebaseAuth.getCurrentUser().getUid();

        databaseReferenceUsers = FirebaseDatabase.getInstance().getReference("users");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        Query query = databaseReference.child("users").orderByChild("uid").equalTo(this.currentUser);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot d : dataSnapshot.getChildren()){
                    editTextFname.setText(d.child("fname").getValue().toString());
//                    editTextFname.setText(firebaseAuth.getCurrentUser().getDisplayName());
                    editTextLname.setText(d.child("lname").getValue().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText("Welcome " + user.getEmail());

        buttonLogout = (Button) findViewById((R.id.buttonLogout));

//        buttonApp = (Button) findViewById(R.id.buttonApp);
//
//        buttonApp.setOnClickListener(this);

        buttonLogout.setOnClickListener(this);


        editTextFname = (EditText) findViewById(R.id.editTextFname);
        editTextLname = (EditText) findViewById(R.id.editTextLname);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        Log.d("profile", "yooo");

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doUpdate();
            }
        });



    }

    private void doUpdate(){
        String fname = editTextFname.getText().toString().trim();
        String lname = editTextLname.getText().toString().trim();


        if (!TextUtils.isEmpty(fname) && !TextUtils.isEmpty(lname)){
            User user = new User(fname, lname, this.currentUser);
            databaseReferenceUsers.child(this.currentUser).setValue(user);

            Toast.makeText(this, "Updated", Toast.LENGTH_LONG).show();

        }else {
            Toast.makeText(this, "Invalid", Toast.LENGTH_LONG).show();
        }
    }
    public void onBack(View view){
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

//        if (view == buttonApp){
//            finish();
//            startActivity(new Intent(this, MapsActivity.class));
//        }


    }
}