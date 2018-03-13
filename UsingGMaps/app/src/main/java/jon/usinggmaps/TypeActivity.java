package jon.usinggmaps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class TypeActivity extends AppCompatActivity {
    private boolean charitiesSelected;
    private boolean eventsSelected;
    private View nextButton;
    private Button charityButton;
    private Button eventsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
        charitiesSelected = false;
        eventsSelected = false;

        charityButton =  findViewById(R.id.charitiesButton);
        eventsButton =  findViewById(R.id.eventsButton);
        nextButton = findViewById(R.id.nextButton);


        turnOnNext();
    }

    public void onCharities(View view){
        charitiesSelected = !charitiesSelected;
        if(charitiesSelected){
            charityButton.setText("✔");
        }
        else{
            charityButton.setText(R.string.Charities);
        }
        turnOnNext();

    }

    public void onEvents(View view){
        eventsSelected = !eventsSelected;
        if(eventsSelected){
            eventsButton.setText("✔");
        }
        else{
            eventsButton.setText(R.string.Events);
        }
        turnOnNext();

    }
    public void onNext(View view){
        Intent goTypes = new Intent(this, CharityTypeActivity.class);
        goTypes.putExtra("charitiesSelected", charitiesSelected);
        goTypes.putExtra("eventsSelected",eventsSelected);
        startActivity(goTypes);
    }



    private void turnOnNext(){
        if(charitiesSelected || eventsSelected){
            nextButton.setVisibility(View.VISIBLE);
        }else{
            nextButton.setVisibility(View.GONE);
        }


    }

}
