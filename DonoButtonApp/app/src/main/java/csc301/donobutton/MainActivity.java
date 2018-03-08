package csc301.donobutton;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnClickListener  {
    List<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout ll = (LinearLayout)findViewById(R.id.vodmenu_scroll);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);

        list = new ArrayList<String>();
        //list.add("101676864RR0001");
        //list.add("101758282RR0001");
        //list.add("106843436RR0001");
        //list.add("106844244RR0001");
        //list.add("106914294RR0001");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("");
        firebaseListener(myRef);

        Log.d("MSG", String.valueOf(list.size()));

        for (int i = 0; i < 936; i+=1) {
            Button charButton = new Button(this);
            charButton.setId(i);
            //charButton.setText(list.get(i));
            ll.addView(charButton, lp);
            charButton.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Button b = (Button)v;

        String buttonText = b.getText().toString();
        CharityPage.id = buttonText;
        Intent startNewActivity = new Intent(this, CharityPage.class);
        startActivity(startNewActivity);
    }

    private void firebaseListener (DatabaseReference databaseReference) {
        // Read from the database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int i = 0;
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        //list.add(d.getKey());
                        Button btn = (Button) findViewById(i);
                        btn.setText(d.getKey());
                        i++;
                        //Log.d("MSG", "Value is: " + d.getKey());
                    }
                }
                Log.d("MSG", String.valueOf(list.size()));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("FAIL", "Failed to read value.", error.toException());
            }
        });
    }
}